package com.Project.ResumeBuilder.service;

import com.Project.ResumeBuilder.dto.AuthResponse;
import com.Project.ResumeBuilder.entity.Resume;
import com.Project.ResumeBuilder.repositary.ResumeRepository;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String,Object> imageUploadResult =cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap("resource_type","image"));
        log.info("Inside FileUploadService- uploadSingleImage() {}",imageUploadResult.get("secure_url").toString());

        return Map. of( "image_url", imageUploadResult.get("secure_url").toString());
    }

    public Map<String, String> uploadResumeImages(Integer resumeId,
                                                  Object principle,
                                                  MultipartFile thumbnail,
                                                  MultipartFile profileImage) throws IOException {

        AuthResponse response = authService.getProfile(principle);

        Resume existingResume = resumeRepository.findByUserIdAndId(response.getId(), resumeId)
                .orElseThrow(() -> new RuntimeException("resume not found"));


        Map<String, String> returnValue = new HashMap<>();
        Map<String, String> uploadResult=null;

        if (Objects.nonNull(thumbnail)) {
            uploadResult = uploadSingleImage(thumbnail);
            String thumbnailUrl = uploadResult.get("image_url");
            existingResume.setThumbnailLink(thumbnailUrl);
            returnValue.put("thumbnailLink", thumbnailUrl);
        }

        if (Objects.nonNull(profileImage)) {
            uploadResult = uploadSingleImage(profileImage);
            String profileUrl = uploadResult.get("image_url");

            if (Objects.isNull(existingResume.getProfileInfo())) {
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePreviewUrl(profileUrl);
            returnValue.put("profilePreviewUrl",profileUrl);
        }

        resumeRepository.save(existingResume);
        returnValue.put("message", "images uploaded successfully");

        return returnValue;
    }
}

