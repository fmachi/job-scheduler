package com.company.jobscheduler.scheduler.checker;

import java.util.LinkedList;
import java.util.concurrent.Future;

public class FixedTimeoutFutureCheckerFactory implements FutureCheckerFactory
{
  private final long timeout;

  public FixedTimeoutFutureCheckerFactory(long timeout)
  {
    this.timeout = timeout;
  }

  @Override public FutureChecker of(LinkedList<Future> collection)
  {
    return new FutureChecker(collection,timeout);
  }
}
