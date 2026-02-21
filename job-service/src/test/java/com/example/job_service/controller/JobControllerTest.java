package com.example.job_service.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.job_service.dto.JobDTO;
import com.example.job_service.external.Company;
import com.example.job_service.external.Review;
import com.example.job_service.model.Job;
import com.example.job_service.service.JobService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(JobController.class)
class JobControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private JobService jobService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Job testJob;
    private JobDTO testJobDTO;
    private Company testCompany;
    private Review testReview;
    
    @BeforeEach
    void setUp() {
        // Setup Job
        testJob = new Job();
        testJob.setId(1L);
        testJob.setTitle("Software Engineer");
        testJob.setDescription("Backend developer");
        testJob.setMinSalary("5000");
        testJob.setMaxSalary("8000");
        testJob.setLocation("Jakarta");
        testJob.setCompanyId(1L);
        
        // Setup Company
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Tech Corp");
        
        // Setup Review
        testReview = new Review();
        testReview.setId(1L);
        testReview.setTitle("Great company");
        testReview.setRating(4.5);
        
        // Setup JobDTO
        testJobDTO = new JobDTO();
        testJobDTO.setId(1L);
        testJobDTO.setTitle("Software Engineer");
        testJobDTO.setDescription("Backend developer");
        testJobDTO.setMinSalary("5000");
        testJobDTO.setMaxSalary("8000");
        testJobDTO.setLocation("Jakarta");
        testJobDTO.setCompany(testCompany);
        testJobDTO.setReview(Arrays.asList(testReview));
    }
    
    // ========== GET ALL JOBS ==========
    
    @Test
    void testFindAll_ReturnsListOfJobs() throws Exception {
        // GIVEN
        JobDTO jobDTO2 = new JobDTO();
        jobDTO2.setId(2L);
        jobDTO2.setTitle("DevOps Engineer");
        jobDTO2.setCompany(testCompany);
        
        List<JobDTO> mockJobs = Arrays.asList(testJobDTO, jobDTO2);
        when(jobService.findAllJob()).thenReturn(mockJobs);
        
        // WHEN & THEN
        mockMvc.perform(get("/jobs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].title").value("Software Engineer"))
            .andExpect(jsonPath("$[0].company.name").value("Tech Corp"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].title").value("DevOps Engineer"));
        
        verify(jobService, times(1)).findAllJob();
    }
    
    @Test
    void testFindAll_EmptyList() throws Exception {
        // GIVEN
        when(jobService.findAllJob()).thenReturn(List.of());
        
        // WHEN & THEN
        mockMvc.perform(get("/jobs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$.length()").value(0));
        
        verify(jobService, times(1)).findAllJob();
    }
    
    // ========== GET JOB BY ID ==========
    
    @Test
    void testFindJobsById_Found() throws Exception {
        // GIVEN
        when(jobService.getJobById(1L)).thenReturn(testJobDTO);
        
        // WHEN & THEN
        mockMvc.perform(get("/jobs/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Software Engineer"))
            .andExpect(jsonPath("$.description").value("Backend developer"))
            .andExpect(jsonPath("$.minSalary").value("5000"))
            .andExpect(jsonPath("$.maxSalary").value("8000"))
            .andExpect(jsonPath("$.location").value("Jakarta"))
            .andExpect(jsonPath("$.company.name").value("Tech Corp"))
            .andExpect(jsonPath("$.reviews[0].title").value("Great company"));
        
        verify(jobService, times(1)).getJobById(1L);
    }
    
    @Test
    void testFindJobsById_NotFound() throws Exception {
        // GIVEN
        when(jobService.getJobById(999L)).thenReturn(null);
        
        // WHEN & THEN
        mockMvc.perform(get("/jobs/999"))
            .andExpect(status().isNotFound());
        
        verify(jobService, times(1)).getJobById(999L);
    }
    
    // ========== CREATE JOB ==========
    
    @Test
    void testCreateJob_Success() throws Exception {
        // GIVEN
        Job newJob = new Job();
        newJob.setTitle("Backend Developer");
        newJob.setDescription("Java Spring Boot");
        newJob.setMinSalary("6000");
        newJob.setMaxSalary("9000");
        newJob.setLocation("Surabaya");
        newJob.setCompanyId(1L);
        
        when(jobService.createJob(any(Job.class))).thenReturn(testJob);
        
        // WHEN & THEN
        mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newJob)))
            .andExpect(status().isCreated())
            .andExpect(content().string("successfully addeed"));
        
        verify(jobService, times(1)).createJob(any(Job.class));
    }
    
    @Test
    void testCreateJob_InvalidJson() throws Exception {
        // GIVEN
        String invalidJson = "{ invalid json }";
        
        // WHEN & THEN
        mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
            .andExpect(status().isBadRequest());
        
        verify(jobService, never()).createJob(any(Job.class));
    }
    
    @Test
    void testCreateJob_EmptyBody() throws Exception {
        // WHEN & THEN
        mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isCreated());
        
        verify(jobService, times(1)).createJob(any(Job.class));
    }
    
    // ========== UPDATE JOB ==========
    
    @Test
    void testUpdateJobs_Success() throws Exception {
        // GIVEN
        Job updatedJob = new Job();
        updatedJob.setTitle("Senior Software Engineer");
        updatedJob.setDescription("Lead developer");
        updatedJob.setMinSalary("8000");
        updatedJob.setMaxSalary("12000");
        updatedJob.setLocation("Jakarta");
        
        when(jobService.updateJob(eq(1L), any(Job.class))).thenReturn(true);
        
        // WHEN & THEN
        mockMvc.perform(put("/jobs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedJob)))
            .andExpect(status().isOk())
            .andExpect(content().string("Job succesfully updated"));
        
        verify(jobService, times(1)).updateJob(eq(1L), any(Job.class));
    }
    
    @Test
    void testUpdateJobs_NotFound() throws Exception {
        // GIVEN
        Job updatedJob = new Job();
        updatedJob.setTitle("Updated Title");
        
        when(jobService.updateJob(eq(999L), any(Job.class))).thenReturn(false);
        
        // WHEN & THEN
        mockMvc.perform(put("/jobs/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedJob)))
            .andExpect(status().isNotFound());
        
        verify(jobService, times(1)).updateJob(eq(999L), any(Job.class));
    }
    
    // ========== DELETE JOB ==========
    
    @Test
    void testDeleteJobs_Success() throws Exception {
        // GIVEN
        when(jobService.deleteById(1L)).thenReturn(true);
        
        // WHEN & THEN
        mockMvc.perform(delete("/jobs/1"))
            .andExpect(status().isOk())
            .andExpect(content().string("Job deleted successfully"));
        
        verify(jobService, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteJobs_NotFound() throws Exception {
        // GIVEN
        when(jobService.deleteById(999L)).thenReturn(false);
        
        // WHEN & THEN
        mockMvc.perform(delete("/jobs/999"))
            .andExpect(status().isNotFound());
        
        verify(jobService, times(1)).deleteById(999L);
    }
    
    // ========== EDGE CASES ==========
    
    @Test
    void testFindJobsById_InvalidIdFormat() throws Exception {
        // WHEN & THEN
        mockMvc.perform(get("/jobs/invalid"))
            .andExpect(status().isBadRequest());
        
        verify(jobService, never()).getJobById(anyLong());
    }
    
    @Test
    void testCreateJob_ServiceThrowsException() throws Exception {
        // GIVEN
        Job newJob = new Job();
        newJob.setTitle("Test Job");
        
        when(jobService.createJob(any(Job.class)))
            .thenThrow(new RuntimeException("Database error"));
        
        // WHEN & THEN
        mockMvc.perform(post("/jobs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newJob)))
            .andExpect(status().isInternalServerError());
        
        verify(jobService, times(1)).createJob(any(Job.class));
    }
}