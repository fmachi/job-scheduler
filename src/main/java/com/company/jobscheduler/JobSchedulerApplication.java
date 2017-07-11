package com.company.jobscheduler;

import com.company.jobscheduler.scheduler.JobScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JobSchedulerApplication
{

	@Autowired JobScheduler jobScheduler;

	public static void main(String[] args) {

		ConfigurableApplicationContext context = SpringApplication.run(JobSchedulerApplication.class, args);

		JobScheduler jobScheduler = context.getBean(JobScheduler.class);
		jobScheduler.execute();

		context.close();
	}
}
