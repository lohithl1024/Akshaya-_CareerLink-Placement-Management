param(
    [int]$Port = 8080
)

$ErrorActionPreference = "Stop"
Set-Location -LiteralPath $PSScriptRoot

function Write-Step {
    param([string]$Message)
    Write-Host ""
    Write-Host "==> $Message" -ForegroundColor Cyan
}

function Get-MySqlService {
    Get-Service |
        Where-Object { $_.Name -match "MySQL" -or $_.DisplayName -match "MySQL" } |
        Select-Object -First 1
}

function Get-MavenRepositoryPath {
    $defaultRepository = Join-Path $HOME ".m2\repository"

    try {
        if (-not (Test-Path -LiteralPath $defaultRepository)) {
            New-Item -ItemType Directory -Path $defaultRepository -Force | Out-Null
        }

        return $defaultRepository
    } catch {
        $projectRepository = Join-Path $PSScriptRoot ".maven-repo"

        if (-not (Test-Path -LiteralPath $projectRepository)) {
            New-Item -ItemType Directory -Path $projectRepository -Force | Out-Null
        }

        return $projectRepository
    }
}

$existingListener = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
    Select-Object -First 1

if ($existingListener) {
    throw "Port $Port is already in use by PID $($existingListener.OwningProcess). Stop it first or run .\run-careerlink.ps1 -Port 8081"
}

$mysqlService = Get-MySqlService

if ($null -eq $mysqlService) {
    Write-Warning "No Windows MySQL service was found automatically. Ensure MySQL is already running before starting CareerLink."
} else {
    if ($mysqlService.Status -ne "Running") {
        Write-Step "Starting MySQL service: $($mysqlService.Name)"
        try {
            Start-Service -Name $mysqlService.Name -ErrorAction Stop
        } catch {
            throw "MySQL service '$($mysqlService.Name)' is not running and could not be started automatically. Open PowerShell as Administrator and run: Start-Service -Name $($mysqlService.Name)"
        }
    } else {
        Write-Step "MySQL service is already running: $($mysqlService.Name)"
    }
}

Write-Step "Preparing Maven"
$mavenRepository = Get-MavenRepositoryPath

Write-Step "Starting CareerLink on http://localhost:$Port"
$mavenArguments = @("-Dmaven.repo.local=$mavenRepository", "spring-boot:run")

if ($Port -ne 8080) {
    $mavenArguments += "-Dspring-boot.run.arguments=--server.port=$Port"
}

& mvn @mavenArguments
exit $LASTEXITCODE
