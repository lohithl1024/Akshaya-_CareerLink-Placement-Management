# CareerLink - Smart Campus Placement Management System

## Major Project Report

**Submitted to:** [Replace with your college / university name]  
**Department:** [Replace with your department name]  
**Submitted by:** [Your Name]  
**Roll Number:** [Your Roll Number]  
**Registration Number:** [Your Registration Number]  
**Guided by:** [Guide Name]  
**Academic Year:** 2025-2026

---

## Bonafide Certificate

This is to certify that the project entitled **"CareerLink - Smart Campus Placement Management System"** is a bona fide record of the work carried out by **[Your Name]** under my supervision in partial fulfillment of the requirements for the award of the degree of **Bachelor of Technology / Bachelor of Engineering** in **[Department Name]** during the academic year **2025-2026**.

The work presented in this report is original and has not been submitted elsewhere for the award of any degree or diploma.

**Project Guide:** [Guide Name]  
**Head of Department:** [HOD Name]  
**External Examiner:** [Examiner Name]  
**Date:** [Insert Date]  
**Place:** [Insert Place]

---

## Declaration

I hereby declare that the project report entitled **"CareerLink - Smart Campus Placement Management System"** is an original work carried out by me under the guidance of **[Guide Name]**. This work has not previously formed the basis for the award of any degree, diploma, or similar title by any other institution.

**Student Signature:** ____________________

---

## Acknowledgement

I express my sincere gratitude to **[Guide Name]** for continuous support, valuable suggestions, and technical guidance throughout the development of this project. I also thank the Head of Department, faculty members, and all those who provided the necessary academic environment and encouragement. Finally, I thank my family and friends for their constant support during the successful completion of this major project.

---

## Abstract

**CareerLink - Smart Campus Placement Management System** is a secure full stack web application developed to automate and modernize the campus recruitment process. Traditional placement management in many institutions depends on spreadsheets, messaging groups, emails, and manual coordination. This causes scattered information, inconsistent records, poor visibility of opportunities, delayed communication, and weak control over user access. CareerLink addresses these issues through a centralized, role-based platform designed specifically for administrators, recruiters, and students.

The project uses **Spring Boot** for the backend, **Spring Security with JWT** for authentication and authorization, **BCrypt** for password encryption, **Spring Data JPA with Hibernate** for persistence, **MySQL** for relational data storage, and a professional **HTML, CSS, and JavaScript frontend** for user interaction. The backend is organized using a clean layered architecture with **Controller -> Service -> Repository -> Entity** flow. DTOs are used for API communication, validation annotations enforce field constraints, and a global exception handler ensures consistent error responses.

The system supports five major modules: user authentication, student profile management, company management, job posting, and job application management. The final implementation includes a realistic demonstration dataset containing **15 users**, **6 companies**, **10 jobs**, and **12 applications**, with fully verified login accounts for admin, recruiter, and student roles. The project demonstrates secure workflow management, professional frontend integration, and a production-style backend design suitable for a final year academic submission.

---

## Table of Contents

1. Chapter I - Introduction  
2. Chapter II - Literature Survey  
3. Chapter III - System Design  
4. Chapter IV - Methodology and Project Interface Outline  
5. Chapter V - Limitations  
6. Chapter VI - Conclusions and Future Scope  
7. References  

---

## List of Figures

1. High-Level Architecture of CareerLink  
2. Entity Relationship Diagram  
3. Role-Based Access Design  
4. Login and Registration Interface  
5. Student Dashboard Interface  
6. Recruiter Dashboard Interface  
7. Job Listings Page  
8. Application Tracking Page  

---

## List of Tables

1. Technology Stack and Development Tools  
2. Comparative Analysis of Existing Systems  
3. Functional and Non-Functional Requirements  
4. Database Schema Summary  
5. Module Implementation Mapping  
6. Testing and Validation Summary  
7. Seed Dataset Overview  
8. Future Enhancement Roadmap  

---

## List of Abbreviations

| Abbreviation | Meaning |
| --- | --- |
| API | Application Programming Interface |
| BCrypt | Adaptive password hashing algorithm |
| CRUD | Create, Read, Update, Delete |
| DTO | Data Transfer Object |
| HTML | HyperText Markup Language |
| JPA | Java Persistence API |
| JWT | JSON Web Token |
| ORM | Object Relational Mapping |
| RBAC | Role-Based Access Control |
| REST | Representational State Transfer |
| SQL | Structured Query Language |
| UI | User Interface |

---

# Chapter I - Introduction

## 1.1 Background of the Project

Campus placement is a critical bridge between academic learning and professional employment. However, many colleges still manage placement-related information through disconnected channels such as spreadsheets, email threads, and messaging groups. These methods introduce challenges such as duplicated records, inconsistent eligibility checking, delayed updates, lack of audit trails, and unclear role boundaries.

CareerLink was designed as a centralized campus placement platform to solve these problems. It provides a secure and structured environment where students can maintain profiles and apply for jobs, recruiters can publish roles and review applications, and administrators can manage companies and overall platform activity.

### 1.1.1 Need for the Project

The project is needed because existing placement operations in academic institutions often suffer from:

- fragmented student and company data
- lack of secure role-based access
- repeated manual updates
- limited tracking of application progress
- inconsistent communication among stakeholders

### 1.1.2 Problem Statement

There is a need for a professional web-based placement management system that can securely manage users, students, companies, jobs, and applications in one integrated environment while preserving data consistency and providing a clear user experience for all stakeholders.

### 1.1.3 Objectives

- To design and develop a campus placement management platform with admin, recruiter, and student roles.
- To implement secure authentication and authorization using JWT and BCrypt.
- To provide student profile management, company management, job posting, and application tracking.
- To build a maintainable backend using clean layered architecture.
- To integrate a simple but professional frontend with backend APIs.

## 1.2 Scope of the Project

The scope of CareerLink includes authentication, student profile maintenance, company creation, job publishing, job filtering, student job applications, and status tracking. The project is intended as a strong academic and demonstration system. Features such as email notifications, interview scheduling, analytics dashboards, and AI-based matching are not part of the current version but are suitable for future expansion.

## 1.3 Technology Stack and Development Tools

| Component | Technology | Purpose |
| --- | --- | --- |
| Backend | Spring Boot 4.0.3 | REST API and application framework |
| Language | Java 21 | Core business logic |
| Security | Spring Security, JWT, BCrypt | Secure authentication and role control |
| ORM | Spring Data JPA, Hibernate | Data persistence and mapping |
| Database | MySQL 8 | Relational data storage |
| Frontend | HTML, CSS, JavaScript | User interface |
| Build Tool | Maven | Build and dependency management |
| API Testing | Postman | Endpoint testing |
| IDE | VS Code | Development and debugging |

## 1.4 Major Features of CareerLink

- Secure login and registration
- Role-based dashboards
- Student profile management
- Company management
- Job posting and filtering
- Job application and status tracking
- Global exception handling
- Professional demonstration dataset

## 1.5 Organization of the Report

This report is divided into six chapters. Chapter I introduces the project and its objectives. Chapter II examines related systems and existing approaches. Chapter III discusses the system design and architecture. Chapter IV explains the development methodology, implementation, testing, and interface flow. Chapter V describes limitations, and Chapter VI presents the conclusion and future scope.

---

# Chapter II - Literature Survey

## 2.1 Existing Manual Placement Processes

Many colleges still manage placements through manual or semi-manual approaches. Student profiles are stored in spreadsheets, jobs are shared informally, and status tracking depends on repeated human intervention. Such methods are difficult to scale and are prone to errors and communication gaps.

## 2.2 Existing Digital Platforms

Existing digital platforms generally fall into three categories:

- basic college placement portals with limited features
- generic job portals intended for broad employment markets
- corporate recruitment systems focused on internal enterprise hiring

While these platforms solve parts of the problem, they do not fully address the academic placement workflow in a lightweight and institution-focused way.

## 2.3 Security and Data Handling Observations

Modern web applications require secure password storage, role-based authorization, validation, and token-based access control. Many simpler academic systems are weak in these areas. CareerLink adopts a better security model through JWT-based authentication and BCrypt password hashing.

## 2.4 Comparative Analysis

| Criteria | Manual Process | Basic Portal | Generic Job Portal | CareerLink |
| --- | --- | --- | --- | --- |
| Student Profile Management | Manual | Limited | Generic | Structured and placement-specific |
| Recruiter Workflow | Informal | Partial | Broad market-focused | Role-based and controlled |
| Application Tracking | Weak | Limited | Generic | Full status tracking |
| Security | Low | Moderate | High | JWT + BCrypt + RBAC |
| Data Visibility | Scattered | Moderate | High but generic | Centralized and academic-focused |

## 2.5 Literature Survey Summary

The survey shows that a specialized academic placement portal should combine the usability of professional job platforms with the structure and access control required by educational institutions. CareerLink is designed to satisfy this need with a campus-centered data model and secure web architecture.

---

# Chapter III - System Design

## 3.1 Requirement Analysis

| Requirement Type | Description |
| --- | --- |
| Functional | User registration and login |
| Functional | Company and job management |
| Functional | Student profile update |
| Functional | Job viewing, filtering, and applying |
| Functional | Application status update and tracking |
| Non-Functional | Security with JWT and BCrypt |
| Non-Functional | Clean architecture and maintainability |
| Non-Functional | Structured MySQL database |
| Non-Functional | Professional and responsive UI |

## 3.2 High-Level Architecture

The project follows a layered architecture. The frontend sends requests through the fetch API to Spring Boot REST controllers. Security filters validate JWT tokens before protected requests are processed. Service classes apply business rules, repository interfaces perform data access through JPA, and Hibernate maps entities to MySQL tables.

**Figure Placeholder:** Insert the architecture diagram showing Browser -> Security Layer -> Controllers -> Services -> Repositories -> MySQL.

## 3.3 Database Design

The database has been rebuilt cleanly and contains only the current project tables.

| Table | Purpose | Important Columns |
| --- | --- | --- |
| users | Stores identity and role data | id, name, email, password, role, enabled |
| students | Stores student profile details | id, user_id, branch, skills, resume |
| companies | Stores company information | id, name, industry, website, location, description |
| jobs | Stores placement opportunities | id, title, description, eligibility, location, salary_package |
| applications | Stores student applications | id, student_id, job_id, status |

Relationships:

- One `User` can have one `Student` profile.
- One `Company` can have many `Job` records.
- One `Job` can receive many `Application` records.
- One `Student` can submit many `Application` records.
- One `Application` links exactly one student and one job.

**Figure Placeholder:** Insert ER diagram here.

## 3.4 Security and Access Control Design

Security is implemented using Spring Security, JWT, and BCrypt.

- JWT tokens are generated after successful login.
- Protected API calls require `Authorization: Bearer <token>`.
- Passwords are stored in hashed form.
- Access is controlled according to role.

Role permissions:

- `ADMIN`: manage companies, oversee data, platform-level control
- `RECRUITER`: post jobs and manage job-related workflows
- `STUDENT`: maintain profile, browse jobs, apply, and track applications

## 3.5 Interface and Module Design

The frontend contains focused pages for different workflows:

- Login / Register
- Student Dashboard
- Recruiter Dashboard
- Job Listings
- Applications

These pages are connected to the backend through fetch-based API calls and dynamic rendering logic.

---

# Chapter IV - Methodology and Project Interface Outline

## 4.1 Development Methodology

The project followed an iterative development model. Each major module was designed, implemented, tested, and integrated in stages:

1. requirement definition  
2. entity and database design  
3. authentication and security implementation  
4. business module development  
5. frontend integration  
6. database rebuild and professional seed data creation  
7. validation and presentation preparation  

## 4.2 Backend Implementation

The backend is organized using clean layers:

- **Controller Layer:** accepts HTTP requests and returns JSON responses
- **Service Layer:** contains business rules and transactional logic
- **Repository Layer:** interacts with the database using Spring Data JPA
- **Entity Layer:** maps the relational schema using JPA annotations

Key backend modules:

- `AuthController` and `AuthServiceImpl`
- `StudentController` and `StudentServiceImpl`
- `CompanyController` and `CompanyServiceImpl`
- `JobController` and `JobServiceImpl`
- `ApplicationController` and `ApplicationServiceImpl`

## 4.3 Frontend Implementation

The frontend uses plain HTML, CSS, and JavaScript. It communicates with the backend using the fetch API. JWT tokens are stored in local storage and attached automatically to protected requests. The UI was refined to present a more enterprise-style appearance with better structure, shorter copy, and clearer dashboard sections.

**Figure Placeholders:**

- Login and registration page screenshot
- Student dashboard screenshot
- Recruiter dashboard screenshot
- Job listings page screenshot
- Applications page screenshot

## 4.4 Testing and Validation

| Test Case | Expected Result | Status |
| --- | --- | --- |
| Build the project | Project compiles successfully | Passed |
| Start backend | Application starts successfully | Passed |
| Admin login | Valid token and admin role | Passed |
| Recruiter login | Valid token and recruiter role | Passed |
| Student login | Valid token and student role | Passed |
| View jobs | Student sees available jobs | Passed |
| View applications | Student sees personal application data | Passed |
| View companies | Admin sees company directory | Passed |
| Role restriction | Unauthorized actions are blocked | Passed |

## 4.5 Deployment and Execution Procedure

Environment requirements:

- Java 21
- Maven
- MySQL running locally
- Configured database credentials

Execution steps:

1. Start MySQL.
2. Run the schema reset script if a fresh rebuild is needed.
3. Run the approved seed SQL file.
4. Start the project using `mvn spring-boot:run`.
5. Open `http://localhost:8080`.

### Seed Dataset Overview

| Category | Count |
| --- | --- |
| Users | 15 |
| Students | 8 |
| Companies | 6 |
| Jobs | 10 |
| Applications | 12 |

---

# Chapter V - Limitations

## 5.1 Functional Limitations

The current version does not support interview scheduling, multi-round candidate assessment, offer letter generation, or recruiter feedback workflows beyond status updates.

## 5.2 Technical Limitations

The system is designed primarily for local or controlled deployment. It does not yet include distributed caching, advanced pagination, or cloud-native deployment pipelines.

## 5.3 Security and Operational Limitations

Although the current security model is strong for an academic project, the application does not yet implement password reset through email, token revocation tracking, audit logs, or two-factor authentication.

## 5.4 Maintenance and Usability Constraints

The frontend is intentionally framework-free, which keeps it simple and transparent but limits advanced component-level state management. The final submission still requires the student to replace personal placeholders and insert actual screenshots in the document.

---

# Chapter VI - Conclusions and Future Scope

## 6.1 Conclusion

CareerLink successfully demonstrates a secure, professional, and maintainable campus placement management platform. It centralizes the complete flow of student profiles, company information, job opportunities, and application tracking while applying role-based security and a clean backend structure.

## 6.2 Project Outcomes

- Full stack placement platform completed
- Professional frontend integrated with backend APIs
- JWT-based authentication implemented
- BCrypt-based password protection implemented
- Realistic professional demonstration dataset created
- Database rebuilt cleanly for the final project version
- Verified login and workflow testing completed

## 6.3 Future Scope

| Enhancement Area | Proposed Improvement |
| --- | --- |
| Resume Management | Actual file upload and secure storage |
| Notifications | Email / SMS / push alerts |
| Analytics | Placement statistics dashboard |
| Interview Workflow | Schedule and feedback modules |
| AI Matching | Skill-based recommendation engine |
| Cloud Deployment | Containerization and CI/CD |
| Advanced Security | MFA, audit logs, password reset |

CareerLink can be extended into a larger institutional product by adding these modules while preserving the current clean architectural foundation.

---

# References

1. Spring Boot Official Documentation  
2. Spring Security Reference Documentation  
3. Hibernate ORM Documentation  
4. MySQL Reference Manual  
5. Oracle Java Documentation  
6. OWASP Security Guidelines  
7. REST architectural principles and secure API development resources  

---

## Appendix - Presentation Login Accounts

### Admin

- `admin@careerlink.com` / `Admin@CareerLink2026`

### Recruiters

- `recruiter.microsoft@careerlink.com` / `Recruiter@CareerLink2026`
- `recruiter.amazon@careerlink.com` / `Recruiter@CareerLink2026`
- `recruiter.deloitte@careerlink.com` / `Recruiter@CareerLink2026`
- `recruiter.infosys@careerlink.com` / `Recruiter@CareerLink2026`
- `recruiter.accenture@careerlink.com` / `Recruiter@CareerLink2026`
- `recruiter.tcs@careerlink.com` / `Recruiter@CareerLink2026`

### Students

- `ananya.gupta@careerlink.com` / `Student@CareerLink2026`
- `rohan.verma@careerlink.com` / `Student@CareerLink2026`
- `sneha.iyer@careerlink.com` / `Student@CareerLink2026`
- `aditya.rao@careerlink.com` / `Student@CareerLink2026`
- `meera.nair@careerlink.com` / `Student@CareerLink2026`
- `kunal.singh@careerlink.com` / `Student@CareerLink2026`
- `ishita.kapoor@careerlink.com` / `Student@CareerLink2026`
- `vivek.menon@careerlink.com` / `Student@CareerLink2026`

---

**Final Note:** Replace your personal details, guide information, institution details, and insert actual screenshots before final submission. This report is structured according to the template chapter flow and tailored specifically to the current CareerLink implementation.
