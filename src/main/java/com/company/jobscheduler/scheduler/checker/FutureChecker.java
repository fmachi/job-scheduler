package com.company.jobscheduler.scheduler.checker;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

public class FutureChecker implements Function<Future,Optional<Throwable>>
{
  final LinkedList<Future> toBeChecked;
  final long timeout;

  public FutureChecker(LinkedList<Future> toBeChecked, long timeout)
  {
    this.toBeChecked = toBeChecked;
    this.timeout = timeout;
  }

  @Override public Optional<Throwable> apply(Future future) {
    try
    {
      future.get(timeout, TimeUnit.MILLISECONDS);

    } catch (InterruptedException ex) {
      return Optional.of(new FutureCheckerInterruptedException());
    } catch (ExecutionException ex) {
      return Optional.of(ex.getCause());
    } catch (TimeoutException ex) {
      toBeChecked.add(future);
    }
    return Optional.empty();
  }

  class FutureCheckerInterruptedException extends Throwable
  {
    public FutureCheckerInterruptedException()
    {
      super("Checker thread interrupted while working");
    }
  }
}
