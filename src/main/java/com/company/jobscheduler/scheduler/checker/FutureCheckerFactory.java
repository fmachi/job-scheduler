package com.company.jobscheduler.scheduler.checker;

import java.util.LinkedList;
import java.util.concurrent.Future;

public interface FutureCheckerFactory
{
  public FutureChecker of(LinkedList<Future> collection);
}
