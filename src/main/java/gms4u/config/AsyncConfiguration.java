package gms4u.config;

import io.github.jhipster.async.ExceptionHandlingAsyncTaskExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.quartz.QuartzProperties;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfiguration implements AsyncConfigurer {

    private final Logger log = LoggerFactory.getLogger(AsyncConfiguration.class);

    private final TaskExecutionProperties taskExecutionProperties;

    private final TaskSchedulingProperties taskSchedulingProperties;

    @Autowired
    @Qualifier("quartzDataSource")
    private DataSource quartzDataSource;

    @Autowired
    private ApplicationContext applicationContext;


    public AsyncConfiguration(TaskExecutionProperties taskExecutionProperties, TaskSchedulingProperties taskSchedulingProperties) {
        this.taskExecutionProperties = taskExecutionProperties;
        this.taskSchedulingProperties = taskSchedulingProperties;
    }

    @Override
    @Bean(name = "taskExecutor")
    public Executor getAsyncExecutor() {
        log.debug("Creating Async Task Executor");
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());
        executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());
        executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity());
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

//    @Bean
//    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
//        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
//        threadPoolTaskScheduler.setThreadNamePrefix(taskSchedulingProperties.getThreadNamePrefix());
//        threadPoolTaskScheduler.setPoolSize(taskSchedulingProperties.getPool().getSize() * 10);
//        return threadPoolTaskScheduler;
//    }


    @Primary
    @Bean
    public QuartzProperties quartzProperties() {
        return new QuartzProperties();
    }

//    @Bean
//    public SpringBeanJobFactory springBeanJobFactory() {
//        AutoWiringSpringBeanJobFactory jobFactory = new AutoWiringSpringBeanJobFactory();
//        log.debug("Configuring Job factory");
//
//        jobFactory.setApplicationContext(applicationContext);
//        return jobFactory;
//    }
//
//    @Bean
//    public Scheduler scheduler(Trigger trigger, JobDetail job, SchedulerFactoryBean factory)
//        throws SchedulerException {
//        Scheduler scheduler = factory.getScheduler();
//        scheduler.scheduleJob(job, trigger);
//        scheduler.start();
//        return scheduler;
//    }
//
//    @Bean
//    public JobDetailFactoryBean jobDetail() {
//
//        JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
//        jobDetailFactory.setJobClass(ReminderJob.class);
//        jobDetailFactory.setName("Qrtz_Job_Detail");
//        jobDetailFactory.setDescription("Invoke Sample Job service...");
//        jobDetailFactory.setDurability(true);
//        return jobDetailFactory;
//    }
//
//
//    @Bean
//    public SimpleTriggerFactoryBean trigger(JobDetail job) {
//
//        SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
//        trigger.setJobDetail(job);
//
//        int frequencyInSec = 10;
//        log.info("Configuring trigger to fire every {} seconds", frequencyInSec);
//
//        trigger.setRepeatInterval(frequencyInSec * 1000);
//        trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
//        trigger.setName("Qrtz_Trigger");
//        return trigger;
//    }

    @Bean
    @DependsOn("quartzDataSource")
    public SchedulerFactoryBean schedulerFactoryBean() {

        SchedulerFactoryBean scheduler = new SchedulerFactoryBean();
        scheduler.setDataSource(quartzDataSource);
        Properties props = new Properties();

        props.putAll(quartzProperties().getProperties());
        scheduler.setQuartzProperties(props);
        scheduler.setApplicationContextSchedulerContextKey("applicationContext");
        return scheduler;
    }


}
