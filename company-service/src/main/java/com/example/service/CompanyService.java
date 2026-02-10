package com.example.service;

import java.util.List;

import com.example.model.Company;

public interface CompanyService {

    List<Company> getAllCompanies();

    boolean updateCompany(Company company, Long id);

    void createCompany(Company company);

    boolean deleteCompany(Long id);

    Company getCompanyById(Long id);
}
