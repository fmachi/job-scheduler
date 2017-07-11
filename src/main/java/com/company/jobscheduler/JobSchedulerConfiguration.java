package com.company.jobscheduler;

import com.company.jobscheduler.jobs.IJob;
import com.company.jobscheduler.scheduler.JobScheduler;
import com.company.jobscheduler.scheduler.checker.FixedTimeoutFutureCheckerFactory;
import com.company.jobscheduler.scheduler.checker.FutureCheckerFactory;
import com.company.jobscheduler.stubs.SuccessJobStub;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static java.util.Arrays.asList;

@Configuration
public class JobSchedulerConfiguration
{

  @Value("${jobs.scheduler.numberOfThreads}")
  int numberOfThreads;

  @Bean
  public JobScheduler jobScheduler(List<IJob> tasks, FutureCheckerFactory futureCheckerFactory)
  {
    return new JobScheduler(numberOfThreads, tasks, futureCheckerFactory);
  }

  @Bean
  public List<IJob> fakeTaskList()
  {
    return asList(
        new SuccessJobStub(300,10),
        new SuccessJobStub(300,8),
        new SuccessJobStub(300,6),
        new SuccessJobStub(300,11)
    );
  }

  @Bean
  public FutureCheckerFactory futureCheckerFactory()
  {
    return new FixedTimeoutFutureCheckerFactory(10);
  }

}
