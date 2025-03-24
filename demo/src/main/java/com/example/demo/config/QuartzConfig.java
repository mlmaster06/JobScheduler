package com.example.demo.config;

import com.example.demo.schedulerQuartz.QuartzJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class QuartzConfig {

    @Bean
    public JobDetail quartzJobDetail() {
        return JobBuilder.newJob(QuartzJob.class)
                .withIdentity("QuartzJob")
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger quartzTrigger(JobDetail quartzJobDetail) {
        return TriggerBuilder.newTrigger()
                .forJob(quartzJobDetail)
                .withIdentity("QuartzTrigger")
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(30)  // Runs every 30 seconds
                        .repeatForever())
                .build();
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean() {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        return factoryBean;
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factoryBean) throws SchedulerException {
        return factoryBean.getScheduler();
    }

}

