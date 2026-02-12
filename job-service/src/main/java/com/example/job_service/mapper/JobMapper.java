package com.example.job_service.mapper;

import java.util.List;

import com.example.job_service.dto.JobDTO;
import com.example.job_service.external.Company;
import com.example.job_service.external.Review;
import com.example.job_service.model.Job;

public class JobMapper {

    public static JobDTO mapToJobWithCompanyDTO(
        Job job, 
        Company company,
        List<Review> review
    ){
        JobDTO jobWithCompanyDTO = new JobDTO();
        jobWithCompanyDTO.setId(job.getId());
        jobWithCompanyDTO.setTitle(job.getTitle());
        jobWithCompanyDTO.setDescription(job.getDescription());
        jobWithCompanyDTO.setLocation(job.getLocation());
        jobWithCompanyDTO.setMaxSalary(job.getMaxSalary());
        jobWithCompanyDTO.setMinSalary(job.getMinSalary());
        jobWithCompanyDTO.setCompany(company);
        jobWithCompanyDTO.setReview(review);

        return jobWithCompanyDTO;
    }
}
