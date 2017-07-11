package com.company.jobscheduler.scheduler.checker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FutureCheckerTest
{

  FutureChecker checker;
  @Mock
  LinkedList<Future> toBeChecked;
  @Mock
  Future future;

  @Before
  public void setup() {
    checker = new FutureChecker(toBeChecked,10);
  }

  @Test
  public void futureCompleted() throws InterruptedException, ExecutionException, TimeoutException
  {
    when(future.get(10, TimeUnit.MILLISECONDS)).thenReturn(new Object());

    Optional<Throwable> optionalException = checker.apply(future);

    assertThat(optionalException.isPresent(),is(false));
    verifyZeroInteractions(toBeChecked);

  }

  @Test
  public void executionException() throws InterruptedException, ExecutionException, TimeoutException
  {
    RuntimeException innerException = new RuntimeException();
    when(future.get(10, TimeUnit.MILLISECONDS)).thenThrow(new ExecutionException(innerException));

    Optional<Throwable> optionalException = checker.apply(future);

    assertThat(optionalException.isPresent(),is(true));
    assertThat(optionalException.get(),is(equalTo(innerException)));
    verifyZeroInteractions(toBeChecked);

  }

  @Test
  public void timeoutException() throws InterruptedException, ExecutionException, TimeoutException
  {
    when(future.get(10, TimeUnit.MILLISECONDS)).thenThrow(TimeoutException.class);

    Optional<Throwable> optionalException = checker.apply(future);

    assertThat(optionalException.isPresent(),is(false));
    verify(toBeChecked).add(future);

  }

  @Test
  public void interruptedException() throws InterruptedException, ExecutionException, TimeoutException
  {
    when(future.get(10, TimeUnit.MILLISECONDS)).thenThrow(InterruptedException.class);

    Optional<Throwable> optionalException = checker.apply(future);

    assertThat(optionalException.isPresent(),is(true));
    assertThat(optionalException.get() instanceof FutureChecker.FutureCheckerInterruptedException, is(true));
    verifyZeroInteractions(toBeChecked);

  }

}
