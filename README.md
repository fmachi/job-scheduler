# job-scheduler
This project contains an implementation to schedule jobs in parallel and to handle exceptions

In particular the problem to solve was the following:


In our backend application, there are multiple classes implementing the IJob interface. 
Each of this implementations performs certain operations and is scheduled to run at a 
certain time on our servers. Because the amount of jobs we have to run increases 
steadily, we want to create a new job, which takes a list of IJob instances and runs 
them in parallel. 

The amount of threads that the new implementation is using to run the jobs in parallel 
should be configurable. 

If one of the jobs throws an exception, all other currently running jobs should be stopped 
as well and the execute() method should pass the exception to the caller. 

To make sure the implementation works as designed UnitTests are required.

Solution:

The solution is based on executors and java 8 streams.
Basically it builds an executors whose size is configurable and submit all
the job instances it receives in the constructor once the execute method is invoked.
Then it checks periodically (the period can be configured) in order to checks whether
tasks are completed or whether an exception is thrown.
A List is used to store Future and in case the job execution is not completed, the future
is added again to the list. In case of exception from once of the inner job, the first
caught exception is propagated to the caller.

Unfortunately I made the checking of Future inside a stream generated but there's
no easy way to stop it (takeWhile will be added in java 9)

In order to make unit tests I create stubs for jobs taking a while and for jobs throwing
exceptions.

I added the project on travis in order to have a continuous integration tool.
