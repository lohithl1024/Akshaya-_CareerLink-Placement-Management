package com.careerlink.entity;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"company", "recruiter", "applications"})
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false, length = 3000)
    private String description;

    @Column(nullable = false, length = 1000)
    private String eligibility;

    @Column(name = "eligibility_criteria", nullable = false, length = 1000)
    private String eligibilityCriteria;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false, length = 50)
    private String salaryPackage;

    @Column(nullable = false)
    private LocalDate applicationDeadline;

    @Column(nullable = false)
    private boolean active;

    @Column(length = 500)
    private String eligibleBranches;

    @Column(precision = 4, scale = 2)
    private BigDecimal minimumCgpa;

    private Integer graduationYear;

    private Integer maximumActiveBacklogs;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recruiter_id", nullable = false)
    private User recruiter;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();
}
