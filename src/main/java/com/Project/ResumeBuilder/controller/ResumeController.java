package com.Project.ResumeBuilder.controller;


import com.Project.ResumeBuilder.dto.CreateResumeRequest;
import com.Project.ResumeBuilder.entity.Resume;
import com.Project.ResumeBuilder.service.FileUploadService;
import com.Project.ResumeBuilder.service.PdfExportService;
import com.Project.ResumeBuilder.service.ResumeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.Project.ResumeBuilder.util.AppConstant.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(RESUME_CONTROLLER)
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final FileUploadService fileUploadService;
    private final PdfExportService pdfExportService;

    @PostMapping
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request
            , Authentication authentication){
        Resume newResume =resumeService.createResume(request,authentication.getPrincipal());

        return  ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }

    @GetMapping
    public ResponseEntity<?> gerResume(Authentication authentication){
        List<Resume> resume =resumeService.getResume(authentication.getPrincipal());

        return ResponseEntity.ok(resume);
    }

    @GetMapping(ID)
    public ResponseEntity<?> getResumeById(@PathVariable Integer id,Authentication authentication){
        Resume existingResume =resumeService.getResumeById(id,authentication.getPrincipal());

        return ResponseEntity.ok(existingResume);
    }

    @PutMapping(ID)
    public ResponseEntity<?> updatedResume(@PathVariable Integer id,
                                           @RequestBody Resume updatedData,
                                           Authentication authentication){
      Resume updatedResume =resumeService.updateResume(id,updatedData,authentication.getPrincipal());

      return ResponseEntity.ok(updatedResume);
    }

    @PutMapping(UPLOAD_IMAGES)
    public ResponseEntity<Map<String, String>> uploadResumeImages(
            @PathVariable Integer id,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            Authentication authentication) throws IOException {

        log.info("Inside ResumeController uploadResumeImages method: {}", id);
        Map<String, String> response = fileUploadService.uploadResumeImages(id,
                authentication.getPrincipal(),
                thumbnail, profileImage);

        return ResponseEntity.ok(response);
    }

    @GetMapping(EXPORT_PDF)
    public ResponseEntity<byte[]> exportPdf(@PathVariable Integer id, Authentication authentication) {
        Resume resume = resumeService.getResumeById(id, authentication.getPrincipal());
        byte[] pdfBytes = pdfExportService.exportResumeToPdf(resume);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "resume.pdf");
        headers.setContentLength(pdfBytes.length);
        return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);
    }

    @DeleteMapping(ID)
    public ResponseEntity<?> deleteById(@PathVariable Integer id, Authentication authentication){

        resumeService.deleteResume(id,authentication.getPrincipal());

       return ResponseEntity.ok(Map.of("message","Resume Deleted"));
    }
}
