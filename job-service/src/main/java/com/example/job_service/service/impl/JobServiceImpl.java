package com.example.job_service.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.job_service.model.Job;
import com.example.job_service.repository.JobRepository;
import com.example.job_service.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<Job> findAllJob() {
        return jobRepository.findAll();
    }

    @Override
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public Job getJobById(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Override
    public boolean deleteById(Long id) {
        jobRepository.deleteById(id);
        return true;
    }

    @Override
    public boolean updateJob(Long id, Job updateJob) {
        Optional<Job> jobOptional = jobRepository.findById(id);
        if (jobOptional.isPresent()) {
            Job job = jobOptional.get();
            job.setTitle(updateJob.getTitle());
            job.setDescription(updateJob.getTitle());
            job.setMinSalary(updateJob.getTitle());
            job.setMaxSalary(updateJob.getTitle());
            job.setLocation(updateJob.getTitle());
            jobRepository.save(job);

            return true;

        }
        return false;
    }

}
