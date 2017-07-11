package com.company.jobscheduler.scheduler;

import com.company.jobscheduler.jobs.IJob;
import com.company.jobscheduler.scheduler.checker.FutureChecker;
import com.company.jobscheduler.scheduler.checker.FutureCheckerFactory;

import javax.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class JobScheduler implements IJob
{
  private final List<IJob> tasks;
  private final ExecutorService executorService;
  private final FutureCheckerFactory futureCheckerFactory;

  public JobScheduler(int numberOfThreads, List<IJob> tasks, FutureCheckerFactory futureCheckerFactory)
  {
    this.tasks = tasks;
    executorService = Executors.newFixedThreadPool(numberOfThreads);
    this.futureCheckerFactory = futureCheckerFactory;
  }

  @Override public void execute()
  {
    final LinkedList<Future> futuresToBeChecked = scheduleTasks();

    Optional<Throwable> exception = checkForExecutionExceptions(futuresToBeChecked);

    exception.ifPresent(
        e ->
        {
          destroy();
          throw new InnerExecutionException(e);
        }
    );

  }

  @PreDestroy
  public void destroy()
  {
    executorService.shutdownNow();
  }

  private LinkedList<Future> scheduleTasks()
  {
    return tasks.stream()
        .map(t -> (Runnable) (() -> t.execute()))
        .map(executorService::submit).collect(Collectors.toCollection(LinkedList::new));
  }

  private Optional<Throwable> checkForExecutionExceptions(LinkedList<Future> futuresToBeChecked)
  {
    FutureChecker futureChecker = futureCheckerFactory.of(futuresToBeChecked);

    /**
     * I first tried a solution based on stream generation but
     * unfortunately takeWhile operation not yet available on java 8
     * so there's no a simple way to generate a finite stream (until a predicate is valid)
     * I could throw an exception but I didn't like it
     *
     return Stream.generate(
     () -> futuresToBeChecked.isEmpty()?
     null:
     futuresToBeChecked.pop()
     ).map(futureChecker).filter(Optional::isPresent).map(Optional::get).findAny();
     **/

    while (!futuresToBeChecked.isEmpty())
    {
      Optional<Throwable> throwableOptional = futureChecker.apply(futuresToBeChecked.pop());
      if (throwableOptional.isPresent())
      {
        return throwableOptional;
      }
    }
    return Optional.empty();
  }

  class InnerExecutionException extends RuntimeException
  {
    public InnerExecutionException(Throwable cause)
    {
      super("At least one exception was thrown while running inner tasks.", cause);
    }
  }
}
