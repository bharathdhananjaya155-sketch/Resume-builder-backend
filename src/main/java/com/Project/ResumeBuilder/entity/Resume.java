package com.Project.ResumeBuilder.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "resumes")
@EntityListeners(AuditingEntityListener.class)
@DynamicUpdate
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private String title;
    private String thumbnailLink;

    @Embedded
    private Template template;

    @Embedded
    private ProfileInfo profileInfo=new ProfileInfo();

    @Embedded
    private ContactInfo contactInfo;

    @ElementCollection
    @CollectionTable(name = "resume_work_experience", joinColumns = @JoinColumn(name = "resume_id"))
    private List<WorkExperience> workExperience;

    @ElementCollection
    @CollectionTable(name = "resume_education", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Education> education;

    @ElementCollection
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Skill> skills;

    @ElementCollection
    @CollectionTable(name = "resume_projects", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Project> projects;

    @ElementCollection
    @CollectionTable(name = "resume_certifications", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Certification> certifications;

    @ElementCollection
    @CollectionTable(name = "resume_languages", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Language> languages;

    @ElementCollection
    @CollectionTable(name = "resume_interests", joinColumns = @JoinColumn(name = "resume_id"))
    @Column(name = "interest")
    private List<String> interests;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Template {
        private String theme;
        @ElementCollection
        private List<String> palette;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProfileInfo {
        private String profilePreviewUrl;
        private String fullName;
        private String designation;
        @Column(columnDefinition = "TEXT")
        private String summary;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ContactInfo {
        private String email;
        private String phone;
        private String location;
        private String linkedin;
        private String github;
        private String website;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WorkExperience {
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        @Column(columnDefinition = "TEXT")
        private String description;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Education {
        private String degree;
        private String institution;
        private String startDate;
        private String endDate;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Skill {
        private String name;
        private Integer progress;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Project {
        private String title;
        @Column(columnDefinition = "TEXT")
        private String description;
        private String githubLink;
        private String liveDemo;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Certification {
        private String title;
        private String issuer;
        private String year;
    }

    @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Language {
        private String name;
        private Integer progress;
    }
}
