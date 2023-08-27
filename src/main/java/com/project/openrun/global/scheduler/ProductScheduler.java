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

    // 스케줄러 설정
    @Scheduled(cron = "0 0 0 * * *")  // 매일 정오에 실행
    public void scheduledJobRunner() throws Exception {

        if (redisLock.tryLock("batch", 5)) {
            try {
                // 마지막 JobExecution의 상태를 확인하여 잡이 이미 실행 중인지 확인
                Set<JobExecution> productJob = jobExplorer.findRunningJobExecutions("productJob");

                if (productJob.isEmpty()) {
                    JobParameters jobParameters = new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters();

                    jobLauncher.run(jobRegistry.getJob("productJob"), jobParameters);

                } else {
                    log.info("Job이 이미 실행 중 입니다.");
                }
            } finally {
                redisLock.unlock("batch");
            }
        } else {
            log.info("락을 획득하지 못했습니다. 다른 인스턴스에서 이미 batch 작업 수행중입니다.");
        }

    }

}
