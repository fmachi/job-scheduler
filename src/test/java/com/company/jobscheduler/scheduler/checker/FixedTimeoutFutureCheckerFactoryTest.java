package com.company.jobscheduler.scheduler.checker;

import org.junit.Test;

import java.util.LinkedList;
import java.util.concurrent.Future;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class FixedTimeoutFutureCheckerFactoryTest
{
  FixedTimeoutFutureCheckerFactory factory = new FixedTimeoutFutureCheckerFactory(100);
  private LinkedList<Future> collection = new LinkedList<>();

  @Test
  public void happyPath() {
    FutureChecker futureChecker = factory.of(collection);

    assertThat(futureChecker.timeout,is(equalTo(100L)));
    assertThat(futureChecker.toBeChecked,is(equalTo(collection)));
  }

}
