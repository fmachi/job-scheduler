package com.company.jobscheduler.stubs;

import com.company.jobscheduler.jobs.IJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.IntStream;

public class SuccessJobStub implements IJob
{
  private final static Logger logger = LoggerFactory.getLogger(SuccessJobStub.class);

  private final long howToSleep;
  private final int numberOfattempts;
  private boolean completed =false;
  private boolean started = false;

  public SuccessJobStub(long howToSleep, int numberOfattempts)
  {
    this.howToSleep = howToSleep;
    this.numberOfattempts = numberOfattempts;
  }

  @Override public void execute()
  {
    started = true;
    IntStream.rangeClosed(0,numberOfattempts).forEach(
        i ->
        {
          try
          {
            logger.info("Sleeping slot {}",i);
            Thread.sleep(howToSleep);
          }
          catch (InterruptedException e)
          {
            logger.error("Thread interrupted");
          }
        }
    );
    completed = true;
  }

  public boolean isStarted()
  {
    return started;
  }

  public boolean isCompleted()
  {
    return completed;
  }
}
