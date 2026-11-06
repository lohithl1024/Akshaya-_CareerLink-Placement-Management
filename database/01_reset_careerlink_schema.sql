DROP DATABASE IF EXISTS careerlink;
CREATE DATABASE careerlink CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE careerlink;

CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'RECRUITER', 'STUDENT') NOT NULL,
    enabled BIT(1) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE students (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    branch VARCHAR(100) NOT NULL,
    resume VARCHAR(255) NOT NULL,
    skills VARCHAR(1000) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_students_user (user_id),
    CONSTRAINT fk_students_user FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE companies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    name VARCHAR(150) NOT NULL,
    industry VARCHAR(100) NOT NULL,
    website VARCHAR(255) NOT NULL,
    location VARCHAR(120) NOT NULL,
    description VARCHAR(1500) NOT NULL,
    created_by BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_companies_name (name),
    KEY idx_companies_created_by (created_by),
    CONSTRAINT fk_companies_created_by FOREIGN KEY (created_by) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE jobs (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    title VARCHAR(120) NOT NULL,
    description VARCHAR(3000) NOT NULL,
    eligibility VARCHAR(1000) NOT NULL,
    eligibility_criteria VARCHAR(1000) NOT NULL,
    location VARCHAR(120) NOT NULL,
    salary_package VARCHAR(50) NOT NULL,
    application_deadline DATE NOT NULL,
    active BIT(1) NOT NULL,
    company_id BIGINT NOT NULL,
    recruiter_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_jobs_company (company_id),
    KEY idx_jobs_recruiter (recruiter_id),
    CONSTRAINT fk_jobs_company FOREIGN KEY (company_id) REFERENCES companies (id),
    CONSTRAINT fk_jobs_recruiter FOREIGN KEY (recruiter_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE applications (
    id BIGINT NOT NULL AUTO_INCREMENT,
    created_at DATETIME(6) DEFAULT NULL,
    updated_at DATETIME(6) DEFAULT NULL,
    status ENUM('APPLIED', 'IN_REVIEW', 'SHORTLISTED', 'REJECTED', 'SELECTED') NOT NULL,
    student_id BIGINT NOT NULL,
    job_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_applications_student_job (student_id, job_id),
    KEY idx_applications_job (job_id),
    CONSTRAINT fk_applications_student FOREIGN KEY (student_id) REFERENCES students (id),
    CONSTRAINT fk_applications_job FOREIGN KEY (job_id) REFERENCES jobs (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
