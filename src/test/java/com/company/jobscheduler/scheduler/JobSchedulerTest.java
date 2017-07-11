package com.company.jobscheduler.scheduler;

import com.company.jobscheduler.jobs.IJob;
import com.company.jobscheduler.scheduler.checker.FixedTimeoutFutureCheckerFactory;
import com.company.jobscheduler.scheduler.checker.FutureCheckerFactory;
import com.company.jobscheduler.stubs.ExceptionJobStub;
import com.company.jobscheduler.stubs.SuccessJobStub;
import org.junit.Test;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.*;

public class JobSchedulerTest
{
  FutureCheckerFactory futureCheckFactory = new FixedTimeoutFutureCheckerFactory(10);
  JobScheduler scheduler;
  List<IJob> tasks;
  private Predicate<SuccessJobStub> completed = j -> j.isStarted() && j.isCompleted();
  private Predicate<SuccessJobStub> startedButNotCompleted = j -> j.isStarted() && !j.isCompleted();

  @Test
  public void happyPath() {

    givenASchedulerJobWithAllSuccessTasks();

    whenExecutingTheSchedulerJob();

    assertAllJobsAreCompleted();

  }

  @Test
  public void oneErrorOccur() {

    givenASchedulerJobWithOneInError();

    RuntimeException exception = null;
    try
    {
      whenExecutingTheSchedulerJob();
    } catch(RuntimeException ex) {
       exception = ex;
    }

    assertThatExceptionIsThrown(exception);

    assertInnerJobsAre(completed, 1, 2);
    assertInnerJobsAre(startedButNotCompleted, 0, 3, 4, 5);

  }

  private void assertThatExceptionIsThrown(RuntimeException exception)
  {
    assertThat(exception,is(notNullValue()));
    assertThat(exception.getMessage(),is(equalTo("At least one exception was thrown while running inner tasks.")));
    assertThat(exception.getCause(),is(notNullValue()));
    assertThat(exception.getCause().getMessage(),is(equalTo("Breaking everything")));
  }

  private void givenASchedulerJobWithOneInError()
  {
    tasks = asList(
        new SuccessJobStub(200, 4),
        new SuccessJobStub(200,2),
        new SuccessJobStub(100,5),
        new SuccessJobStub(200,6),
        new SuccessJobStub(200,7),
        new SuccessJobStub(200,8),
        new ExceptionJobStub(700, new RuntimeException("Breaking everything"))
    );

    scheduler = new JobScheduler(7, tasks, futureCheckFactory);
  }

  private void assertAllJobsAreCompleted()
  {
    assertInnerJobsAre(completed,0,1,2);
  }

  private void givenASchedulerJobWithAllSuccessTasks()
  {
    tasks = asList(
        new SuccessJobStub(30, 10),
        new SuccessJobStub(200,2),
        new SuccessJobStub(100,5)
    );

    scheduler = new JobScheduler(3, tasks, futureCheckFactory);
  }

  private void whenExecutingTheSchedulerJob()
  {
    scheduler.execute();
  }


  private void assertInnerJobsAre(Predicate<SuccessJobStub> toCheck, int...values)
  {


    assertThat(IntStream.of(values).mapToObj(i -> tasks.get(i))
                   .map(
                       t -> (SuccessJobStub) t)
                   .allMatch(toCheck),
               is(true));
  }

}
