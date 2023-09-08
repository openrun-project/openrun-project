package com.project.openrun.global.scheduler;

import com.project.openrun.global.util.RedisLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j(topic = "ProductScheduler")
@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final JobLauncher jobLauncher;
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;

    private final RedisLock redisLock;

    private static final String JOB_NAME = "productJob";
    private static final String BATCH_LOCK_KEY = "batch";
    private static final String JOB_PARAM_KEY = "time";
    private static final long TIME_OUT = 5;

    @Scheduled(cron = "0 0 0 * * *")  // 매일 정오에 실행
    public void scheduledJobRunner() throws Exception {

        if (redisLock.tryLock(BATCH_LOCK_KEY, TIME_OUT)) {
            try {
                Set<JobExecution> productJob = jobExplorer.findRunningJobExecutions(JOB_NAME);

                if (productJob.isEmpty()) {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addLong(JOB_PARAM_KEY, System.currentTimeMillis())
                            .toJobParameters();

                    jobLauncher.run(jobRegistry.getJob(JOB_NAME), jobParameters);

                } else {
                    log.info("Job이 이미 실행 중 입니다.");
                }
            } finally {
                redisLock.unlock(BATCH_LOCK_KEY);
            }
        } else {
            log.info("락을 획득하지 못했습니다. 다른 인스턴스에서 이미 batch 작업 수행중입니다.");
        }

    }

}
