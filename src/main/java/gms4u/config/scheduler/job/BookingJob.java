package gms4u.config.scheduler.job;

import gms4u.repository.BookingRepository;
import gms4u.service.ReminderService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
public class BookingJob implements Job {

    Logger logger = LoggerFactory.getLogger(getClass());

    public void execute(JobExecutionContext context) throws JobExecutionException {

        logger.info("Job ** {} ** fired @ {}", context.getJobDetail().getKey().getName(), context.getFireTime());

        try {
            ApplicationContext applicationContext = (ApplicationContext) context.getScheduler().getContext().get("applicationContext");
            ReminderService reminderService = applicationContext.getBean(ReminderService.class);
            BookingRepository bookingRepository = applicationContext.getBean(BookingRepository.class);
            long bookingId = context.getJobDetail().getJobDataMap().getLong("bookingId");

            bookingRepository.findById(bookingId).ifPresent(booking -> {
                try {
                    reminderService.sendBookingReminderMessage(booking);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            });
        } catch (SchedulerException e) {
            logger.error(e.getMessage());

        }
    }
}
