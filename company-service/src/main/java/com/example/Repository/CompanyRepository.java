package com.example.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Company;

public interface CompanyRepository extends JpaRepository <Company, Long> {

}
