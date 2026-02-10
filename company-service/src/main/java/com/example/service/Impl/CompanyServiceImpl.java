package com.example.service.Impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.Repository.CompanyRepository;
import com.example.model.Company;
import com.example.service.CompanyService;

@Service
public class CompanyServiceImpl implements CompanyService {

    CompanyRepository companyRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    @Override
    public void createCompany(Company company) {
        companyRepository.save(company);
    }

    @Override
    public boolean deleteCompany(Long id) {
        companyRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    @Override
    public Company getCompanyById(Long id) {

        return companyRepository.findById(id).orElse(null);
    }

    @Override
    public boolean updateCompany(Company companyUpdate, Long id) {
        Optional<Company> companyOptional = companyRepository.findById(id);
        if (companyOptional.isPresent()) {
            Company company = companyOptional.get();
            company.setName(companyUpdate.getName());
            company.setDescription(companyUpdate.getDescription());
            companyRepository.save(company);
            
            return true;
        }
        return false;
    }

}
