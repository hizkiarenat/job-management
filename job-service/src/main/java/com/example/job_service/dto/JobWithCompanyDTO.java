package com.example.job_service.dto;

import com.example.job_service.external.Company;
import com.example.job_service.model.Job;

public class JobWithCompanyDTO {

    private Job job;

    private Company company;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    
}
