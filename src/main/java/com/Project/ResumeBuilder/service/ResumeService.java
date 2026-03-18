package com.Project.ResumeBuilder.service;

import com.Project.ResumeBuilder.dto.AuthResponse;
import com.Project.ResumeBuilder.dto.CreateResumeRequest;
import com.Project.ResumeBuilder.entity.Resume;
import com.Project.ResumeBuilder.repositary.ResumeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final AuthService authService;

    public Resume createResume( CreateResumeRequest request,  Object principal) {
        Resume newResume = new Resume();
        AuthResponse response = authService.getProfile(principal);

        newResume.setUserId(response.getId());
        newResume.setTitle(request.getTitle());

        setDefaultResumeData(newResume);

        return resumeRepository.save(newResume);

    }

    private void setDefaultResumeData(Resume resume) {
        resume.setProfileInfo(new Resume.ProfileInfo());
        resume.setContactInfo(new Resume.ContactInfo());
        resume.setWorkExperience(new ArrayList<>());
        resume.setEducation(new ArrayList<>());
        resume.setSkills(new ArrayList<>());
        resume.setProjects(new ArrayList<>());
        resume.setCertifications(new ArrayList<>());
        resume.setLanguages(new ArrayList<>());
        resume.setInterests(new ArrayList<>());
    }

    public List<Resume> getResume( Object principal) {

        AuthResponse response =authService.getProfile(principal);

        List<Resume> resumes=resumeRepository.findByUserIdOrderByUpdatedAtDesc(response.getId());
        return resumes;
    }

    public Resume getResumeById(Integer resumeId,Object principal) {

       AuthResponse response =authService.getProfile(principal);

       Resume resume =resumeRepository.findByUserIdAndId(response.getId(),resumeId)
               .orElseThrow(()-> new RuntimeException("Resume not Found"));
       return resume;
    }

    public Resume updateResume(Integer resumeId, Resume updatedData, Object principle) {
        AuthResponse response = authService.getProfile(principle);
        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        existingResume.setTitle(updatedData.getTitle());
        existingResume.setThumbnailLink(updatedData.getThumbnailLink());
        existingResume.setTemplate(updatedData.getTemplate());
        existingResume.setProfileInfo(updatedData.getProfileInfo());
        existingResume.setContactInfo(updatedData.getContactInfo());
        existingResume.setWorkExperience(updatedData.getWorkExperience());
        existingResume.setEducation(updatedData.getEducation());
        existingResume.setSkills(updatedData.getSkills());
        existingResume.setProjects(updatedData.getProjects());
        existingResume.setCertifications(updatedData.getCertifications());
        existingResume.setLanguages(updatedData.getLanguages());
        existingResume.setInterests(updatedData.getInterests());

        return resumeRepository.save(existingResume);
    }

    public void deleteResume(Integer resumeId, Object principal) {

        AuthResponse response =authService.getProfile(principal);

        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                        .orElseThrow(()->new RuntimeException("Resume not found"));

        resumeRepository.delete(existingResume);
    }
}
