package com.Project.ResumeBuilder.service;

import com.Project.ResumeBuilder.entity.Resume;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfExportService {

    private static final Font TITLE_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
    private static final Font HEADING_FONT = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font NORMAL_FONT = FontFactory.getFont(FontFactory.HELVETICA, 10);

    public byte[] exportResumeToPdf(Resume resume) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title
            String title = resume.getTitle() != null ? resume.getTitle() : "Resume";
            document.add(new Paragraph(title, TITLE_FONT));
            document.add(Chunk.NEWLINE);

            // Profile / Contact
            Resume.ProfileInfo profile = resume.getProfileInfo();
            Resume.ContactInfo contact = resume.getContactInfo();
            if (profile != null || contact != null) {
                if (profile != null && profile.getFullName() != null) {
                    document.add(new Paragraph(profile.getFullName(), HEADING_FONT));
                }
                if (contact != null) {
                    StringBuilder contactLine = new StringBuilder();
                    if (contact.getEmail() != null) contactLine.append(contact.getEmail());
                    if (contact.getPhone() != null) contactLine.append(" | ").append(contact.getPhone());
                    if (contact.getLocation() != null) contactLine.append(" | ").append(contact.getLocation());
                    if (contact.getLinkedin() != null) contactLine.append(" | ").append(contact.getLinkedin());
                    if (contact.getWebsite() != null) contactLine.append(" | ").append(contact.getWebsite());
                    if (contactLine.length() > 0) {
                        document.add(new Paragraph(contactLine.toString(), NORMAL_FONT));
                    }
                }
                if (profile != null && profile.getSummary() != null && !profile.getSummary().isEmpty()) {
                    document.add(Chunk.NEWLINE);
                    document.add(new Paragraph("Summary", HEADING_FONT));
                    document.add(new Paragraph(profile.getSummary(), NORMAL_FONT));
                }
                document.add(Chunk.NEWLINE);
            }

            // Work Experience
            List<Resume.WorkExperience> work = resume.getWorkExperience();
            if (work != null && !work.isEmpty()) {
                document.add(new Paragraph("Experience", HEADING_FONT));
                for (Resume.WorkExperience exp : work) {
                    String role = exp.getRole() != null ? exp.getRole() : "";
                    String company = exp.getCompany() != null ? " · " + exp.getCompany() : "";
                    String dates = buildDateRange(exp.getStartDate(), exp.getEndDate());
                    document.add(new Paragraph(role + company, NORMAL_FONT));
                    if (dates != null && !dates.isEmpty()) {
                        document.add(new Paragraph(dates, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9)));
                    }
                    if (exp.getDescription() != null && !exp.getDescription().isEmpty()) {
                        document.add(new Paragraph(exp.getDescription(), NORMAL_FONT));
                    }
                    document.add(Chunk.NEWLINE);
                }
            }

            // Education
            List<Resume.Education> education = resume.getEducation();
            if (education != null && !education.isEmpty()) {
                document.add(new Paragraph("Education", HEADING_FONT));
                for (Resume.Education ed : education) {
                    String degree = ed.getDegree() != null ? ed.getDegree() : "";
                    String institution = ed.getInstitution() != null ? " · " + ed.getInstitution() : "";
                    String dates = buildDateRange(ed.getStartDate(), ed.getEndDate());
                    document.add(new Paragraph(degree + institution, NORMAL_FONT));
                    if (dates != null && !dates.isEmpty()) {
                        document.add(new Paragraph(dates, FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9)));
                    }
                    document.add(Chunk.NEWLINE);
                }
            }

            // Skills
            List<Resume.Skill> skills = resume.getSkills();
            if (skills != null && !skills.isEmpty()) {
                document.add(new Paragraph("Skills", HEADING_FONT));
                StringBuilder skillLine = new StringBuilder();
                for (Resume.Skill s : skills) {
                    if (s.getName() != null) {
                        if (skillLine.length() > 0) skillLine.append(" · ");
                        skillLine.append(s.getName());
                    }
                }
                if (skillLine.length() > 0) {
                    document.add(new Paragraph(skillLine.toString(), NORMAL_FONT));
                }
                document.add(Chunk.NEWLINE);
            }

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Failed to export resume to PDF", e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage());
        }
    }

    private static String buildDateRange(String start, String end) {
        if (start == null && end == null) return "";
        if (start == null) return end != null ? end : "";
        if (end == null || end.isEmpty()) return start + " – Present";
        return start + " – " + end;
    }
}
