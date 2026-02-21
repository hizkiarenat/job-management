package com.example.job_service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.job_service.client.CompanyClient;
import com.example.job_service.client.ReviewClient;
import com.example.job_service.dto.JobDTO;
import com.example.job_service.external.Company;
import com.example.job_service.external.Review;
import com.example.job_service.model.Job;
import com.example.job_service.repository.JobRepository;
import com.example.job_service.service.impl.JobServiceImpl;

@ExtendWith(MockitoExtension.class)
public class JobServiceImplTest {

    @Mock
    private JobRepository jobRepository;
    @Mock
    private CompanyClient companyClient;
    @Mock
    private ReviewClient reviewClient;
    @InjectMocks
    private JobServiceImpl jobServiceImpl;

    private Job testJob1;
    private Job testJob2;
    private Company testCompany;
    private Review testReview1;
    private Review testReview2;

    @BeforeEach
    void setUp() {
        testJob1 = new Job();
        testJob1.setId(1L);
        testJob1.setTitle("Software Engineer");
        testJob1.setDescription("Backend Dev");
        testJob1.setMinSalary("5000000");
        testJob1.setMaxSalary("10000000");
        testJob1.setLocation("Jakarta");
        testJob1.setCompanyId(1L);

        testJob2 = new Job();
        testJob2.setId(2L);
        testJob2.setTitle("DevOps Engineer");
        testJob2.setDescription("Cloud infrastructure");
        testJob2.setMinSalary("6000");
        testJob2.setMaxSalary("9000");
        testJob2.setLocation("Bandung");
        testJob2.setCompanyId(1L);

        // Setup Company test data
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Tech Corp");
        testCompany.setDescription("Software company");

        // Setup Review test data
        testReview1 = new Review();
        testReview1.setId(1L);
        testReview1.setTitle("Great company");
        testReview1.setDescription("Good culture");
        testReview1.setRating(4.5);

        testReview2 = new Review();
        testReview2.setId(2L);
        testReview2.setTitle("Nice workplace");
        testReview2.setDescription("Good benefits");
        testReview2.setRating(4.0);
    }

    @Test
    void testCreateJob_Success() {
        // GIVEN
        Job newJob = new Job();
        newJob.setTitle("Backend Developer");
        newJob.setDescription("Java Spring Boot");
        newJob.setMinSalary("5000");
        newJob.setMaxSalary("7000");
        newJob.setLocation("Surabaya");
        newJob.setCompanyId(1L);

        when(jobRepository.save(any(Job.class))).thenReturn(testJob1);

        // WHEN
        Job result = jobServiceImpl.createJob(newJob);

        // THEN
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Software Engineer", result.getTitle());

        // Verify repository.save() called exactly once
        verify(jobRepository, times(1)).save(any(Job.class));
    }

    @Test
    void testCreateJob_NullJob() {
        // GIVEN
        Job nullJob = null;

        // THEN
        assertThrows(IllegalArgumentException.class, () -> {
            jobServiceImpl.createJob(nullJob);
        });

        verify(jobRepository, never()).save(any());
    }

    // ========== READ TESTS ==========
    
    @Test
    void testFindAllJob_ReturnsListOfJobDTO() {
        // GIVEN
        List<Job> mockJobs = Arrays.asList(testJob1, testJob2);
        when(jobRepository.findAll()).thenReturn(mockJobs);
        
        // Mock CompanyClient & ReviewClient
        when(companyClient.getCompanyById(1L)).thenReturn(testCompany);
        when(reviewClient.getReview(1L)).thenReturn(Arrays.asList(testReview1, testReview2));
        
        // WHEN
        List<JobDTO> result = jobServiceImpl.findAllJob();
        
        // THEN
        assertNotNull(result);
        assertEquals(2, result.size());
        
        // Verify first job
        JobDTO firstJob = result.get(0);
        assertEquals("Software Engineer", firstJob.getTitle());
        assertEquals("Tech Corp", firstJob.getCompany().getName());
        assertEquals(2, firstJob.getReview().size());
        
        // Verify interactions
        verify(jobRepository, times(1)).findAll();
        verify(companyClient, times(2)).getCompanyById(1L);  // Called for each job
        verify(reviewClient, times(2)).getReview(1L);
    }
    
    @Test
    void testFindAllJob_EmptyList() {
        // GIVEN
        when(jobRepository.findAll()).thenReturn(List.of());
        
        // WHEN
        List<JobDTO> result = jobServiceImpl.findAllJob();
        
        // THEN
        assertNotNull(result);
        assertTrue(result.isEmpty());
        
        verify(jobRepository, times(1)).findAll();
        // CompanyClient & ReviewClient should NOT be called (no jobs)
        verify(companyClient, never()).getCompanyById(anyLong());
        verify(reviewClient, never()).getReview(anyLong());
    }
    
    @Test
    void testGetJobById_Found() {
        // GIVEN
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob1));
        when(companyClient.getCompanyById(1L)).thenReturn(testCompany);
        when(reviewClient.getReview(1L)).thenReturn(Arrays.asList(testReview1));
        
        // WHEN
        JobDTO result = jobServiceImpl.getJobById(1L);
        
        // THEN
        assertNotNull(result);
        assertEquals("Software Engineer", result.getTitle());
        assertEquals("Jakarta", result.getLocation());
        assertEquals("Tech Corp", result.getCompany().getName());
        assertEquals(1, result.getReview().size());
        
        verify(jobRepository, times(1)).findById(1L);
        verify(companyClient, times(1)).getCompanyById(1L);
        verify(reviewClient, times(1)).getReview(1L);
    }
    
    @Test
    void testGetJobById_NotFound_ReturnsNull() {
        // GIVEN
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        
        // WHEN
        JobDTO result = jobServiceImpl.getJobById(999L);
        
        // THEN
        assertNull(result);
        
        verify(jobRepository, times(1)).findById(999L);
        // Should NOT call CompanyClient/ReviewClient (job not found)
        verify(companyClient, never()).getCompanyById(anyLong());
        verify(reviewClient, never()).getReview(anyLong());
    }
    
    @Test
    void testGetJobById_CompanyClientFails() {
        // GIVEN
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob1));
        when(companyClient.getCompanyById(1L)).thenThrow(new RuntimeException("Company service down"));
        
        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            jobServiceImpl.getJobById(1L);
        });
        
        verify(jobRepository, times(1)).findById(1L);
        verify(companyClient, times(1)).getCompanyById(1L);
        // ReviewClient should NOT be called (failed at companyClient)
        verify(reviewClient, never()).getReview(anyLong());
    }
    
    // ========== UPDATE TESTS ==========
    
    @Test
    void testUpdateJob_Success() {
        // GIVEN
        Job updatedJobData = new Job();
        updatedJobData.setTitle("Senior Software Engineer");
        updatedJobData.setDescription("Lead backend developer");
        updatedJobData.setMinSalary("8000");
        updatedJobData.setMaxSalary("12000");
        updatedJobData.setLocation("Jakarta");
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob1));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob1);
        
        // WHEN
        boolean result = jobServiceImpl.updateJob(1L, updatedJobData);
        
        // THEN
        assertTrue(result);
        
        // Verify the job was updated (testJob1 is modified in-place)
        assertEquals("Senior Software Engineer", testJob1.getTitle());
        assertEquals("Lead backend developer", testJob1.getDescription());
        assertEquals("8000", testJob1.getMinSalary());
        
        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(testJob1);
    }
    
    @Test
    void testUpdateJob_NotFound() {
        // GIVEN
        Job updatedJobData = new Job();
        updatedJobData.setTitle("Updated Title");
        
        when(jobRepository.findById(999L)).thenReturn(Optional.empty());
        
        // WHEN
        boolean result = jobServiceImpl.updateJob(999L, updatedJobData);
        
        // THEN
        assertFalse(result);
        
        verify(jobRepository, times(1)).findById(999L);
        verify(jobRepository, never()).save(any(Job.class));
    }
    
    @Test
    void testUpdateJob_PartialUpdate() {
        // GIVEN: Only update title, leave others unchanged
        Job partialUpdate = new Job();
        partialUpdate.setTitle("Updated Title Only");
        partialUpdate.setDescription(null);  // Not updating description
        partialUpdate.setMinSalary(null);
        partialUpdate.setMaxSalary(null);
        partialUpdate.setLocation(null);
        
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob1));
        when(jobRepository.save(any(Job.class))).thenReturn(testJob1);
        
        // WHEN
        boolean result = jobServiceImpl.updateJob(1L, partialUpdate);
        
        // THEN
        assertTrue(result);
        assertEquals("Updated Title Only", testJob1.getTitle());
        // Note: Your implementation sets null values, might want to add null checks
        
        verify(jobRepository, times(1)).findById(1L);
        verify(jobRepository, times(1)).save(testJob1);
    }
    
    // ========== DELETE TESTS ==========
    
    @Test
    void testDeleteById_Success() {
        // GIVEN
        doNothing().when(jobRepository).deleteById(1L);
        
        // WHEN
        boolean result = jobServiceImpl.deleteById(1L);
        
        // THEN
        assertTrue(result);
        
        verify(jobRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void testDeleteById_NotFound() {
        // GIVEN
        doThrow(new RuntimeException("Job not found"))
            .when(jobRepository)
            .deleteById(999L);
        
        // WHEN & THEN
        assertThrows(RuntimeException.class, () -> {
            jobServiceImpl.deleteById(999L);
        });
        
        verify(jobRepository, times(1)).deleteById(999L);
    }
    
    // ========== CIRCUIT BREAKER FALLBACK TEST ==========
    
    @Test
    void testFindAllJob_CircuitBreakerFallback() {
        // GIVEN
        List<Job> mockJobs = Arrays.asList(testJob1);
        when(jobRepository.findAll()).thenReturn(mockJobs);
        
        // Mock CompanyClient to fail (trigger circuit breaker)
        when(companyClient.getCompanyById(1L))
            .thenThrow(new RuntimeException("Company service down"));
        
        // WHEN & THEN
        // Note: Circuit breaker fallback returns List<String>, not List<JobDTO>
        // Your fallback method signature might need adjustment
        assertThrows(RuntimeException.class, () -> {
            jobServiceImpl.findAllJob();
        });
        
        verify(jobRepository, times(1)).findAll();
        verify(companyClient, times(1)).getCompanyById(1L);
    }
    
    // ========== EDGE CASES ==========
    
    @Test
    void testCreateJob_WithNullFields() {
        // GIVEN
        Job incompleteJob = new Job();
        incompleteJob.setTitle("Only Title");
        // Other fields are null
        
        when(jobRepository.save(any(Job.class))).thenReturn(incompleteJob);
        
        // WHEN
        Job result = jobServiceImpl.createJob(incompleteJob);
        
        // THEN
        assertNotNull(result);
        assertEquals("Only Title", result.getTitle());
        assertNull(result.getDescription());
        
        verify(jobRepository, times(1)).save(any(Job.class));
    }
    
    @Test
    void testGetJobById_ReviewClientReturnsEmptyList() {
        // GIVEN
        when(jobRepository.findById(1L)).thenReturn(Optional.of(testJob1));
        when(companyClient.getCompanyById(1L)).thenReturn(testCompany);
        when(reviewClient.getReview(1L)).thenReturn(List.of());  // Empty reviews
        
        // WHEN
        JobDTO result = jobServiceImpl.getJobById(1L);
        
        // THEN
        assertNotNull(result);
        assertTrue(result.getReview().isEmpty());
        
        verify(reviewClient, times(1)).getReview(1L);
    }
}
