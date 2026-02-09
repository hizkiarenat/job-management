package com.example.job_service.service;

import java.util.List;

import com.example.job_service.model.Job;

public interface JobService {

    public List<Job> findAllJob();

    public Job createJob(Job job);

    public Job getJobById(Long id);

    public boolean deleteById(Long id);

    public boolean updateJob(Long id, Job job);
}
