package com.example.job_service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.job_service.external.Company;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/company/{id}")
    Company getCompanyById(@PathVariable("id") Long id);
}
