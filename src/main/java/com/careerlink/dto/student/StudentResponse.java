package com.careerlink.dto.student;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {

    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String branch;
    private String skills;
    private String resume;
    private Integer graduationYear;
    private BigDecimal cgpa;
    private Integer activeBacklogs;
}
