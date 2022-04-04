package gms4u.config.scheduler.job;

import gms4u.repository.ReminderRepository;
import gms4u.service.ReminderService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class ReminderJob extends QuartzJobBean {

    Logger logger = LoggerFactory.getLogger(getClass());

    public void executeInternal(JobExecutionContext context) throws JobExecutionException {

        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        try {
            ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
            ReminderService reminderService = applicationContext.getBean(ReminderService.class);
            ReminderRepository reminderRepository = applicationContext.getBean(ReminderRepository.class);
            long reminderId = context.getJobDetail().getJobDataMap().getLong("reminderId");

            reminderRepository.findById(reminderId).ifPresent(r -> {
                try {
                    reminderService.sendReminderMessage(r);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        } catch (SchedulerException e) {
            logger.error(e.getMessage());

        }
    }
}
