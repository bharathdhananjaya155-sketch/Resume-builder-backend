package com.Project.ResumeBuilder.repositary;

import com.Project.ResumeBuilder.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResumeRepository extends JpaRepository<Resume, Integer> {


    List<Resume> findByUserIdOrderByUpdatedAtDesc(Integer userid);


    Optional<Resume> findByUserIdAndId(Integer id, Integer resumeId);
}
