package com.example.job_service.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    private static final Logger logger = LoggerFactory.getLogger(JobServiceImpl.class);

    private final CompanyClient companyClient;

    private final JobRepository jobRepository;

    private final ReviewClient reviewClient;

    public JobServiceImpl(CompanyClient companyClient, JobRepository jobRepository, ReviewClient reviewClient) {
        this.companyClient = companyClient;
        this.jobRepository = jobRepository;
        this.reviewClient = reviewClient;
    }

    /**
     * Get all jobs (CACHED for 10 minutes)
     * Cache key: "jobs::all"
     */
    @Override
    @CircuitBreaker(name = "companyBreaker", fallbackMethod = "companyBreakerFallback")
    @Cacheable(value = "jobs", key = "'all'")
    public List<JobDTO> findAllJob() {
        logger.info("Fetching all jobs from database(CACHE MISS)");
        List<Job> jobs = jobRepository.findAll();
        return jobs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    /**
     * Create job (CLEAR ALL CACHE)
     * Why clear "all"? Because job list changed
     */
    @Override
    @CacheEvict(value = "jobs", key = "'all'")
    public Job createJob(Job job) {
        logger.info("Creating new job (CLEARING CACHE for 'all')");
        if (job == null) {
            throw new IllegalArgumentException("Job cannot be null");
        }
        return jobRepository.save(job);
    }

    /**
     * Get job by ID (CACHED for 10 minutes)
     * Cache key: "jobs::1", "jobs::2", etc
     */
    @Override
    @Cacheable(value = "jobs", key = "#id")
    public JobDTO getJobById(Long id) {
        logger.info("Fetching job {} from database (CACHE MISS)", id);
        Job job = jobRepository.findById(id).orElse(null);
        return convertToDto(job);
    }

    @Override
    public boolean deleteById(Long id) {
        jobRepository.deleteById(id);
        return true;
    }

    /**
     * Update job (UPDATE CACHE)
     * @CachePut: Update cache with new value
     */
    @Override
    @CachePut(value = "jobs", key = "#id")
    @CacheEvict(value = "jobs", key = "'all'")
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
