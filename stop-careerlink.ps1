param(
    [int]$Port = 8080
)

$ErrorActionPreference = "Stop"

$listener = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction SilentlyContinue |
    Select-Object -First 1

if ($null -eq $listener) {
    Write-Host "No active process is listening on port $Port." -ForegroundColor Yellow
    exit 0
}

Stop-Process -Id $listener.OwningProcess -Force
Write-Host "Stopped process $($listener.OwningProcess) on port $Port." -ForegroundColor Green
