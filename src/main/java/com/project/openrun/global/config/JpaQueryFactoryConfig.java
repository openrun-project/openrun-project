package com.project.openrun.global.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JpaQueryFactoryConfig {
    @Bean
    @Primary
    JPAQueryFactory jpaRepository(EntityManager em){
        return new JPAQueryFactory(em);
    }
}
