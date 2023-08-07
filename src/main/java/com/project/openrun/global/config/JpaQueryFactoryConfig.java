package com.project.openrun.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JpaQueryFactoryConfig {
    @Bean
    JPAQueryFactory jpaRepository(EntityManager em){
        return new JPAQueryFactory(em);
    }
}
