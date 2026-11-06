package com.careerlink.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.careerlink.dto.student.StudentProfileUpdateRequest;
import com.careerlink.dto.student.StudentRequest;
import com.careerlink.dto.student.StudentResponse;
import com.careerlink.entity.Student;
import com.careerlink.entity.User;
import com.careerlink.enums.Role;
import com.careerlink.exception.BadRequestException;
import com.careerlink.exception.ResourceNotFoundException;
import com.careerlink.repository.StudentRepository;
import com.careerlink.repository.UserRepository;
import com.careerlink.service.StudentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public StudentResponse createStudent(StudentRequest request) {
        User user = getStudentUser(request.getUserId());
        if (studentRepository.existsByUserId(user.getId())) {
            throw new BadRequestException("Student record already exists for this user");
        }

        Student student = new Student();
        applyStudentValues(student, request, user);

        return mapStudent(studentRepository.save(student));
    }

    @Override
    public List<StudentResponse> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::mapStudent)
                .toList();
    }

    @Override
    public StudentResponse getStudentById(Long studentId) {
        return mapStudent(getStudentEntity(studentId));
    }

    @Override
    public StudentResponse getOwnProfile(String email) {
        Student student = studentRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        return mapStudent(student);
    }

    @Override
    @Transactional
    public StudentResponse updateStudent(Long studentId, StudentRequest request) {
        Student student = getStudentEntity(studentId);
        User user = getStudentUser(request.getUserId());

        if (!student.getUser().getId().equals(user.getId()) && studentRepository.existsByUserId(user.getId())) {
            throw new BadRequestException("Student record already exists for this user");
        }

        applyStudentValues(student, request, user);
        return mapStudent(studentRepository.save(student));
    }

    @Override
    @Transactional
    public StudentResponse updateOwnProfile(String email, StudentProfileUpdateRequest request) {
        Student student = studentRepository.findByUserEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found"));
        student.setSkills(request.getSkills().trim());
        student.setResume(request.getResume().trim());
        student.setBranch(request.getBranch().trim());
        student.setGraduationYear(request.getGraduationYear());
        student.setCgpa(request.getCgpa());
        student.setActiveBacklogs(request.getActiveBacklogs());
        return mapStudent(studentRepository.save(student));
    }

    @Override
    @Transactional
    public void deleteStudent(Long studentId) {
        studentRepository.delete(getStudentEntity(studentId));
    }

    private User getStudentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (user.getRole() != Role.STUDENT) {
            throw new BadRequestException("Only users with STUDENT role can be linked to a student record");
        }
        return user;
    }

    private Student getStudentEntity(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));
    }

    private void applyStudentValues(Student student, StudentRequest request, User user) {
        student.setUser(user);
        student.setSkills(request.getSkills().trim());
        student.setResume(request.getResume().trim());
        student.setBranch(request.getBranch().trim());
        student.setGraduationYear(request.getGraduationYear());
        student.setCgpa(request.getCgpa());
        student.setActiveBacklogs(request.getActiveBacklogs());
    }

    private StudentResponse mapStudent(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .userId(student.getUser().getId())
                .name(student.getUser().getName())
                .email(student.getUser().getEmail())
                .branch(student.getBranch())
                .skills(student.getSkills())
                .resume(student.getResume())
                .graduationYear(student.getGraduationYear())
                .cgpa(student.getCgpa())
                .activeBacklogs(student.getActiveBacklogs())
                .build();
    }
}
