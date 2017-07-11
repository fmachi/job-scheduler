package com.company.jobscheduler.stubs;

import com.company.jobscheduler.jobs.IJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExceptionJobStub implements IJob
{
  private final static Logger logger = LoggerFactory.getLogger(ExceptionJobStub.class);

  final long howToSleep;
  private final RuntimeException throwable;
  private boolean started = false;

  public ExceptionJobStub(long howToSleep,RuntimeException throwable)
  {
    this.howToSleep = howToSleep;
    this.throwable = throwable;
  }

  @Override public void execute()
  {
    started = true;
    try
    {
      logger.info("Sleeping for {}", howToSleep);
      Thread.sleep(howToSleep);
    }
    catch (InterruptedException e)
    {
      logger.error("Thread interrupted");
    }
    throw throwable;
  }

  public boolean isStarted()
  {
    return started;
  }
}
