package com.project.openrun;

import org.springframework.batch.core.Job;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;


@TestConfiguration
public class BatchTestConfig {

    @Autowired
    private Job job;
    @Bean
    JobLauncherTestUtils jobLauncherTestUtils() {
        JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
        jobLauncherTestUtils.setJob(job);
        return jobLauncherTestUtils;
    }
}
