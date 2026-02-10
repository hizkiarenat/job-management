package com.example.job_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.job_service.model.Job;

public interface JobRepository extends JpaRepository<Job, Long> {

}
