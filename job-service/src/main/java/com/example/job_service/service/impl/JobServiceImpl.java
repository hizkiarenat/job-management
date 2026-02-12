package com.example.job_service.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.job_service.dto.JobDTO;
import com.example.job_service.external.Company;
import com.example.job_service.external.Review;
import com.example.job_service.mapper.JobMapper;
import com.example.job_service.model.Job;
import com.example.job_service.repository.JobRepository;
import com.example.job_service.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    RestTemplate restTemplate;

    JobRepository jobRepository;

    public JobServiceImpl(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public List<JobDTO> findAllJob() {
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    public Job createJob(Job job) {
        return jobRepository.save(job);
    }

    @Override
    public JobDTO getJobById(Long id) {
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDto(job);
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

    private JobDTO convertToDto(Job job) {
        Company company = restTemplate.getForObject("http://company-service:2000/company/" + job.getCompanyId(),
                Company.class);
        ResponseEntity<List<Review>> reviewResponse = restTemplate
                .exchange("http://review-service:3000/reviews?companyId=" + job.getCompanyId(), 
                HttpMethod.GET, 
                null, 
                new ParameterizedTypeReference<List<Review>>(){
                });
        List<Review> reviews = reviewResponse.getBody();
        JobDTO jobWithCompanyDTO = JobMapper.mapToJobWithCompanyDTO(job, company, reviews);
        
        return jobWithCompanyDTO;

    }
}
