package com.example.job_service.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.job_service.model.Job;
import com.example.job_service.service.JobService;

@Service
public class JobServiceImpl implements JobService {

    private List<Job> jobs = new ArrayList<>();
    private Long id = 1L;

    @Override
    public List<Job> findAllJob() {
        return jobs;
    }

    @Override
    public Job createJob(Job job) {
        job.setId(id++);
        jobs.add(job);
        return job;
    }

    @Override
    public Job getJobById(Long id) {
        for (Job job : jobs) {
            if (job.getId().equals(id)) {
                return job;
            }
        }
        return null;
    }

    @Override
    public boolean deleteById(Long id) {
        Iterator<Job> iterator = jobs.iterator();
        while (iterator.hasNext()) {
            Job job = iterator.next();
            if (job.getId().equals(id)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean updateJob(Long id, Job updateJob) {
        for (Job job : jobs) {
            if (job.getId().equals(id)) {
                job.setTitle(updateJob.getTitle());
                job.setDescription(updateJob.getTitle());
                job.setMinSalary(updateJob.getTitle());
                job.setMaxSalary(updateJob.getTitle());
                job.setLocation(updateJob.getTitle());
                return true;
            }
        }
        return false;
    }

}
