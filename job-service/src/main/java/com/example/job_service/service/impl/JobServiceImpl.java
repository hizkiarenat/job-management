package com.example.job_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.job_service.client.CompanyClient;
import com.example.job_service.client.ReviewClient;
import com.example.job_service.dto.JobDTO;
import com.example.job_service.external.Company;
import com.example.job_service.external.Review;
import com.example.job_service.mapper.JobMapper;
import com.example.job_service.model.Job;
import com.example.job_service.repository.JobRepository;
import com.example.job_service.service.JobService;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class JobServiceImpl implements JobService {

    private final CompanyClient companyClient;

    private final JobRepository jobRepository;

    private final ReviewClient reviewClient;

    public JobServiceImpl(CompanyClient companyClient, JobRepository jobRepository, ReviewClient reviewClient) {
        this.companyClient = companyClient;
        this.jobRepository = jobRepository;
        this.reviewClient = reviewClient;
    }

    @Override
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
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
            job.setDescription(updateJob.getDescription());
            job.setMinSalary(updateJob.getMinSalary());
            job.setMaxSalary(updateJob.getMaxSalary());
            job.setLocation(updateJob.getLocation());
            jobRepository.save(job);

            return true;

        }
        return false;
    }

    private JobDTO convertToDto(Job job) {
        // call company-service with openFeign
        Company company = companyClient.getCompanyById(job.getCompanyId());

        // call review-service with openFeign
        List<Review> reviewResponse = reviewClient.getReview(job.getCompanyId());

        JobDTO jobWithCompanyDTO = JobMapper.mapToJobWithCompanyDTO(job, company, reviewResponse);
        return jobWithCompanyDTO;

    }

    private List<String> companyBreakerFallback() {
        List<String> list = new ArrayList<>();
        list.add("Services is down, wait a minute...");
        return list;
    }
}
