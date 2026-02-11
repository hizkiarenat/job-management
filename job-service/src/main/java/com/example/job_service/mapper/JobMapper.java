package com.example.job_service.mapper;

import com.example.job_service.dto.JobWithCompanyDTO;
import com.example.job_service.external.Company;
import com.example.job_service.model.Job;

public class JobMapper {

    public static JobWithCompanyDTO mapToJobWithCompanyDTO(
        Job job, 
        Company company
    ){
        JobWithCompanyDTO jobWithCompanyDTO = new JobWithCompanyDTO();
        jobWithCompanyDTO.setId(job.getId());
        jobWithCompanyDTO.setTitle(job.getTitle());
        jobWithCompanyDTO.setDescription(job.getDescription());
        jobWithCompanyDTO.setLocation(job.getLocation());
        jobWithCompanyDTO.setMaxSalary(job.getMaxSalary());
        jobWithCompanyDTO.setMinSalary(job.getMinSalary());
        jobWithCompanyDTO.setCompany(company);

        return jobWithCompanyDTO;
    }
}
