#JavaConcurrent Tutorial

## 一.Synchronizer

### 1.1 CountDownLatch

Introduction  
A synchronization aid that allows one or more threads to wait until a set of operations being performed in other threads
completes.  
A CountDownLatch is initialized with a given count.  
The (#await await) methods block until the current count reaches zero due to invocations of the #countDown method,
after which all waiting threads are released and any subsequent invocations of (#await await) return immediately.  
This is a one-shot phenomenon -- the count cannot be reset.  
If you need a version that resets the count, consider using a CyclicBarrier.  
A CountDownLatch is a versatile synchronization tool and can be used for a number of purposes.  
A CountDownLatch initialized with a count of one serves as a simple on/off latch, or gate: all threads invoking (
await await) wait at the gate until it is opened by a thread invoking #countDown.  
A CountDownLatch initialized to N can be used to make one thread wait until N threads have completed some action, or
some action has been completed N times.  
A useful property of a CountDownLatch is that it doesn't require that threads calling countDown wait for the
count to reach zero before proceeding, it simply prevents any thread from proceeding past an (#await await) until all
threads could pass.  
Memory consistency effects: Until the count reaches zero, actions in a thread prior to calling countDown()
happen-before actions following a successful return from a corresponding await() in another thread.

We can use CountDownLatch to let a thread wait until one or more events have occurred.  
A CountDownLatch is initially created with a count of the number of events that must occur before the latch is
released.  
Each time an event happens, the count is decremented.  
When the count reaches zero, the latch opens.  
CountDownLatch has the following constructor:

```
CountDownLatch(int num)
```

Here, num specifies the number of events that must occur in order for the latch to open.  
To wait on the latch, a thread calls await(), which has the forms shown here:

```
void await() throws InterruptedException
boolean await(long wait, TimeUnit tu) throws InterruptedException
```

The first form waits until the count associated with the invoking CountDownLatch reaches zero.  
The second form waits only for the period of time specified by wait.  
The units represented by wait are specified by tu, which is an object the TimeUnit enumeration.  
It returns false if the time limit is reached and true if the countdown reaches zero.  
To signal an event, call the countDown() method, shown next:

```
void countDown()
```

Each call to countDown() decrements the count associated with the invoking object.  
The following program demonstrates CountDownLatch.  
It creates a latch that requires five events to occur before it opens.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.CountDownLatch;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-02 17:56
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    CountDownLatch countDownLatch = new CountDownLatch(5);
    new SubThread("A", countDownLatch);
    try {
      countDownLatch.await();
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("Done!!!");
  }

  private static class SubThread implements Runnable {
    private String name;
    private CountDownLatch countDownLatch;

    public SubThread(String name, CountDownLatch countDownLatch) {
      this.name = name;
      this.countDownLatch = countDownLatch;
      new Thread(this, name).start();
    }

    @Override
    public void run() {
      for (int x = 1; x <= 5; x++) {
        System.out.println(name + ":" + x);
        this.countDownLatch.countDown();
      }
    }
  }
}
```

The output produced by the program is shown here:

```
starting
1
2
3
4
5
Done
```

Inside main(), a CountDownLatch called countDownLatch is created with an initial count of five.  
Next, an instance of SubThread is created, which begins execution of a new thread.  
countDownLatch is passed as a parameter to SubThread's constructor and stored in the latch instance variable.  
The main thread calls await() on countDownLatch, which causes execution of the main thread to pause until
countDownLatch's count has been decremented five times.  
Inside the run() method of SubThread, a loop is created that iterates five times.  
With each iteration, the countDown() method is called on latch, which refers to countDownLatch in main().  
After the fifth iteration, the latch opens, which allows the main thread to resume.

+ CountDownLatch(int count)  
  Java CountDownLatch CountDownLatch(int count) Constructs a CountDownLatch initialized with the given count.  
  The method CountDownLatch(int count) is a constructor.  
  Syntax  
  The method CountDownLatch(int count) from CountDownLatch is declared as:
  ```
  public CountDownLatch(int count)
  ```
  Parameter  
  The method CountDownLatch(int count) has the following parameter:
    + int count - the number of times #countDown must be invoked before threads can pass through #await

  Exception  
  The method CountDownLatch(int count) throws the following exceptions:
    + IllegalArgumentException - if count is negative

+ await()  
  Introduction  
  Causes the current thread to wait until the latch has counted down to zero, unless the thread is Thread interrupt
  interrupted.  
  If the current count is zero then this method returns immediately.  
  If the current count is greater than zero then the current thread becomes disabled for thread scheduling purposes and
  lies dormant until one of two things happen:
    + The count reaches zero due to invocations of the #countDown method; or
    + Some other thread Thread interrupt interrupts the current thread.

  If the current thread:
    + has its interrupted status set on entry to this method; or
    + is Thread interrupt while waiting,

  then InterruptedException is thrown and the current thread's interrupted status is cleared.  
  Syntax  
  The method await() from CountDownLatch is declared as:
  ```
  public void await() throws InterruptedException
  ```
+ await(long timeout, TimeUnit unit)  
  Introduction  
  Causes the current thread to wait until the latch has counted down to zero, unless the thread is Thread interrupted),
  or the specified waiting time elapses.  
  If the current count is zero then this method returns immediately with the value true.  
  If the current count is greater than zero then the current thread becomes disabled for thread scheduling purposes and
  lies dormant until one of three things happen:
    + The count reaches zero due to invocations of the #countDown method; or
    + Some other thread Thread interrupt interrupts the current thread; or
    + The specified waiting time elapses.

  If the count reaches zero then the method returns with the value true.  
  If the current thread:
    + has its interrupted status set on entry to this method; or
    + is Thread interrupt while waiting,

  then InterruptedException is thrown and the current thread's interrupted status is cleared.  
  If the specified waiting time elapses then the value false is returned.  
  If the time is less than or equal to zero, the method will not wait at all.  
  Syntax  
  The method await(long timeout, TimeUnit unit) from CountDownLatch is declared as:
  ```
  public boolean await(long timeout, TimeUnit unit) throws InterruptedException
  ```
  Parameter  
  The method await(long timeout, TimeUnit unit) has the following parameter:
    + long timeout - the maximum time to wait
    + TimeUnit unit - the time unit of the timeout argument

  Return  
  The method await(long timeout, TimeUnit unit) returns true if the count reached zero and false if the
  waiting time elapsed before the count reached zero  
  Exception  
  The method await(long timeout, TimeUnit unit) throws the following exceptions:
    + InterruptedException - if the current thread is interrupted while waiting
+ getCount()  
  Introduction  
  Returns the current count.  
  This method is typically used for debugging and testing purposes.  
  Syntax  
  The method getCount() from CountDownLatch is declared as:
  ```
  public long getCount()
  ```
  Return  
  The method getCount() returns the current count
+ toString()  
  Introduction  
  Returns a string identifying this latch, as well as its state.  
  The state, in brackets, includes the String ("Count =") followed by the current count.  
  Syntax  
  The method toString() from CountDownLatch is declared as:
  ```
  public String toString()
  ```
  Return  
  The method toString() returns a string identifying this latch, as well as its state

### 1.2 CyclicBarrier

A synchronization aid that allows a set of threads to all wait for each other to reach a common barrier point.  
Introduction  
CyclicBarriers are useful in programs involving a fixed sized party of threads that must occasionally wait for each
other.  
The barrier is called cyclic because it can be re-used after the waiting threads are released.  
A CyclicBarrier supports an optional Runnable command that is run once per barrier point, after the last thread
in the party arrives, but before any threads are released.  
This barrier action is useful for updating shared-state before any of the parties continue.  
When all rows are processed the supplied Runnable barrier action is executed and merges the rows.  
If the merger determines that a solution has been found then done() will return true and each worker will
terminate.  
If the barrier action does not rely on the parties being suspended when it is executed, then any of the threads in the
party could execute that action when it is released.  
To facilitate this, each invocation of #await returns the arrival index of that thread at the barrier.  
You can then choose which thread should execute the barrier action, for example:

```
if (barrier.await() == 0){ }// log the completion of this iteration}
```

The CyclicBarrier uses an all-or-none breakage model for failed synchronization attempts:  
If a thread leaves a barrier point prematurely because of interruption, failure, or timeout, all other threads waiting
at that barrier point will also leave abnormally via BrokenBarrierException (or InterruptedException if they too
were interrupted at about the same time).  
Memory consistency effects: Actions in a thread prior to calling await() happen-before actions that are part of
the barrier action, which in turn happen-before actions following a successful return from the corresponding await()
in other threads.

We can useJavaCyclicBarrier class to make a set of two or more threads to wait at a predetermined execution point
until all threads in the set have reached that point.  
It enables you to define a synchronization object that suspends until the specified number of threads has reached the
barrier point.  
Java CyclicBarrier class has the following constructors:

```
CyclicBarrier(int numThreads)
CyclicBarrier(int numThreads, Runnable action)
```

Here,numThreads specifies the number of threads that must reach the barrier before execution continues.  
In the second form, action specifies a thread that will be executed when the barrier is reached.  
Here is the general procedure that you will follow to use CyclicBarrier.

1. Create a CyclicBarrier object, specifying the number of threads that you will be waiting for.
2. When each thread reaches the barrier, have it call await() on that object. This will pause execution of the
   thread until all of the other threads also call await().
3. Once the specified number of threads has reached the barrier, await() will return and execution will resume.

If you have specified an action, then that thread is executed.  
The await() method has the following two forms:

1. int await() throws InterruptedException, BrokenBarrierException
2. int await(long wait, TimeUnit tu) throws InterruptedException, BrokenBarrierException, TimeoutException

The first form waits until all the threads have reached the barrier point.  
The second form waits only for the period of time specified by wait.  
The units represented by wait are specified by tu.  
Both forms return a value that indicates the order that the threads arrive at the barrier point.  
The first thread returns a value equal to the number of threads waited upon minus one.  
The last thread returns zero.  
Example  
Here is an example that illustrates CyclicBarrier.  
It waits until a set of three threads has reached the barrier.  
When that occurs, the thread specified by ArriveAction executes.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-19 10:07
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    CyclicBarrier cyclicBarrier = new CyclicBarrier(3, new ArriveAction());
    System.out.println("Starting");
    new SubThread("A", cyclicBarrier);
    new SubThread("B", cyclicBarrier);
    new SubThread("C", cyclicBarrier);
  }

  private static class SubThread implements Runnable {
    private String name;
    private CyclicBarrier cyclicBarrier;

    public SubThread(String name, CyclicBarrier cyclicBarrier) {
      this.name = name;
      this.cyclicBarrier = cyclicBarrier;
      new Thread(this, name).start();
    }

    @Override
    public void run() {
      System.out.println(name);
      try {
        TimeUnit.MILLISECONDS.sleep(500);
        cyclicBarrier.await();
      } catch (InterruptedException | BrokenBarrierException e) {
        e.printStackTrace();
      }
    }
  }

  private static class ArriveAction implements Runnable { // An object of this class is called when the CyclicBarrier ends.
    @Override
    public void run() {
      System.out.println("Barrier Reached, Starting!!!");
    }
  }
}
```

A CyclicBarrier can be reused because it will release waiting threads each time the specified number of threads
calls await().

+ CyclicBarrier(int parties)  
  Java CyclicBarrier CyclicBarrier(int parties) Creates a new CyclicBarrier that will trip when the given number
  of parties (threads) are waiting upon it, and does not perform a predefined action when the barrier is tripped.  
  The method CyclicBarrier(int parties) is a constructor.  
  Syntax  
  The method CyclicBarrier(int parties) from CyclicBarrier is declared as:
  ```
  public CyclicBarrier(int parties)
  ```
  Parameter  
  The method CyclicBarrier(int parties) has the following parameter:
    + int parties - the number of threads that must invoke #await before the barrier is tripped

  Exception  
  The method CyclicBarrier(int parties) throws the following exceptions:
    + IllegalArgumentException - if parties is less than 1
+ CyclicBarrier(int parties, Runnable barrierAction)  
  Java CyclicBarrier CyclicBarrier(int parties, Runnable barrierAction) Creates a new CyclicBarrier that will
  trip when the given number of parties (threads) are waiting upon it, and which will execute the given barrier action
  when the barrier is tripped, performed by the last thread entering the barrier.  
  The method CyclicBarrier(int parties, Runnable barrierAction) is a constructor.  
  Syntax  
  The method CyclicBarrier(int parties, Runnable barrierAction) from CyclicBarrier is declared as:
  ```
  public CyclicBarrier(int parties, Runnable barrierAction)
  ```
  Parameter  
  The method CyclicBarrier(int parties, Runnable barrierAction) has the following parameter:
    + int parties - the number of threads that must invoke #await before the barrier is tripped
    + Runnable barrierAction - the command to execute when the barrier is tripped, or null if there is no action

  Exception  
  The method CyclicBarrier(int parties, Runnable barrierAction) throws the following exceptions:
    + IllegalArgumentException - if parties is less than 1
+ await()  
  Introduction  
  Waits until all #getParties parties have invoked await on this barrier.  
  If the current thread is not the last to arrive then it is disabled for thread scheduling purposes and lies dormant
  until one of the following things happens:
    + The last thread arrives; or
    + Some other thread Thread interrupt interrupts the current thread; or
    + Some other thread Thread interrupt interrupts one of the other waiting threads; or
    + Some other thread times out while waiting for barrier; or
    + Some other thread invokes #reset on this barrier.

  If the current thread:
    + has its interrupted status set on entry to this method; or
    + is Thread interrupt interrupted while waiting

  then InterruptedException is thrown and the current thread's interrupted status is cleared.  
  If the barrier is #reset while any thread is waiting, or if the barrier isBroken when await is invoked, or while
  any thread is waiting, then BrokenBarrierException is thrown.  
  If any thread is Thread interrupt interrupted while waiting, then all other waiting threads will throw 
  BrokenBarrierException and the barrier is placed in the broken state.  
  If the current thread is the last thread to arrive, and a non-null barrier action was supplied in the constructor,
  then the current thread runs the action before allowing the other threads to continue.  
  If an exception occurs during the barrier action then that exception will be propagated in the current thread and the
  barrier is placed in the broken state.  
  Syntax  
  The method await() from CyclicBarrier is declared as:
  ```
  public int await() throws InterruptedException, BrokenBarrierException
  ```
  Return  
  The method await() returns the arrival index of the current thread, where index (getParties() - 1) indicates
  the first to arrive and zero indicates the last to arrive  
  Exception  
  The method await() throws the following exceptions:
    + InterruptedException - if the current thread was interrupted while waiting
    + BrokenBarrierException - if another thread was interrupted or timed out while the current thread was waiting,
      or the barrier was reset, or the barrier was broken when await was called, or the barrier action (if present)
      failed due to an exception

### 1.3 Exchanger

Introduction  
A synchronization point at which threads can pair and swap elements within pairs.  
Each thread presents some object on entry to the (#exchange exchange) method, matches with a partner thread, and
receives its partner's object on return.  
An Exchanger may be viewed as a bidirectional form of a SynchronousQueue.  
Exchangers may be useful in applications such as genetic algorithms and pipeline designs.  
Memory consistency effects: For each pair of threads that successfully exchange objects via an Exchanger,
actions prior to the exchange() in each thread happen-before those subsequent to a return from the corresponding
exchange() in the other thread.

Java Exchanger can simplify the exchange of data between two threads.  
The Exchanger waits until two separate threads call its exchange() method.  
When that occurs, it exchanges the data supplied by the threads.  
Exchanger is a generic class that is declared as shown here:

```
Exchanger<V>
```

Here, V specifies the type of the data being exchanged.  
The only method defined by Exchanger is exchange(), which has the two forms shown here:

```
V exchange(V objRef) throws InterruptedException
V exchange(V objRef, long wait, TimeUnit tu) throws InterruptedException, TimeoutException
```

Here, objRef is a reference to the data to exchange.  
The data received from the other thread is returned.  
The second form of exchange() allows a time-out period to be specified.  
The exchange() method synchronizes the exchange of the data.

Example  
Here is an example that demonstrates Exchanger.  
It creates two threads. One thread creates an empty buffer that will receive the data put into it by the second
thread.  
The first thread exchanges an empty string for a full one.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Exchanger;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2021/12/30-18:53
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    Exchanger<String> exchanger = new Exchanger<>();
    new UseString(exchanger);
    new MakeString(exchanger);
  }

  private static class MakeString implements Runnable {
    private Exchanger<String> exchanger;
    private String str;

    public MakeString(Exchanger<String> exchanger) {
      this.exchanger = exchanger;
      new Thread(this).start();
    }

    public void run() {
      for (int i = 1; i <= 3; i++) {
        str = "i = " + i;
        try {
          str = exchanger.exchange(str); // Exchange a full buffer for an empty one.
        } catch (InterruptedException exc) {
          exc.printStackTrace();
        }
      }
    }
  }

  private static class UseString implements Runnable { // A Thread that uses a string.
    private Exchanger<String> exchanger;
    private String str;

    public UseString(Exchanger<String> exchanger) {
      this.exchanger = exchanger;
      new Thread(this).start();
    }

    public void run() {
      for (int i = 1; i <= 3; i++) {
        try {
          str = exchanger.exchange(""); // Exchange an empty buffer for a full one.
          System.out.println("Got: " + str);
        } catch (InterruptedException exc) {
          exc.printStackTrace();
        }
      }
    }
  }
}
```

Note  
In the program, the main() method creates an Exchanger for strings.  
This object is then used to synchronize the exchange of strings between the MakeString and UseString classes.  
The MakeString class fills a string with data.  
The UseString exchanges an empty string for a full one.  
It then displays the contents of the newly constructed string.

+ Exchanger()  
  Java Exchanger Exchanger() Creates a new Exchanger.  
  The method Exchanger() is a constructor.  
  Syntax  
  The method Exchanger() from Exchanger is declared as:
  ```
  public Exchanger()
  ```

### 1.4 Semaphore

Introduction  
A counting semaphore.  
Conceptually, a semaphore maintains a set of permits.  
Each #acquire blocks if necessary until a permit is available, and then takes it.  
Each #release adds a permit, potentially releasing a blocking acquirer.  
However, no actual permit objects are used; the Semaphore just keeps a count of the number available and acts
accordingly.  
Semaphores are often used to restrict the number of threads than can access some (physical or logical) resource.  
When the thread has finished with the item it is returned back to the pool and a permit is returned to the semaphore,
allowing another thread to acquire that item.  
Note that no synchronization lock is held when #acquire is called as that would prevent an item from being returned
to the pool.  
The semaphore encapsulates the synchronization needed to restrict access to the pool, separately from any
synchronization needed to maintain the consistency of the pool itself.  
A semaphore initialized to one, and which is used such that it only has at most one permit available, can serve as a
mutual exclusion lock.  
This is more commonly known as a binary semaphore, because it only has two states: one permit available, or zero permits
available.  
When used in this way, the binary semaphore has the property (unlike many java.util.concurrent.locks.Lock
implementations), that the &quot;lock&quot; can be released by a thread other than the owner (as semaphores have no
notion of ownership).  
This can be useful in some specialized contexts, such as deadlock recovery.  
The constructor for this class optionally accepts a fairness parameter.  
When set false, this class makes no guarantees about the order in which threads acquire permits.  
In particular, barging is permitted, that is, a thread invoking #acquire can be allocated a permit ahead of a thread
that has been waiting - logically the new thread places itself at the head of the queue of waiting threads.  
When fairness is set true, the semaphore guarantees that threads invoking any of the (#acquire() acquire) methods
are selected to obtain permits in the order in which their invocation of those methods was processed (
first-in-first-out; FIFO).  
Note that FIFO ordering necessarily applies to specific internal points of execution within these methods.  
So, it is possible for one thread to invoke acquire before another, but reach the ordering point after the other,
and similarly upon return from the method.  
Also note that the untimed (#tryAcquire() tryAcquire) methods do not honor the fairness setting, but will take any
permits that are available.  
Generally, semaphores used to control resource access should be initialized as fair, to ensure that no thread is starved
out from accessing a resource.  
When using semaphores for other kinds of synchronization control, the throughput advantages of non-fair ordering often
outweigh fairness considerations.  
This class also provides convenience methods to (#acquire(int) acquire) and (#release(int) release) multiple
permits at a time.  
These methods are generally more efficient and effective than loops.  
However, they do not establish any preference order.  
For example, if thread A invokes s.acquire(3) and thread B invokes s.acquire(2), and two permits become
available, then there is no guarantee that thread B will obtain them unless its acquire came first and Semaphore s
is in fair mode.  
Memory consistency effects: Actions in a thread prior to calling a "release" method such as release() happen-before
actions following a successful "acquire" method such as acquire() in another thread.

The synchronization object Semaphore implements a classic semaphore.  
A semaphore controls access to a shared resource in the following way:

1. If the counter is greater than zero, then access is allowed.
2. If it is zero, then access is denied.
3. What the counter is counting are permits that allow access to the shared resource.
4. To access the resource, a thread must be granted a permit from the semaphore.

To use a semaphore, the thread that wants access to the shared resource tries to acquire a permit.  
If the semaphore's count is greater than zero, then the thread acquires a permit, which causes the semaphore's count to
be decremented.  
Otherwise, the thread will be blocked until a permit can be acquired.  
When the thread no longer needs access to the shared resource, it releases the permit, which causes the semaphore's
count to be incremented.  
If there is another thread waiting for a permit, then that thread will acquire a permit at that time.

Java's Semaphore class implements this mechanism.  
Semaphore has the two constructors shown here:

```
Semaphore(int num)
Semaphore(int num, boolean how)
```

Here, num specifies the initial permit count.  
Thus, num specifies the number of threads that can access a resource at any one time.  
If num is one, then only one thread can access the resource at any one time.  
By default, waiting threads are granted a permit in an undefined order.  
By setting how to true, you can ensure that waiting threads are granted a permit in the order in which they
requested access.  
To acquire a permit, call the acquire() method, which has these two forms:

```
void acquire() throws InterruptedException
void acquire(int  num) throws InterruptedException
```

The first form acquires one permit.  
The second form acquires num permits.  
The first form is used much often.  
If the permit cannot be granted at the time of the call, then the invoking thread suspends until the permit is
available.  
To release a permit, call release(), which has these two forms:

```
void release()
void release(int  num)
```

The first form releases one permit. The second form releases the number of permits specified by num.  
To use a semaphore, each thread that wants to use that resource must first call acquire() before accessing the
resource.  
When the thread is done with the resource, it must call release().  
Here is an example that illustrates the use of a semaphore:

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-19 9:02
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    Semaphore semaphore = new Semaphore(1);
    new ProducerThread("Producer", semaphore);
    new ConsumerThread("Consumer", semaphore);
  }

  private static class SharedResource { // A shared resource.
    static int count = 0;
  }

  private static class ProducerThread implements Runnable { // A thread of execution that increments count.
    private String name;
    private Semaphore semaphore;

    public ProducerThread(String name, Semaphore semaphore) {
      this.name = name;
      this.semaphore = semaphore;
      new Thread(this, name).start();
    }

    @Override
    public void run() {
      System.out.println("Starting " + name);
      try {
        // First, get a permit
        System.out.println(name + " is waiting for a permit.");
        semaphore.acquire();
        System.out.println(name + " gets a permit.");
        // now, access shared resource
        for (int i = 0; i < 5; i++) {
          SharedResource.count++;
          System.out.println(name + ": " + SharedResource.count);
          TimeUnit.SECONDS.sleep(1);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // Release the permit.
      System.out.println(name + " release the permit.");
      semaphore.release();
    }
  }

  private static class ConsumerThread implements Runnable { // A thread of execution that decrement count.
    private String name;
    private Semaphore semaphore;

    public ConsumerThread(String name, Semaphore semaphore) {
      this.name = name;
      this.semaphore = semaphore;
      new Thread(this, name).start();
    }

    @Override
    public void run() {
      System.out.println("Starting " + name);
      try {
        // First, get a permit.
        System.out.println(name + " is waiting for a permit.");
        semaphore.acquire();
        System.out.println(name + " gets a permit");
        // access shared resource
        for (int i = 0; i < 5; i++) {
          SharedResource.count--;
          System.out.println(name + ": " + SharedResource.count);
          TimeUnit.MILLISECONDS.sleep(500);
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      // Release the permit.
      System.out.println(name + " releases the permit");
      semaphore.release();
    }
  }
}
```

Note  
The program uses a semaphore to control access to the count variable, which is a static variable within the
SharedResource class.  
SharedResource.count is incremented five times by the run() method of ProducerThread and decremented five
times by
ConsumerThread.  
To prevent these two threads from accessing SharedResource.count at the same time, access is allowed only after a
permit is acquired from the controlling semaphore.  
After access is complete, the permit is released.  
In this way, only one thread at a time will access SharedResource.count, as the output shows.  
In both ProducerThread and ConsumerThread, notice the call to sleep() within run().  
It is used to "prove" that accesses to SharedResource.count are synchronized by the semaphore.  
In run(), the call to sleep() causes the invoking thread to pause between each access to 
SharedResource.count.  
This would normally enable the second thread to run.  
However, because of the semaphore, the second thread must wait until the first has released the permit, which happens
only after all accesses by the first thread are complete.  
Thus, SharedResource.count is first incremented five times by ProducerThread and then decremented five times by
ConsumerThread.  
The increments and decrements are not intermixed.  
Without the use of the semaphore, accesses to SharedResource.count by both threads would have occurred
simultaneously, and the increments and decrements would be intermixed.  
To confirm this, try commenting out the calls to acquire() and release().  
When you run the program, you will see that access to SharedResource.count is no longer synchronized, and each
thread accesses it as soon as it gets a time slice.

Java semaphore producer consumer example  
The following code solves the producer consumer synchronization problem by using semaphore.  
It is an implementation of a producer and consumer that use semaphores to control synchronization.  
It uses two semaphores to regulate the producer and consumer threads, ensuring that each call to put() is followed
by a corresponding call to get():

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Semaphore;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-19 9:22
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    SharedResource sharedResource = new SharedResource();
    new ConsumerThread(sharedResource);
    new ProducerThread(sharedResource);
  }

  private static class SharedResource {
    private int count;
    private static Semaphore consumerSemaphore = new Semaphore(0); // Start  with consumer semaphore unavaliable.
    private static Semaphore producerSemaphore = new Semaphore(1);

    private void get() {
      try {
        consumerSemaphore.acquire();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      System.out.println("Got: " + count);
      producerSemaphore.release();
    }

    private void put(int count) {
      try {
        producerSemaphore.acquire();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      this.count = count;
      System.out.println("Put: " + count);
      consumerSemaphore.release();
    }
  }

  private static class ConsumerThread implements Runnable {
    private SharedResource sharedResource;

    public ConsumerThread(SharedResource sharedResource) {
      this.sharedResource = sharedResource;
      new Thread(this, "Consumer").start();
    }

    @Override
    public void run() {
      for (int i = 1; i <= 10; i++) {
        sharedResource.get();
      }
    }
  }

  private static class ProducerThread implements Runnable {
    private SharedResource sharedResource;

    public ProducerThread(SharedResource sharedResource) {
      this.sharedResource = sharedResource;
      new Thread(this, "Producer").start();
    }

    @Override
    public void run() {
      for (int i = 1; i <= 10; i++) {
        sharedResource.put(i);
      }
    }
  }
}
```

Note  
The calls to put() and get() are synchronized.  
Each call to put() is followed by a call to get() and no values are missed.  
The sequencing of put() and get() calls is handled by two semaphores: producerSemaphore and 
consumerSemaphore.  
Before put() can produce a value, it must acquire a permit from producerSemaphore.  
After it has set the value, it releases consumerSemaphore.  
Before get() can consume a value, it must acquire a permit from consumerSemaphore.  
After it consumes the value, it releases producerSemaphore.  
consumerSemaphore is initialized with no available permits.  
This ensures that put() executes first.

+ Semaphore(int permits)  
  Java Semaphore Semaphore(int permits) Creates a Semaphore with the given number of permits and nonfair
  fairness setting.  
  The method Semaphore(int permits) is a constructor.  
  Syntax  
  The method Semaphore(int permits) from Semaphore is declared as:
  ```
  public Semaphore(int permits)
  ```
  Parameter  
  The method Semaphore(int permits) has the following parameter:
    + int permits - the initial number of permits available. This value may be negative, in which case releases must
      occur before any acquires will be granted.
+ Semaphore(int permits, boolean fair)  
  Java Semaphore Semaphore(int permits, boolean fair) Creates a Semaphore with the given number of permits and
  the given fairness setting.  
  The method Semaphore(int permits, boolean fair) is a constructor.  
  Syntax  
  The method Semaphore(int permits, boolean fair) from Semaphore is declared as:
  ```
  public Semaphore(int permits, boolean fair)
  ```
  Parameter  
  The method Semaphore(int permits, boolean fair) has the following parameter:
    + int permits - the initial number of permits available. This value may be negative, in which case releases must
      occur before any acquires will be granted.
    + boolean fair - true if this semaphore will guarantee first-in first-out granting of permits under
      contention, else false
+ acquire()  
  Introduction  
  Acquires a permit from this semaphore, blocking until one is available, or the thread is Thread#interrupt
  interrupted.  
  Acquires a permit, if one is available and returns immediately, reducing the number of available permits by one.  
  If no permit is available then the current thread becomes disabled for thread scheduling purposes and lies dormant
  until one of two things happens:
    + Some other thread invokes the #release method for this semaphore and the current thread is next to be assigned a
      permit; or
    + Some other thread Thread#interrupt interrupts the current thread.  
      If the current thread:
    + has its interrupted status set on entry to this method; or
    + is Thread#interrupt interrupted while waiting for a permit,  
      then InterruptedException is thrown and the current thread's interrupted status is cleared.

  Syntax  
  The method acquire() from Semaphore is declared as:
  ```
  public void acquire() throws InterruptedException
  ```
  Exception  
  The method acquire() throws the following exceptions:
    + InterruptedException - if the current thread is interrupted
+ release()  
  Introduction  
  Releases a permit, returning it to the semaphore.  
  Releases a permit, increasing the number of available permits by one.  
  If any threads are trying to acquire a permit, then one is selected and given the permit that was just released.  
  That thread is (re)enabled for thread scheduling purposes.  
  There is no requirement that a thread that releases a permit must have acquired that permit by calling #acquire.
  Correct usage of a semaphore is established by programming convention in the application.  
  Syntax  
  The method release() from Semaphore is declared as:
  ```
  public void release()
  ```

### 1.5 Phaser

Synchronization class Phaser enables the synchronization of threads that represent one or more phases of activity.  
AJavaPhaser works like a CyclicBarrier except that it supports multiple phases.  
Phaser lets you define a synchronization object that waits until a specific phase has completed.  
It then advances to the next phase, again waiting until that phase concludes.  
Phaser can also be used to synchronize only a single phase.  
In that case, it acts much like a CyclicBarrier.  
Phaser defines four constructors.  
Here are the two used in this section:

```
Phaser()
Phaser(int numParties)
```

The first creates a phaser that has a registration count of zero.  
The second sets the registration count to numParties.  
The term party is often applied to the objects that register with a phaser.  
In both cases, the current phase is zero. When a Phaser is created, it is initially at phase zero.  
In general, here is how you use Phaser.  
First, create a new instance of Phaser.  
Next, register one or more parties with the phaser, either by calling register() or by specifying the number of
parties in the constructor.  
For each registered party, have the phaser wait until all registered parties complete a phase.  
A party signals this by calling one of a variety of methods supplied by Phaser, such as arrive() or 
arriveAndAwaitAdvance().  
After all parties have arrived, the phase is complete, and the phaser can move on to the next phase if there is one, or
terminate.

Usage  
To register parties after a Phaser has been constructed, call register().

```
int register()
```

It returns the phase number of the phase to which it is registered.  
To signal that a party has completed a phase, it must call arrive() or some variation of arrive().  
When the number of arrivals equals the number of registered parties, the phase is completed and the Phaser moves on
to the next phase if there is one.  
The arrive() method has this general form:

```
int arrive()
```

This method signals that a thread has completed some task or portion of a task.  
It returns the current phase number.  
If the phaser has been terminated, then it returns a negative value.  
The arrive() method does not suspend execution of the calling thread.  
This means that it does not wait for the phase to be completed.  
This method should be called only by a registered party.  
To indicate the completion of a phase and then wait until all other registrants have also completed that phase, use 
arriveAndAwaitAdvance().

```
int arriveAndAwaitAdvance()
```

It waits until all parties have arrived.  
It returns the next phase number or a negative value if the phaser has been terminated.  
This method should be called only by a registered party.  
A thread can arrive and then deregister itself by calling arriveAndDeregister().  
It is shown here:

```
int arriveAndDeregister()
```

It returns the current phase number or a negative value if the phaser has been terminated.  
It does not wait until the phase is complete.  
This method should be called only by a registered party.  
To obtain the current phase number, call getPhase(), which is shown here:

```
final int getPhase()
```

When a Phaser is created, the first phase will be 0, the second phase 1, the third phase 2, and so on.  
A negative value is returned if the invoking Phaser has been terminated.

Example  
Here is an example that shows how to use Phaser.  
It creates three threads, each of which have three phases.  
It uses a Phaser to synchronize each phase.

```java
package cn.tuyucheng.edu.classes;

import java.util.concurrent.Phaser;

import static java.util.concurrent.TimeUnit.SECONDS;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2021/12/30-18:57
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    Phaser phaser = new Phaser(1);
    int currentPhase;
    System.out.println("Starting");
    new SubThread(phaser, "A");
    new SubThread(phaser, "B");
    new SubThread(phaser, "C");

    currentPhase = phaser.getPhase(); // Wait for all threads to complete phase one.
    phaser.arriveAndAwaitAdvance();
    System.out.println("Phase " + currentPhase + " Complete");

    currentPhase = phaser.getPhase(); // Wait for all threads to complete phase two.
    phaser.arriveAndAwaitAdvance();
    System.out.println("Phase " + currentPhase + " Complete");

    currentPhase = phaser.getPhase();
    phaser.arriveAndAwaitAdvance();
    System.out.println("Phase " + currentPhase + " Complete");

    phaser.arriveAndDeregister(); // Unregister the main thread.
    if (phaser.isTerminated())
      System.out.println("The Phaser is terminated");
  }

  private static class SubThread implements Runnable { // A thread of execution that uses a Phaser.
    private Phaser phaser;
    private String name;

    public SubThread(Phaser phaser, String name) {
      this.phaser = phaser;
      this.name = name;
      this.phaser.register();
      new Thread(this).start();
    }

    public void run() {
      System.out.println("Thread " + name + " Beginning Phase One");
      phaser.arriveAndAwaitAdvance(); // Signal arrival.

      try {
        SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Thread " + name + " Beginning Phase Two");
      phaser.arriveAndAwaitAdvance(); // Signal arrival.

      try {
        SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }

      System.out.println("Thread " + name + " Beginning Phase Three");
      phaser.arriveAndDeregister(); // Signal arrival and unregister.
    }
  }
}
```

Note  
First, in main(), a Phaser called phaser is created with an initial party count of 1 which corresponds to
the main thread.  
Then three threads are started by creating three SubThread objects.  
SubThread is passed a reference to phaser, the phaser.  
The SubThread objects use this phaser to synchronize their activities.  
main() calls getPhase() to obtain the current phase number which is initially zero and then calls 
arriveAndAwaitAdvance().  
This causes main() to suspend until phase zero has completed.  
This won't happen until all SubThread also call arriveAndAwaitAdvance().  
When this occurs, main() will resume execution, at which point it displays that phase zero has completed, and it
moves on to the next phase.  
This process repeats until all three phases have finished.  
Then, main() calls arriveAndDeregister().  
At that point, all three SubThreads have also unregistered.  
Since this results in there being no registered parties when the phaser advances to the next phase, the phaser is
terminated.  
SubThread's constructor is passed a reference to the phaser that it will use and then registers with the new thread as a
party on that phaser.  
Thus, each new SubThread becomes a party registered with the passed-in phaser.  
Each thread has three phases.  
In this example, each phase consists of a placeholder that displays the name of the thread and what it is doing.  
Between the first two phases, the thread calls arriveAndAwaitAdvance().  
Thus, each thread waits until all threads have completed the phase and the main thread is ready.  
After all threads have arrived including the main thread, the phaser moves on to the next phase.  
After the third phase, each thread unregisters itself with a call to arriveAndDeregister().  
Each party that uses a phaser can be unique, with each performing some separate task.

We can control precisely what happens when a phase advance occurs by overriding the onAdvance() method.  
This method is called by the run time when a Phaser advances from one phase to the next.  
It is shown here:

```
protected boolean onAdvance(int phase, int numParties)
```

Here, phase will contain the current phase number prior to being incremented and numParties will contain the number
of registered parties.  
To terminate the phaser, onAdvance() must return true.  
To keep the phaser alive, onAdvance() must return false.  
The default version of onAdvance() returns true thus terminating the phaser when there are no registered parties.  
The override should follow this practice.  
The override of onAdvance() can enable a phaser to execute a specific number of phases and then stop.

Example  
The following example creates a class called MyPhaser that extends Phaser so that it will run a specified number
of phases.  
It does this by overriding the onAdvance() method.  
The MyPhaser constructor accepts one argument, which specifies the number of phases to execute.  
MyPhaser automatically registers one party.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-02 3:15
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    MyPhaser phaser = new MyPhaser(1, 4);
    System.out.println("Startingn");
    new SubThread(phaser, "A");
    new SubThread(phaser, "B");
    new SubThread(phaser, "C");
    while (!phaser.isTerminated()) { // Wait for the specified number of phases to complete.
      phaser.arriveAndAwaitAdvance();
    }
    System.out.println("The Phaser is terminated");
  }

  private static class MyPhaser extends Phaser {
    private int numPhases;

    public MyPhaser(int parties, int numPhases) {
      super(parties);
      this.numPhases = numPhases - 1;
    }

    @Override
    protected boolean onAdvance(int phase, int registeredParties) {
      System.out.println("Phase " + phase + " completed.n");
      // If all phases have completed, return true
      return phase == numPhases || registeredParties == 0;
    }
  }

  private static class SubThread implements Runnable {
    private Phaser phaser;
    private String name;

    public SubThread(Phaser phaser, String name) {
      this.phaser = phaser;
      this.name = name;
      this.phaser.register();
      new Thread(this, name).start();
    }

    @Override
    public void run() {
      while (!this.phaser.isTerminated()) {
        System.out.println("Thread " + name + " Beginning Phase " + phaser.getPhase());
        phaser.arriveAndAwaitAdvance();
        try {
          TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
```

Note  
Inside main(), one instance of Phaser is created.  
It is passed 4 as an argument, which means that it will execute four phases and then stop.  
Next, three threads are created and then the following loop is entered:

```
while (!phaser.isTerminated()) { // Wait for the specified number of phases to complete.
            phaser.arriveAndAwaitAdvance();
        }
```

This loop calls arriveAndAwaitAdvance() until the phaser is terminated.  
The phaser won't terminate until the specified number of phases have been executed.  
In this case, the loop continues to execute until four phases have run.  
The threads call arriveAndAwaitAdvance() within a loop that runs until the phaser is terminated.  
They will execute until the specified number of phases has been completed.  
Each time onAdvance() is called, it is passed the current phase and the number of registered parties.  
If the current phase equals the specified phase, or if the number of registered parties is zero, onAdvance() returns
true, thus stopping the phaser.  
This is accomplished with this line of code:

```
return phase == numPhases || registeredParties == 0;
```

You can wait for a specific phase by calling awaitAdvance(), which is shown here:

```
int awaitAdvance(int phase )
```

Here, phase indicates the phase number on which awaitAdvance() will wait until a transition to the next phase
takes place.  
It will return immediately if the argument passed to phase is not equal to the current phase.  
It will also return immediately if the phaser is terminated.  
If phase is passed the current phase, then it will wait until the phase increments.  
This method should be called only by a registered party.  
There is also an interruptible version of this method called awaitAdvanceInterruptibly().  
To register more than one party, call bulkRegister().  
To obtain the number of registered parties, call getRegisteredParties().  
You can obtain the number of arrived parties and unarrived parties by calling getArrivedParties() and 
getUnarrivedParties(), respectively.  
To force the phaser to enter a terminated state, call forceTermination().  
Phaser also lets you create a tree of phasers.  
This is supported by two additional constructors, which let you specify the parent, and the getParent() method.

Introduction  
A reusable synchronization barrier, similar in functionality to CyclicBarrier and CountDownLatch but supporting
more flexible usage.  
Registration.  
Unlike the case for other barriers, the number of parties registered to synchronize on a phaser may vary over time.  
Tasks may be registered at any time (using methods #register, #bulkRegister, or forms of constructors
establishing initial numbers of parties), and optionally deregistered upon any arrival (using #arriveAndDeregister)
.  
As is the case with most basic synchronization constructs, registration and deregistration affect only internal counts;
they do not establish any further internal bookkeeping, so tasks cannot query whether they are registered.  
(However, you can introduce such bookkeeping by subclassing this class.) Synchronization.  
Like a CyclicBarrier, a Phaser may be repeatedly awaited.  
Method #arriveAndAwaitAdvance has effect analogous to (java.util.concurrent.CyclicBarrier#await
CyclicBarrier.await).  
Each generation of a phaser has an associated phase number.  
The phase number starts at zero, and advances when all parties arrive at the phaser, wrapping around to zero after
reaching Integer.MAX_VALUE.  
The use of phase numbers enables independent control of actions upon arrival at a phaser and upon awaiting others, via
two kinds of methods that may be invoked by any registered party:  
Arrival  
Methods #arrive and #arriveAndDeregister record arrival.  
These methods do not block, but return an associated arrival phase number; that is, the phase number of the phaser to
which the arrival applied.  
When the final party for a given phase arrives, an optional action is performed and the phase advances.  
These actions are performed by the party triggering a phase advance, and are arranged by overriding method (onAdvance(
int, int)), which also controls termination.  
Overriding this method is similar to, but more flexible than, providing a barrier action to a CyclicBarrier.  
Waiting  
Method #awaitAdvance requires an argument indicating an arrival phase number, and returns when the phaser advances
to (or is already at) a different phase.  
Unlike similar constructions using CyclicBarrier, method awaitAdvance continues to wait even if the waiting
thread is interrupted.  
Interruptible and timeout versions are also available, but exceptions encountered while tasks wait interruptibly or with
timeout do not change the state of the phaser.  
If necessary, you can perform any associated recovery within handlers of those exceptions, often after invoking 
forceTermination.  
Phasers may also be used by tasks executing in a ForkJoinPool.  
Progress is ensured if the pool's parallelism level can accommodate the maximum number of simultaneously blocked
parties.  
Termination  
A phaser may enter a termination state, that may be checked using method #isTerminated.  
Upon termination, all synchronization methods immediately return without waiting for advance, as indicated by a negative
return value.  
Similarly, attempts to register upon termination have no effect.  
Termination is triggered when an invocation of onAdvance returns true.  
The default implementation returns true if a deregistration has caused the number of registered parties to become
zero.  
As illustrated below, when phasers control actions with a fixed number of iterations, it is often convenient to override
this method to cause termination when the current phase number reaches a threshold.  
Method #forceTermination is also available to abruptly release waiting threads and allow them to terminate.  
Tiering  
Phasers may be tiered (i.e., constructed in tree structures) to reduce contention.  
Phasers with large numbers of parties that would otherwise experience heavy synchronization contention costs may instead
be set up so that groups of sub-phasers share a common parent.  
This may greatly increase throughput even though it incurs greater per-operation overhead.  
In a tree of tiered phasers, registration and deregistration of child phasers with their parent are managed
automatically.  
Whenever the number of registered parties of a child phaser becomes non-zero (as established in the #Phaser(
Phaser,int) constructor, #register, or #bulkRegister), the child phaser is registered with its parent.  
Whenever the number of registered parties becomes zero as the result of an invocation of #arriveAndDeregister, the
child phaser is deregistered from its parent.  
Monitoring  
While synchronization methods may be invoked only by registered parties, the current state of a phaser may be monitored
by any caller.  
At any given moment there are #getRegisteredParties parties in total, of which #getArrivedParties have arrived
at the current phase (#getPhase).  
When the remaining (#getUnarrivedParties) parties arrive, the phase advances.  
The values returned by these methods may reflect transient states and so are not in general useful for synchronization
control.  
Method #toString returns snapshots of these state queries in a form convenient for informal monitoring.  
Sample usages: A Phaser may be used instead of a CountDownLatch to control a one-shot action serving a variable
number of parties.  
The typical idiom is for the method setting this up to first register, then start all the actions, then deregister, as
in:

```
(void runTasks(List<Runnable> tasks) {
   Phaser startingGate = new Phaser(1); // "1" to register self
   // create and start threads
   for (Runnable task : tasks) {
      startingGate.register();
      new Thread(() -> {
          startingGate.arriveAndAwaitAdvance();
          task.run();)).start();
      }
      // deregister self to allow threads to proceed
      startingGate.arriveAndDeregister();
   }
}
```

One way to cause a set of threads to repeatedly perform actions for a given number of iterations is to override 
onAdvance:

```
(void startTasks(List<Runnable> tasks, int iterations) {
   Phaser phaser = new Phaser() {
      protected boolean onAdvance(int phase, int registeredParties) {
         return phase >= iterations - 1 || registeredParties == 0;)
      };
      phaser.register();
      for (Runnable task : tasks) {
         phaser.register();
         new Thread(() -> {
            do {
              task.run();
              phaser.arriveAndAwaitAdvance();
            } while (!phaser.isTerminated());
         }).start();
      }
      // allow threads to proceed; don't wait for them
      phaser.arriveAndDeregister();
   }
}
```

If the main task must later await termination, it may re-register and then execute a similar loop:

```
phaser.register(); 
   while (!phaser.isTerminated()) phaser.arriveAndAwaitAdvance();
```

Related constructions may be used to await particular phase numbers in contexts where you are sure that the phase will
never wrap around Integer.MAX_VALUE.  
For example:

```
(void awaitPhase(Phaser phaser, int phase) {
   int p = phaser.register(); // assumes caller not already registered
   while (p < phase) {
      if (phaser.isTerminated())
         // ... deal with unexpected termination 
      else 
        p = phaser.arriveAndAwaitAdvance();) 
        phaser.arriveAndDeregister(); 
   }
}
```

To create a set of n tasks using a tree of phasers, you could use code of the following form, assuming a Task class with
a constructor accepting a Phaser that it registers with upon construction.  
After invocation of (build(new Task[n], 0, n,new Phaser())), these tasks could then be started, for example by
submitting to a pool:

```
(void build(Task[] tasks, int lo, int hi, Phaser ph) {
   if (hi - lo > TASKS_PER_PHASER) {
      for (int i = lo; i < hi; i += TASKS_PER_PHASER) {
         int j = Math.min(i + TASKS_PER_PHASER, hi);
         build(tasks, i, j, new Phaser(ph));)
      } else {
        for (int i = lo; i < hi; ++i)
           tasks[i] = new Task(ph);
        // assumes new Task(ph) performs ph.register()
     }
   }
}
```

The best value of TASKS_PER_PHASER depends mainly on expected synchronization rates.  
A value as low as four may be appropriate for extremely small per-phase task bodies (thus high rates), or up to hundreds
for extremely large ones.  
Implementation notes: This implementation restricts the maximum number of parties to 65535.  
Attempts to register additional parties result in IllegalStateException.  
However, you can and should create tiered phasers to accommodate arbitrarily large sets of participants.However, you can
and should create tiered phasers to accommodate arbitrarily large sets of participants.

+ Phaser()  
  Introduction  
  Creates a new phaser with no initially registered parties, no parent, and initial phase number 0.  
  Any thread using this phaser will need to first register for it.  
  The method Phaser() is a constructor.  
  Syntax  
  The method Phaser() from Phaser is declared as:
  ```
  public Phaser()
  ```

+ Phaser(Phaser parent)  
  Java Phaser Phaser(Phaser parent) Equivalent to (#Phaser(Phaser, int) Phaser(parent, 0)).  
  The method Phaser(Phaser parent) is a constructor.  
  Syntax  
  The method Phaser(Phaser parent) from Phaser is declared as:
  ```
  public Phaser(Phaser parent)
  ```
  Parameter  
  The method Phaser(Phaser parent) has the following parameter:
    + Phaser parent - the parent phaser

  Example  
  The following code shows how to useJavaPhaser Phaser(Phaser parent)

```java
package cn.tuyucheng.edu.concurrent;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-20 10:00
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    Phaser phaser = new Phaser(1);
    for (int i = 1; i <= 3; i++) {
      phaser.register();
      String taskName = "Task #" + i;
      StartTogetherTask task = new StartTogetherTask(phaser, taskName);
      task.start();
    }
    phaser.arriveAndDeregister();
  }

  private static class StartTogetherTask extends Thread {
    private Phaser phaser;
    private String taskName;
    private static final Random RANDOM = new Random();

    public StartTogetherTask(Phaser phaser, String taskName) {
      this.phaser = phaser;
      this.taskName = taskName;
    }

    @Override
    public void run() {
      System.out.println(taskName + ":Initializing...");
      int sleepTime = RANDOM.nextInt(5) + 1;
      try {
        TimeUnit.SECONDS.sleep(sleepTime);
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.out.println(taskName + "Initialized...");
      phaser.arriveAndAwaitAdvance();
      System.out.println(taskName + ":Started...");
    }
  }
}
  ```

+ Phaser(int parties)  
  Java Phaser Phaser(int parties) Creates a new phaser with the given number of registered unarrived parties, no
  parent, and initial phase number 0.  
  The method Phaser(int parties) is a constructor.  
  Syntax  
  The method Phaser(int parties) from Phaser is declared as:
  ```
  public Phaser(int parties)
  ```
  Parameter  
  The method Phaser(int parties) has the following parameter:
    + int parties - the number of parties required to advance to the next phase

  Exception  
  The method Phaser(int parties) throws the following exceptions:
    + IllegalArgumentException - if parties less than zero or greater than the maximum number of parties supported


+ arriveAndAwaitAdvance()  
  Introduction  
  Arrives at this phaser and awaits others.  
  Equivalent in effect to awaitAdvance(arrive()).  
  If you need to await with interruption or timeout, you can arrange this with an analogous construction using one of
  the other forms of the awaitAdvance method.  
  If instead you need to deregister upon arrival, use awaitAdvance(arriveAndDeregister()).  
  It is a usage error for an unregistered party to invoke this method.  
  However, this error may result in an IllegalStateException only upon some subsequent operation on this phaser, if
  ever.  
  Syntax  
  The method arriveAndAwaitAdvance() from Phaser is declared as:
  ```
  public int arriveAndAwaitAdvance()
  ```
  Return  
  The method arriveAndAwaitAdvance() returns the arrival phase number, or the (negative) #getPhase() current
  phase if terminated  
  Exception  
  The method arriveAndAwaitAdvance() throws the following exceptions:
    + IllegalStateException - if not terminated and the number of unarrived parties would become negative


+ arriveAndDeregister()  
  Introduction  
  Arrives at this phaser and deregisters from it without waiting for others to arrive.  
  Deregistration reduces the number of parties required to advance in future phases.  
  If this phaser has a parent, and deregistration causes this phaser to have zero parties, this phaser is also
  deregistered from its parent.  
  It is a usage error for an unregistered party to invoke this method.  
  However, this error may result in an IllegalStateException only upon some subsequent operation on this phaser, if
  ever.  
  Syntax  
  The method arriveAndDeregister() from Phaser is declared as:
  ```
  public int arriveAndDeregister()
  ```
  Return  
  The method arriveAndDeregister() returns the arrival phase number, or a negative value if terminated  
  Exception  
  The method arriveAndDeregister() throws the following exceptions:
    + IllegalStateException - if not terminated and the number of registered or unarrived parties would become
      negative


+ bulkRegister(int parties)  
  Introduction  
  Adds the given number of new unarrived parties to this phaser.  
  If an ongoing invocation of #onAdvance is in progress, this method may await its completion before returning.  
  If this phaser has a parent, and the given number of parties is greater than zero, and this phaser previously had no
  registered parties, this child phaser is also registered with its parent.  
  If this phaser is terminated, the attempt to register has no effect, and a negative value is returned.  
  Syntax  
  The method bulkRegister(int parties) from Phaser is declared as:
  ```
  public int bulkRegister(int parties)
  ```
  Parameter  
  The method bulkRegister(int parties) has the following parameter:
    + int parties - the number of additional parties required to advance to the next phase

  Return  
  The method bulkRegister(int parties) returns the arrival phase number to which this registration applied. If this
  value is negative, then this phaser has terminated, in which case registration has no effect.  
  Exception  
  The method bulkRegister(int parties) throws the following exceptions:
    + IllegalStateException - if attempting to register more than the maximum supported number of parties
    + IllegalArgumentException - if (parties < 0)


+ forceTermination()  
  Introduction  
  Forces this phaser to enter termination state.  
  Counts of registered parties are unaffected.  
  If this phaser is a member of a tiered set of phasers, then all of the phasers in the set are terminated.  
  If this phaser is already terminated, this method has no effect.  
  This method may be useful for coordinating recovery after one or more tasks encounter unexpected exceptions.  
  Syntax  
  The method forceTermination() from Phaser is declared as:
  ```
  public void forceTermination()
  ```

+ isTerminated()  
  Java Phaser isTerminated() Returns true if this phaser has been terminated.  
  Syntax  
  The method isTerminated() from Phaser is declared as:
  ```
  public boolean isTerminated()
  ```
  Return  
  The method isTerminated() returns true if this phaser has been terminated


+ register()  
  Introduction  
  Adds a new unarrived party to this phaser.  
  If an ongoing invocation of #onAdvance is in progress, this method may await its completion before returning.  
  If this phaser has a parent, and this phaser previously had no registered parties, this child phaser is also
  registered with its parent.  
  If this phaser is terminated, the attempt to register has no effect, and a negative value is returned.  
  Syntax  
  The method register() from Phaser is declared as:
  ```
  public int register()
  ```
  Return  
  The method register() returns the arrival phase number to which this registration applied. If this value is
  negative, then this phaser has terminated, in which case registration has no effect.  
  Exception  
  The method register() throws the following exceptions:
    + IllegalStateException - if attempting to register more than the maximum supported number of parties

## 二.Concurrent

### 2.1 Delayed

Introduction  
A mix-in style interface for marking objects that should be acted upon after a given delay.  
An implementation of this interface must define a compareTo method that provides an ordering consistent with its
getDelay method.  
Example  
The following code shows how to use Delayed from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-02 19:53
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws InterruptedException {
    BlockingQueue<DelayedJob> queue = new DelayQueue<>();
    Instant now = Instant.now();

    queue.put(new DelayedJob("A", now.plusSeconds(9)));
    queue.put(new DelayedJob("B", now.plusSeconds(3)));
    queue.put(new DelayedJob("C", now.plusSeconds(6)));
    queue.put(new DelayedJob("D", now.plusSeconds(1)));

    while (queue.size() > 0) {
      System.out.println("started...");
      DelayedJob job = queue.take();
      System.out.println("Job: " + job);
    }
    System.out.println("Finished.");
  }

  private static class DelayedJob implements Delayed {
    private Instant scheduledTime;
    private String jobName;

    public DelayedJob(String jobName, Instant scheduledTime) {
      this.scheduledTime = scheduledTime;
      this.jobName = jobName;
    }

    @Override
    public long getDelay(@NotNull TimeUnit unit) {
      long delay = MILLIS.between(Instant.now(), scheduledTime);
      return unit.convert(delay, MILLISECONDS);
    }

    @Override
    public int compareTo(@NotNull Delayed job) {
      long currentJobDelay = this.getDelay(MILLISECONDS);
      long jobDelay = job.getDelay(MILLISECONDS);

      int diff = 0;
      if (currentJobDelay > jobDelay) {
        diff = 1;
      } else if (currentJobDelay < jobDelay) {
        diff = -1;
      }
      return diff;
    }

    @Override
    public @NotNull String toString() {
      return this.jobName + ", " + "Scheduled Time:  " + this.scheduledTime;
    }
  }
}
```

+ getDelay(TimeUnit unit)  
  Java Delayed getDelay(TimeUnit unit) Returns the remaining delay associated with this object, in the given time
  unit.  
  Syntax  
  The method getDelay(TimeUnit unit) from Delayed is declared as:
  ```
  long getDelay(TimeUnit unit);
  ```
  Parameter  
  The method getDelay(TimeUnit unit) has the following parameter:
    + TimeUnit unit - the time unit

  Return  
  The method getDelay(TimeUnit unit) returns the remaining delay; zero or negative values indicate that the delay
  has already elapsed

### 2.2 ExecutionException

Introduction  
Exception thrown when attempting to retrieve the result of a task that aborted by throwing an exception.  
This exception can be inspected using the #getCause() method.  
Example  
The following code shows how to use ExecutionException from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-18 15:09
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    Callable<Object> badTask = () -> {
      throw new RuntimeException("Throwing exception from task execution...");
    };
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    Future<Object> future = executorService.submit(badTask);
    try {
      Object result = future.get();
    } catch (ExecutionException | InterruptedException e) {
      System.out.println(e.getMessage());
      System.out.println(e.getCause().getMessage());
      e.printStackTrace();
    }
    executorService.shutdown();
  }
}
```

+ getCause()  
  Introduction  
  Returns the cause of this throwable or null if the cause is nonexistent or unknown.  
  (The cause is the throwable that caused this throwable to get thrown.) This implementation returns the cause that was
  supplied via one of the constructors requiring a Throwable, or that was set after creation with the #initCause(
  Throwable) method.  
  While it is typically unnecessary to override this method, a subclass can override it to return a cause set by some
  other means.  
  This is appropriate for a "legacy chained throwable" that predates the addition of chained exceptions to Throwable
  .  
  Note that it is not necessary to override any of the PrintStackTrace methods, all of which invoke the getCause
  method to determine the cause of a throwable.  
  Syntax  
  The method getCause() from ExecutionException is declared as:
  ```
  public synchronized Throwable getCause()
  ```
  Return  
  The method getCause() returns the cause of this throwable or null if the cause is nonexistent or unknown.

+ getMessage()  
  Java ExecutionException getMessage() Returns the detail message string of this throwable.  
  Syntax  
  The method getMessage() from ExecutionException is declared as:
  ```
  public String getMessage()
  ```
  Return  
  The method getMessage() returns the detail message string of this Throwable instance (which may be null).
+ printStackTrace()  
  Introduction  
  Prints this throwable and its backtrace to the standard error stream.  
  This method prints a stack trace for this Throwable object on the error output stream that is the value of the
  field System.err.  
  The first line of output contains the result of the #toString() method for this object.  
  Syntax  
  The method printStackTrace() from ExecutionException is declared as:
  ```
  public void printStackTrace()
  ```

### 2.3 Callable

Introduction  
A task that returns a result and may throw an exception.  
Implementors define a single method with no arguments called call.   
The Callable interface is similar to java.lang.Runnable, in that both are designed for classes whose instances
are potentially executed by another thread.  
A Runnable, however, does not return a result and cannot throw a checked exception.  
The Executors class contains utility methods to convert from other common forms to Callable classes.

TheJavaconcurrent API Callable interface represents a thread that returns a value.  
An application can use Callable objects to compute results that are then returned to the invoking thread.  
This facilitates numerical computations in which partial results are computed simultaneously.  
It can also be used to run a thread that returns a status code that indicates the successful completion of the thread.  
Callable is a generic interface that is defined like this:

```
interface Callable<V>
```

Here, V indicates the type of data returned by the task.  
Callable defines only one method, call(), which is shown here:

```
V call() throws Exception
```

Inside call(), you define the task that you want performed.  
After that task completes, you return the result.  
If the result cannot be computed, call() must throw an exception.  
A Callable task is executed by an ExecutorService by calling its submit() method.  
There are three forms of submit(), but only one is used to execute a Callable.

```
<T> Future<T> submit(Callable<T> task)
```

Here, task is the Callable object that will be executed in its own thread.  
The result is returned through an object of type Future.  
Future is a generic interface that represents the value that will be returned by a Callable object.  
Future is defined like this:

```
interface Future<V>
```

Here, V specifies the type of the result.  
To obtain the returned value, you will call Future's get() method, which has these two forms:

```
V get() throws InterruptedException, ExecutionException
V get(long  wait, TimeUnit tu) throws InterruptedException, ExecutionException, TimeoutException
```

The first form waits for the result indefinitely.  
The second form allows you to specify a timeout period in wait.  
The units of wait are passed in tu, which is an object of the TimeUnit enumeration.

Example  
The following code shows how to use Callable from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.classes.Application
  @createTime: 2021/12/31-1:33
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    ExecutorService executorService = Executors.newFixedThreadPool(3);
    Future<Double> f1 = executorService.submit(new Avg());
    Future<Integer> f2 = executorService.submit(new Factorial());

    System.out.println(f1.get());
    System.out.println(f2.get());

    executorService.shutdown();
  }

  private static class Avg implements Callable<Double> {
    @Contract(pure = true)
    @Override
    public @NotNull Double call() {
      return 1.0;
    }
  }

  private static class Factorial implements Callable<Integer> {
    @Contract(pure = true)
    @Override
    public @NotNull Integer call() {
      return 1;
    }
  }
}
```

+ call()  
  Java Callable call() Computes a result, or throws an exception if unable to do so.  
  Syntax  
  The method call() from Callable is declared as:
  ```
  V call() throws Exception;
  ```
  Return  
  The method call() returns computed result  
  Exception  
  The method call() throws the following exceptions:
    + Exception - if unable to compute a result

### 2.4 Future

Introduction  
A Future represents the result of an asynchronous computation.  
Methods are provided to check if the computation is complete, to wait for its completion, and to retrieve the result of
the computation.  
The result can only be retrieved using method get when the computation has completed, blocking if necessary until it
is ready.  
Cancellation is performed by the cancel method.  
Additional methods are provided to determine if the task completed normally or was cancelled.  
Once a computation has completed, the computation cannot be cancelled.   
If you would like to use a Future for the sake of cancellability but not provide a usable result, you can declare
types of the form Future<?> and return null as a result of the underlying task.

Example  
The following code shows how to use Future from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.Contract;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Future;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-19 12:55
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws Exception {
    Path file = Paths.get("D:java-codeidea-workspacejavastudy-tuyucheng-edu项目.txt");
    AsynchronousFileChannel channel = AsynchronousFileChannel.open(file);
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    Future<Integer> result = channel.read(buffer, 0);
    while (!result.isDone()) {
      ProfitCalculator.calculateTax();
    }
    Integer bytesRead = result.get();
    System.out.println("Bytes read [" + bytesRead + "]");
  }

  private static class ProfitCalculator {
    @Contract(pure = true)
    public static void calculateTax() {

    }
  }
}
```

+ get()  
  Java Future get() Waits if necessary for the computation to complete, and then retrieves its result.  
  Syntax  
  The method get() from Future is declared as:
  ```
  V get() throws InterruptedException, ExecutionException;
  ```
  Return  
  The method get() returns the computed result  
  Exception  
  The method get() throws the following exceptions:
    + CancellationException - if the computation was cancelled
    + ExecutionException - if the computation threw an exception
    + InterruptedException - if the current thread was interrupted while waiting
+ isDone()  
  Introduction  
  Returns true if this task completed.  
  Completion may be due to normal termination, an exception, or cancellation -- in all of these cases, this method will
  return true.  
  Syntax  
  The method isDone() from Future is declared as:
  ```
  boolean isDone();
  ```
  Return  
  The method isDone() returns true if this task completed

### 2.5 FutureTask

Introduction  
A cancellable asynchronous computation.  
This class provides a base implementation of Future, with methods to start and cancel a computation, query to see if
the computation is complete, and retrieve the result of the computation.  
The result can only be retrieved when the computation has completed; the get methods will block if the computation
has not yet completed.  
Once the computation has completed, the computation cannot be restarted or cancelled (unless the computation is invoked
using #runAndReset).  
A FutureTask can be used to wrap a Callable or Runnable object.  
Because FutureTask implements Runnable, a FutureTask can be submitted to an Executor for execution.  
In addition to serving as a standalone class, this class provides protected functionality that may be useful when
creating customized task classes.

Example  
The following code shows how to use FutureTask from java.util.concurrent.

```java
package cn.tuyucheng.edu.classes;

import org.jetbrains.annotations.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-01 22:36
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    int nTasks = 5;
    int fib = 4;
    SingleThreadAccess sta = new SingleThreadAccess();
    for (int i = 0; i < nTasks; i++)
      sta.invokeLater(new Task(fib, "Task " + i));
    sta.shutdown();
  }

  private static class SingleThreadAccess {
    private ThreadPoolExecutor tpe;

    public SingleThreadAccess() {
      tpe = new ThreadPoolExecutor(1, 1,
          50000L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
    }

    public void invokeLater(Runnable r) {
      tpe.execute(r);
    }

    public void invokeAneWait(Runnable r) throws InterruptedException, ExecutionException {
      FutureTask task = new FutureTask(r, null);
      tpe.execute(task);
      task.get();
    }

    public void shutdown() {
      tpe.shutdown();
    }
  }

  private static class Task implements Runnable {
    long n;

    String id;

    private long fib(long n) {
      if (n == 0)
        return 0L;
      if (n == 1)
        return 1L;
      return fib(n - 1) + fib(n - 2);
    }

    @Contract(pure = true)
    public Task(long n, String id) {
      this.n = n;
      this.id = id;
    }

    public void run() {
      Date d = new Date();
      DateFormat df = new SimpleDateFormat("HH:mm:ss:SSS");
      long startTime = System.currentTimeMillis();
      d.setTime(startTime);
      System.out.println("Starting task " + id + " at " + df.format(d));
      fib(n);
      long endTime = System.currentTimeMillis();
      d.setTime(endTime);
      System.out.println("Ending task " + id + " at " + df.format(d)
          + " after " + (endTime - startTime) + " milliseconds");
    }
  }
}
```

+ FutureTask(Runnable runnable, V result)  
  Java FutureTask FutureTask(Runnable runnable, V result) Creates a FutureTask that will, upon running, execute
  the given Runnable, and arrange that get will return the given result on successful completion.  
  The method FutureTask(Runnable runnable, V result) is a constructor.  
  Syntax  
  The method FutureTask(Runnable runnable, V result) from FutureTask is declared as:
  ```
  public FutureTask(Runnable runnable, V result)
  ```
  Parameter  
  The method FutureTask(Runnable runnable, V result) has the following parameter:
    + Runnable runnable - the runnable task
    + V result - the result to return on successful completion. If you don't need a particular result, consider
      using constructions of the form: (Future<?> f = new FutureTask<Void>(runnable, null))

  Exception  
  The method FutureTask(Runnable runnable, V result) throws the following exceptions:
    + NullPointerException - if the runnable is null
+ get()  
  Java FutureTask get() @throws CancellationException  
  Syntax  
  The method get() from FutureTask is declared as:
  ```
  public V get() throws InterruptedException, ExecutionException
  ```
  Return  
  The method get() returns  
  Exception  
  The method get() throws the following exceptions:
    + CancellationException

### 2.6 TimeUnit

The concurrent API defines several methods that take an argument of type TimeUnit, which indicates a time-out
period.  
TimeUnit is an enumeration that is used to specify the unit of the timing.  
TimeUnit is defined within java.util.concurrent.  
It can be one of the following values:
> DAYS  
> HOURS  
> MINUTES  
> SECONDS  
> MICROSECONDS  
> MILLISECONDS  
> NANOSECONDS

Here is an example that uses TimeUnit. The following code uses the second form of get() that takes a 
TimeUnit argument.  
It illustrates Callable and Future by creating two tasks that perform three different computations.

+ The first returns the summation of a value,
+ The second computes the length of the hypotenuse of a right triangle

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-19 14:52
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    System.out.println("Starting");
    Future<Integer> f1 = executorService.submit(new Sum(5));
    Future<Double> f2 = executorService.submit(new Hypot(3, 4));
    try {
      System.out.println(f1.get(10, TimeUnit.MILLISECONDS));
      System.out.println(f2.get(10, TimeUnit.MILLISECONDS));
    } catch (ExecutionException | InterruptedException | TimeoutException e) {
      e.printStackTrace();
    }
    executorService.shutdown();
    System.out.println("Done!!!");
  }

  private static class Sum implements Callable<Integer> {
    private int stop;

    public Sum(int stop) {
      this.stop = stop;
    }

    @Override
    public Integer call() throws Exception {
      int sum = 0;
      for (int x = 1; x <= stop; x++) {
        sum += x;
      }
      return sum;
    }
  }

  private static class Hypot implements Callable<Double> {
    private double side1, side2;

    public Hypot(double side1, double side2) {
      this.side1 = side1;
      this.side2 = side2;
    }

    @Override
    public Double call() throws Exception {
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      return Math.sqrt((side1  side1) + (side2  side2));
    }
  }
}
```

The TimeUnit enumeration defines various methods that convert between units.  
These are shown here:

```
long convert(long tval, TimeUnit tu)
long toMicros(long tval)
long toMillis(long tval)
long toNanos(long tval)
long toSeconds(long tval)
long toDays(long tval)
long toHours(long tval)
long toMinutes(long tval)
```

The convert() method converts tval into the specified unit and returns the result.  
The to methods perform the indicated conversion and return the result. TimeUnit also defines the following
timing methods:

```
void sleep(long  delay) throws InterruptedExecution
void timedJoin(Thread  thrd, long delay) throws InterruptedExecution
void timedWait(Object  obj, long delay) throws InterruptedExecution
```

sleep() pauses execution for the specified delay period, which is specified in terms of the invoking enumeration
constant.  
It translates into a call to Thread.sleep().  
The timedJoin() method is a specialized version of Thread.join() in which thrd pauses for the time period
specified by delay.  
The timedWait() method is a specialized version of Object.wait() in which obj is waited on for the period of
time specified by delay.

### 2.7 ThreadFactory

Introduction  
An object that creates new threads on demand.  
Using thread factories removes hardwiring of calls to (Thread#Thread(Runnable) new Thread), enabling applications to
use special thread subclasses, priorities, etc.  
The simplest implementation of this interface is just:

```
class SimpleThreadFactory implements ThreadFactory { 
  public Thread newThread(Runnable r) { 
     return new Thread(r);
  }
}
```

The Executors#defaultThreadFactory method provides a more useful simple implementation, that sets the created thread
context to known values before returning it.

Example  
The following code shows how to use ThreadFactory from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadFactory;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.CustomizeThreadFactory
  @createTime: 2022-01-20 13:20
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class CustomizeThreadFactory implements ThreadFactory {
  /
    Create a new thread for the thread pool.  The create thread will be a daemon thread.
   
    @param r The runnable used by the thread pool.
    @return The newly created thread.
   /
  @Override
  public Thread newThread(@NotNull Runnable r) {
    Thread thread = new Thread(r);
    if (!thread.isDaemon())
      thread.setDaemon(true);
    return thread;
  }
}
```

### 2.8 ThreadLocalRandom

Introduction  
A random number generator isolated to the current thread.  
Like the global java.util.Random generator used by the java.lang.Math class, a ThreadLocalRandom is
initialized with an internally generated seed that may not otherwise be modified.  
When applicable, use of ThreadLocalRandom rather than shared Random objects in concurrent programs will
typically encounter much less overhead and contention.  
Use of ThreadLocalRandom is particularly appropriate when multiple tasks (for example, each a ForkJoinTask) use
random numbers in parallel in thread pools.  
Usages of this class should typically be of the form: ThreadLocalRandom.current().nextX(...) (where X is Int, 
Long, etc).  
When all usages are of this form, it is never possible to accidentally share a ThreadLocalRandom across multiple
threads.  
This class also provides additional commonly used bounded random generation methods.  
Instances of ThreadLocalRandom are not cryptographically secure.  
Consider instead using java.security.SecureRandom in security-sensitive applications.  
Additionally, default-constructed instances do not use a cryptographically random seed unless the System#getProperty
system property java.util.secureRandomSeed is set to true.

Example  
The following code shows how to use ThreadLocalRandom from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.ThreadLocalRandom;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-20 13:31
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    // Returns the LocalThreadRandom of Current thread
    ThreadLocalRandom localRandom = ThreadLocalRandom.current();
    System.out.println("Current thread's LocalThreadRandom name is: " + localRandom);
    int randomNumber = localRandom.nextInt(10);
    System.out.println("randomNumber = " + randomNumber);

    // Returns a stream of pseudorandom double values
    System.out.println("stream of pseudorandom double value is: " + localRandom.doubles());
  }
}
```

+ current()  
  Java ThreadLocalRandom current() Returns the current thread's ThreadLocalRandom.  
  Syntax  
  The method current() from ThreadLocalRandom is declared as:
  ```
  public static ThreadLocalRandom current()
  ```
  Return  
  The method current() returns the current thread's ThreadLocalRandom


+ nextInt()  
  Java ThreadLocalRandom nextInt() Returns a pseudorandom int value.  
  Syntax  
  The method nextInt() from ThreadLocalRandom is declared as:
  ```
  public int nextInt()
  ```
  Return  
  The method nextInt() returns a pseudorandom int value


+ setSeed(long seed)  
  Introduction  
  Throws UnsupportedOperationException.  
  Setting seeds in this generator is not supported.  
  Syntax  
  The method setSeed(long seed) from ThreadLocalRandom is declared as:
  ```
  public void setSeed(long seed)
  ```
  Parameter  
  The method setSeed(long seed) has the following parameter:
    + long seed

  Exception  
  The method setSeed(long seed) throws the following exceptions:
    + UnsupportedOperationException - always

## 三.ThreadPool

### 3.1 Executor

TheJavaconcurrent API executor initiates and controls the execution of threads.  
An executor offers an alternative to managing threads through the Thread class.  
The Executor interface defines the following method:

```
void execute(Runnable  thread)
```

The thread specified by thread is executed by execute() method.  
The ExecutorService interface extends Executor by adding methods that help manage and control the execution of
threads.  
For example, ExecutorService defines shutdown(), shown here, which stops the invoking ExecutorService.

```
void shutdown()
```

ExecutorService defines methods

+ that execute threads that return results,
+ that execute a set of threads, and
+ that determine the shutdown status.

The interface ScheduledExecutorService which extends ExecutorService supports the scheduling of threads.  
The concurrent API defines three predefined executor classes: ThreadPoolExecutor and ScheduledThreadPoolExecutor
, and ForkJoinPool.  
ThreadPoolExecutor implements the Executor and ExecutorService interfaces and provides support for a managed
pool of threads.  
ScheduledThreadPoolExecutor also implements the ScheduledExecutorService interface to allow a pool of threads to
be scheduled.  
ForkJoinPool implements the Executor and ExecutorService interfaces and is used by the Fork/Join
Framework.  
A thread pool provides a set of threads that is used to execute various tasks.  
Instead of each task using its own thread, the threads in the pool are used.  
This reduces the overhead associated with creating many separate threads.  
We can get an executor by calling one of the following static factory methods defined by the Executors utility
class.  
Here are some examples:

```
static ExecutorService newCachedThreadPool()
static ExecutorService newFixedThreadPool(int numThreads)
static ScheduledExecutorService newScheduledThreadPool(int numThreads)
```

newCachedThreadPool() creates a thread pool that adds threads as needed but reuses threads if possible.  
newFixedThreadPool() creates a thread pool that consists of a specified number of threads.  
newScheduledThreadPool() creates a thread pool that supports thread scheduling.  
Each returns a reference to an ExecutorService that can be used to manage the pool.

Executor Example  
The following program creates a fixed thread pool that contains two threads.  
It then uses that pool to execute four tasks.  
Thus, four tasks share the two threads that are in the pool.  
After the tasks finish, the pool is shut down and the program ends.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-19 14:34
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    CountDownLatch countDownLatch1 = new CountDownLatch(5);
    CountDownLatch countDownLatch2 = new CountDownLatch(5);
    CountDownLatch countDownLatch3 = new CountDownLatch(5);
    CountDownLatch countDownLatch4 = new CountDownLatch(5);
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    System.out.println("Starting");
    executorService.execute(new SubThread("A", countDownLatch1));
    executorService.execute(new SubThread("B", countDownLatch2));
    executorService.execute(new SubThread("C", countDownLatch3));
    executorService.execute(new SubThread("D", countDownLatch4));
    try {
      countDownLatch1.await();
      countDownLatch2.await();
      countDownLatch3.await();
      countDownLatch4.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    executorService.shutdown();
    System.out.println("Done!!!");
  }

  private static class SubThread implements Runnable {
    private String name;
    private CountDownLatch countDownLatch;

    public SubThread(String name, CountDownLatch countDownLatch) {
      this.name = name;
      this.countDownLatch = countDownLatch;
      new Thread(this);
    }

    @Override
    public void run() {
      for (int i = 1; i <= 5; i++) {
        System.out.println(name + ": " + i);
        this.countDownLatch.countDown();
      }
    }
  }
}
```

As the output shows, even though the thread pool contains only two threads, all four tasks are still executed.  
Only two can run at the same time.  
The others must wait until one of the pooled threads is available for use.  
The call to shutdown() is important. If it were not present in the program, then the program would not terminate
because the executor would remain active.

### 3.2 Executors

Introduction  
Factory and utility methods for Executor, ExecutorService, ScheduledExecutorService, ThreadFactory,
and Callable classes defined in this package.  
This class supports the following kinds of methods:

+ Methods that create and return an ExecutorService set up with commonly useful configuration settings.
+ Methods that create and return a ScheduledExecutorService set up with commonly useful configuration settings.
+ Methods that create and return a "wrapped" ExecutorService, that disables reconfiguration by making
  implementation-specific methods inaccessible.
+ Methods that create and return a ThreadFactory that sets newly created threads to a known state.
+ Methods that create and return a Callable out of other closure-like forms, so they can be used in execution methods
  requiring Callable.

Example  
The following code shows how to use Executors from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.TimeUnit.SECONDS;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-18 17:24
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application implements Runnable {
  public static final AtomicLong idSequence = new AtomicLong(0);

  public static void main(String[] args) {
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    for (int i = 0; i < 200; i++) {
      executorService.scheduleAtFixedRate(new Application(), 0, 1, SECONDS);
    }
    try {
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    executorService.shutdown();
  }

  @Override
  public void run() {
    System.out.println("produces " + idSequence.incrementAndGet());
  }
}
```

+ newCachedThreadPool()  
  Introduction  
  Creates a thread pool that creates new threads as needed, but will reuse previously constructed threads when they are
  available.  
  These pools will typically improve the performance of programs that execute many short-lived asynchronous tasks.  
  Calls to execute will reuse previously constructed threads if available.  
  If no existing thread is available, a new thread will be created and added to the pool.  
  Threads that have not been used for sixty seconds are terminated and removed from the cache.  
  Thus, a pool that remains idle for long enough will not consume any resources.  
  Note that pools with similar properties but different details (for example, timeout parameters) may be created
  using ThreadPoolExecutor constructors.  
  Syntax  
  The method newCachedThreadPool() from Executors is declared as:
  ```
  public static ExecutorService newCachedThreadPool()
  ```
  Return  
  The method newCachedThreadPool() returns the newly created thread pool


+ newCachedThreadPool(ThreadFactory threadFactory)  
  Java Executors newCachedThreadPool(ThreadFactory threadFactory) Creates a thread pool that creates new threads as
  needed, but will reuse previously constructed threads when they are available, and uses the provided ThreadFactory to
  create new threads when needed.  
  Syntax  
  The method newCachedThreadPool(ThreadFactory threadFactory) from Executors is declared as:
  ```
  public static ExecutorService newCachedThreadPool(ThreadFactory threadFactory)
  ```
  Parameter  
  The method newCachedThreadPool(ThreadFactory threadFactory) has the following parameter:
    + ThreadFactory threadFactory - the factory to use when creating new threads

  Return  
  The method newCachedThreadPool(ThreadFactory threadFactory) returns the newly created thread pool  
  Exception  
  The method newCachedThreadPool(ThreadFactory threadFactory) throws the following exceptions:
    + NullPointerException - if threadFactory is null


+ newFixedThreadPool(int nThreads)  
  Introduction  
  Creates a thread pool that reuses a fixed number of threads operating off a shared unbounded queue.  
  At any point, at most nThreads threads will be active processing tasks.  
  If additional tasks are submitted when all threads are active, they will wait in the queue until a thread is
  available.  
  If any thread terminates due to a failure during execution prior to shutdown, a new one will take its place if needed
  to execute subsequent tasks.  
  The threads in the pool will exist until it is explicitly (ExecutorService#shutdown shutdown).  
  Syntax  
  The method newFixedThreadPool() from Executors is declared as:
  ```
  public static ExecutorService newFixedThreadPool(int nThreads)
  ```
  Parameter  
  The method newFixedThreadPool() has the following parameter:
    + int nThreads - the number of threads in the pool

  Return  
  The method newFixedThreadPool() returns the newly created thread pool  
  Exception  
  The method newFixedThreadPool() throws the following exceptions:
    + IllegalArgumentException - if (nThreads <= 0)

+ newScheduledThreadPool(int corePoolSize)  
  Java Executors newScheduledThreadPool(int corePoolSize) Creates a thread pool that can schedule commands to run
  after a given delay, or to execute periodically.  
  Syntax  
  The method newScheduledThreadPool(int corePoolSize) from Executors is declared as:
  ```
  public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize)
  ```
  Parameter  
  The method newScheduledThreadPool(int corePoolSize) has the following parameter:
    + int corePoolSize - the number of threads to keep in the pool, even if they are idle

  Return  
  The method newScheduledThreadPool(int corePoolSize) returns the newly created scheduled thread pool  
  Exception  
  The method newScheduledThreadPool(int corePoolSize) throws the following exceptions:
    + IllegalArgumentException - if (corePoolSize < 0)


+ newSingleThreadExecutor()  
  Introduction  
  Creates an Executor that uses a single worker thread operating off an unbounded queue.  
  (Note however that if this single thread terminates due to a failure during execution prior to shutdown, a new one
  will take its place if needed to execute subsequent tasks.)  
  Tasks are guaranteed to execute sequentially, and no more than one task will be active at any given time.  
  Unlike the otherwise equivalent newFixedThreadPool(1) the returned executor is guaranteed not to be reconfigurable
  to use additional threads.  
  Syntax  
  The method newSingleThreadExecutor() from Executors is declared as:
  ```
  public static ExecutorService newSingleThreadExecutor()
  ```
  Return  
  The method newSingleThreadExecutor() returns the newly created single-threaded Executor


+ newSingleThreadScheduledExecutor()  
  Introduction  
  Creates a single-threaded executor that can schedule commands to run after a given delay, or to execute
  periodically.  
  (Note however that if this single thread terminates due to a failure during execution prior to shutdown, a new one
  will take its place if needed to execute subsequent tasks.) Tasks are guaranteed to execute sequentially, and no more
  than one task will be active at any given time.  
  Unlike the otherwise equivalent newScheduledThreadPool(1) the returned executor is guaranteed not to be
  reconfigurable to use additional threads.  
  Syntax  
  The method newSingleThreadScheduledExecutor() from Executors is declared as:
  ```
  public static ScheduledExecutorService newSingleThreadScheduledExecutor()
  ```
  Return  
  The method newSingleThreadScheduledExecutor() returns the newly created scheduled executor

### 3.3 ExecutorService

Introduction  
An Executor that provides methods to manage termination and methods that can produce a Future for tracking
progress of one or more asynchronous tasks.  
An ExecutorService can be shut down, which will cause it to reject new tasks.  
Two different methods are provided for shutting down an ExecutorService.  
The #shutdown method will allow previously submitted tasks to execute before terminating, while the #shutdownNow
method prevents waiting tasks from starting and attempts to stop currently executing tasks.  
Upon termination, an executor has no tasks actively executing, no tasks awaiting execution, and no new tasks can be
submitted.  
An unused ExecutorService should be shut down to allow reclamation of its resources.  
Method submit extends base method Executor#execute(Runnable) by creating and returning a Future that can be
used to cancel execution and/or wait for completion.  
Methods invokeAny and invokeAll perform the most commonly useful forms of bulk execution, executing a collection
of tasks and then waiting for at least one, or all, to complete.  
Memory consistency effects: Actions in a thread prior to the submission of a Runnable or Callable task to
an ExecutorService happen-before any actions taken by that task, which in turn happen-before the result is retrieved
via Future.get().

Example  
The following code shows how to use ExecutorService from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-18 19:44
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  private static int sum;

  public static void main(String[] args) {
    ExecutorService executorService = Executors.newCachedThreadPool();
    Adding adding = new Adding();
    for (int i = 0; i < 1000; i++) {
      executorService.execute(adding);
    }
    executorService.shutdown();
    while (!executorService.isTerminated()) {
      System.out.println("Is it done? : " + executorService.isTerminated());
    }
    System.out.println("Is it done? : " + executorService.isTerminated());
    System.out.println("Sum is " + sum);
  }

  private static class Adding implements Runnable {
    @Override
    public void run() {
      synchronized (Adding.class) {
        sum += 1;
      }
    }
  }
}
```

+ awaitTermination(long timeout, TimeUnit unit)  
  Java ExecutorService awaitTermination(long timeout, TimeUnit unit) Blocks until all tasks have completed execution
  after a shutdown request, or the timeout occurs, or the current thread is interrupted, whichever happens first.  
  Syntax  
  The method awaitTermination(long timeout, TimeUnit unit) from ExecutorService is declared as:
  ```
  boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
  ```
  Parameter  
  The method awaitTermination(long timeout, TimeUnit unit) has the following parameter:
    + long timeout - the maximum time to wait
    + TimeUnit unit - the time unit of the timeout argument

  Return  
  The method awaitTermination(long timeout, TimeUnit unit) returns true if this executor terminated and 
  false if the timeout elapsed before termination  
  Exception  
  The method awaitTermination(long timeout, TimeUnit unit) throws the following exceptions:
    + InterruptedException - if interrupted while waiting


+ execute(Runnable command)  
  Introduction  
  Executes the given command at some time in the future.  
  The command may execute in a new thread, in a pooled thread, or in the calling thread, at the discretion of the 
  Executor implementation.  
  Syntax  
  The method execute(Runnable command) from ExecutorService is declared as:
  ```
  void execute(Runnable command);
  ```
  Parameter  
  The method execute(Runnable command) has the following parameter:
    + Runnable command - the runnable task

  Exception  
  The method execute(Runnable command) throws the following exceptions:
    + RejectedExecutionException - if this task cannot be accepted for execution
    + NullPointerException - if command is null


+ invokeAll(Collection<? extends Callable<T>> tasks)  
  Introduction  
  Executes the given tasks, returning a list of Futures holding their status and results when all complete.  
  Future#isDone is true for each element of the returned list.  
  Note that a completed task could have terminated either normally or by throwing an exception.  
  The results of this method are undefined if the given collection is modified while this operation is in progress.  
  Syntax  
  The method invokeAll(Collection<? extends Callable<T>> tasks) from ExecutorService is declared as:
  ```
  <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException;
  ```  
  Parameter  
  The method invokeAll(Collection<? extends Callable<T>> tasks) has the following parameter:
    + Collection tasks - the collection of tasks

  Return  
  The method invokeAll(Collection<? extends Callable<T>> tasks) returns a list of Futures representing the tasks, in
  the same sequential order as produced by the iterator for the given task list, each of which has completed  
  Exception  
  The method invokeAll(Collection<? extends Callable<T>> tasks) throws the following exceptions:
    + InterruptedException - if interrupted while waiting, in which case unfinished tasks are cancelled
    + NullPointerException - if tasks or any of its elements are null
    + RejectedExecutionException - if any task cannot be scheduled for execution


+ isTerminated()  
  Introduction  
  Returns true if all tasks have completed following shut down.  
  Note that isTerminated is never true unless either shutdown or shutdownNow was called first.  
  Syntax  
  The method isTerminated() from ExecutorService is declared as:
  ```
  boolean isTerminated();
  ```
  Return  
  The method isTerminated() returns true if all tasks have completed following shut down


+ shutdown()  
  Introduction  
  Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.  
  Invocation has no additional effect if already shut down.  
  This method does not wait for previously submitted tasks to complete execution.  
  Use (#awaitTermination awaitTermination) to do that.  
  Syntax  
  The method shutdown() from ExecutorService is declared as:
  ```
  void shutdown();
  ```
  Exception  
  The method shutdown() throws the following exceptions:
    + SecurityException - if a security manager exists and shutting down this ExecutorService may manipulate threads
      that the caller is not permitted to modify because it does not hold java.lang.RuntimePermission("
      modifyThread"), or the security manager's checkAccess method denies access.


+ submit(Callable<T> task)  
  Introduction  
  Submits a value-returning task for execution and returns a Future representing the pending results of the task.  
  The Future's get method will return the task's result upon successful completion.  
  If you would like to immediately block waiting for a task, you can use constructions of the form (result = exec.
  submit(aCallable).get();)  
  Note: The Executors class includes a set of methods that can convert some other common closure-like objects, for
  example, java.security.PrivilegedAction to Callable form so they can be submitted.  
  Syntax  
  The method submit(Callable<T> task) from ExecutorService is declared as:
  ````
  <T> Future<T> submit(Callable<T> task);
  ````
  Parameter  
  The method submit(Callable<T> task) has the following parameter:
    + Callable task - the task to submit

  Return  
  The method submit() returns a Future representing pending completion of the task  
  Exception  
  The method submit(Callable<T> task) throws the following exceptions:
    + RejectedExecutionException - if the task cannot be scheduled for execution
    + NullPointerException - if the task is null


+ submit(Runnable task)  
  Introduction  
  Submits a Runnable task for execution and returns a Future representing that task.  
  The Future's get method will return null upon successful completion.  
  Syntax  
  The method submit(Runnable task) from ExecutorService is declared as:
  ````
  Future<?> submit(Runnable task);
  ````
  Parameter  
  The method submit(Runnable task) has the following parameter:
    + Runnable task - the task to submit

  Return  
  The method submit(Runnable task) returns a Future representing pending completion of the task  
  Exception  
  The method submit(Runnable task) throws the following exceptions:
    + RejectedExecutionException - if the task cannot be scheduled for execution
    + NullPointerException - if the task is null

### 3.4 ExecutorCompletionService

Introduction  
A CompletionService that uses a supplied Executor to execute tasks.  
This class arranges that submitted tasks are, upon completion, placed on a queue accessible using take.  
The class is lightweight enough to be suitable for transient use when processing groups of tasks.

Example  
The following code shows how to use ExecutorCompletionService from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-01 19:34
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws Exception {
    int THREAD_COUNT = 4;
    ExecutorService execService = Executors.newFixedThreadPool(THREAD_COUNT);
    CompletionService<Integer> completionService = new ExecutorCompletionService<>(execService);
    for (int i = 0; i < THREAD_COUNT; i++) {
      completionService.submit(new WordLengthCallable());
    }
    execService.shutdown();
    while (!execService.isTerminated()) {
      int result = completionService.take().get();
      System.out.println("Result is: " + result);
    }
    Thread.sleep(1000);
    System.out.println("done!");
  }

  private static class WordLengthCallable implements Callable<Integer> {
    private Random random = new Random();

    public @NotNull Integer call() throws InterruptedException {
      int sleepTime = (2 + random.nextInt(16))  500;
      Thread.sleep(sleepTime);
      return sleepTime;
    }
  }
}
```

+ ExecutorCompletionService(Executor executor)  
  Java ExecutorCompletionService ExecutorCompletionService(Executor executor) Creates an ExecutorCompletionService
  using the supplied executor for base task execution and a LinkedBlockingQueue as a completion queue.  
  The method ExecutorCompletionService(Executor executor) is a constructor.  
  Syntax  
  The method ExecutorCompletionService(Executor executor) from ExecutorCompletionService is declared as:
  ```
  public ExecutorCompletionService(Executor executor)
  ```
  Parameter  
  The method ExecutorCompletionService(Executor executor) has the following parameter:
    + Executor executor - the executor to use

  Exception  
  The method ExecutorCompletionService(Executor executor) throws the following exceptions:
    + NullPointerException - if executor is null


+ submit(Callable<V> task)  
  Java ExecutorCompletionService submit(Callable<V> task) @throws RejectedExecutionException @throws
  NullPointerException  
  Syntax  
  The method submit(Callable<V> task) from ExecutorCompletionService is declared as:
  ```
  public Future<V> submit(Callable<V> task)
  ```
  Parameter  
  The method submit(Callable<V> task) has the following parameter:
    + Callable task -

  Return  
  The method submit(Callable<V> task) returns  
  Exception  
  The method submit(Callable<V> task) throws the following exceptions:
    + RejectedExecutionException -
    + NullPointerException -


+ take()  
  Java ExecutorCompletionService take() null  
  Syntax  
  The method take() from ExecutorCompletionService is declared as:
  ```
  public Future<V> take() throws InterruptedException
  ```

### 3.5 CompletionService

Introduction  
A service that decouples the production of new asynchronous tasks from the consumption of the results of completed
tasks.  
Producers submit tasks for execution.  
Consumers take completed tasks and process their results in the order they complete.  
A CompletionService can for example be used to manage asynchronous I/O, in which tasks that perform reads are
submitted in one part of a program or system, and then acted upon in a different part of the program when the reads
complete, possibly in a different order than they were requested.  
Typically, a CompletionService relies on a separate Executor to actually execute the tasks, in which case the 
CompletionService only manages an internal completion queue.  
The ExecutorCompletionService class provides an implementation of this approach.  
Memory consistency effects: Actions in a thread prior to submitting a task to a CompletionService happen-before
actions taken by that task, which in turn happen-before actions following a successful return from the corresponding 
take().

Example  
The following code shows how to use CompletionService from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-01 19:34
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    List<String> searchList = new ArrayList<>(7);
    searchList.add("hello");
    searchList.add("world");
    searchList.add("CSS");
    searchList.add("debian");
    searchList.add("linux");
    searchList.add("HTML");
    searchList.add("stack");

    Set<String> targetSet = new HashSet<>(searchList);
    Set<String> matchSet = findMatches(searchList, targetSet);
    for (String match : matchSet) {
      System.out.println("match:  " + match);
    }
  }

  public static Set<String> findMatches(List<String> searchList, Set<String> targetSet)
      throws InterruptedException, ExecutionException {
    Set<String> locatedMatchSet = new HashSet<>();

    int threadCount = Runtime.getRuntime().availableProcessors();
    List<List<String>> partitionList = getChunkList(searchList, threadCount);

    if (partitionList.size() == 1) {
      // if we only have one "chunk" then don't bother with a thread-pool
      locatedMatchSet = new ListSearcher(searchList, targetSet).call();
    } else {
      ExecutorService executor = Executors.newFixedThreadPool(threadCount);
      CompletionService<Set<String>> completionService = new ExecutorCompletionService<>(executor);
      for (List<String> chunkList : partitionList)
        completionService.submit(new ListSearcher(chunkList, targetSet));
      for (int x = 0; x < partitionList.size(); x++) {
        Set<String> threadMatchSet = completionService.take().get();
        locatedMatchSet.addAll(threadMatchSet);
      }
      executor.shutdown();
    }
    return locatedMatchSet;
  }

  private static <T> @NotNull List<List<T>> getChunkList(@NotNull List<T> unpartitionedList, int splitCount) {
    int totalProblemSize = unpartitionedList.size();
    int chunkSize = (int) Math.ceil((double) totalProblemSize / splitCount);

    List<List<T>> chunkList = new ArrayList<>(splitCount);

    int offset = 0;
    int limit;
    for (int x = 0; x < splitCount; x++) {
      limit = offset + chunkSize;
      if (limit > totalProblemSize)
        limit = totalProblemSize;
      List<T> subList = unpartitionedList.subList(offset, limit);
      chunkList.add(subList);
      offset = limit;
    }
    return chunkList;
  }

  private static class ListSearcher implements Callable<Set<String>> {

    private final List<String> searchList;
    private final Set<String> targetSet;
    private final Set<String> matchSet = new HashSet<>();

    public ListSearcher(List<String> searchList, Set<String> targetSet) {
      this.searchList = searchList;
      this.targetSet = targetSet;
    }

    @Override
    public Set<String> call() {
      for (String searchValue : searchList) {
        if (targetSet.contains(searchValue))
          matchSet.add(searchValue);
      }
      return matchSet;
    }
  }
}
```

+ submit(Callable<V> task)  
  Introduction  
  Submits a value-returning task for execution and returns a Future representing the pending results of the task.  
  Upon completion, this task may be taken or polled.  
  Syntax  
  The method submit(Callable<V> task) from CompletionService is declared as:
  ```
  Future<V> submit(Callable<V> task);
  ```
  Parameter  
  The method  submit(Callable<V> task) has the following parameter:
    + Callable task - the task to submit

  Return  
  The method  submit(Callable<V> task) returns a Future representing pending completion of the task  
  Exception  
  The method  submit(Callable<V> task) throws the following exceptions:
    + RejectedExecutionException - if the task cannot be scheduled for execution
    + NullPointerException - if the task is null


+ take()  
  Java CompletionService take() Retrieves and removes the Future representing the next completed task, waiting if
  none are yet present.  
  Syntax  
  The method take() from CompletionService is declared as:
  ```
  Future<V> take() throws InterruptedException;
  ```
  Return  
  The method take() returns the Future representing the next completed task  
  Exception  
  The method take() throws the following exceptions:
    + InterruptedException - if interrupted while waiting

### 3.6 CompletableFuture

Utilize CompletableFuture objects to represent the state of each task that is currently being performed.  
Each CompletableFuture object will run on a designated or application-determined background thread, issuing a callback
to the original calling method once completed.  
In the following solution, two long-running tasks are invoked by a calling method, and they each utilize the
CompletableFuture to report status once the task has been completed.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-01 19:17
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application { // Executing Multiple Tasks Asynchronously
  public static void main(String[] args) {
    try {
      CompletableFuture<String> tasks = performWork().thenApply(work -> {
        String newTask = work + " Second task complete!";
        System.out.println(newTask);
        return newTask;
      }).thenApply(finalTask -> finalTask + " Final Task Complete!");

      CompletableFuture<String> future = performSecondWork("Java 9 is Great! ");
      while (!tasks.isDone()) {
        System.out.println(future.get());
      }
      System.out.println(tasks.get());
    } catch (ExecutionException | InterruptedException ignored) {

    }
  }

  @Contract(" -> new")
  public static @NotNull CompletableFuture<String> performWork() {
    return CompletableFuture.supplyAsync(() -> {
      String taskMessage = "First task complete!";
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ex) {
        System.out.println(ex.getMessage());
      }
      System.out.println(taskMessage);
      return taskMessage;
    });
  }

  @Contract("_ -> new")
  public static @NotNull CompletableFuture<String> performSecondWork(String message) {
    return CompletableFuture.supplyAsync(() -> {
      String taskMessage = message + " Another task complete!";
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException ex) {
        System.out.println(ex.getMessage());
      }
      return taskMessage;
    });
  }
}
```

### 3.7 ScheduledExecutorService

Introduction  
An ExecutorService that can schedule commands to run after a given delay, or to execute periodically.  
The schedule methods create tasks with various delays and return a task object that can be used to cancel or check
execution.  
The scheduleAtFixedRate and scheduleWithFixedDelay methods create and execute tasks that run periodically until
cancelled.  
Commands submitted using the Executor#execute(Runnable) and ExecutorService submit methods are scheduled with a
requested delay of zero.  
Zero and negative delays (but not periods) are also allowed in schedule methods, and are treated as requests for
immediate execution.  
All schedule methods accept relative delays and periods as arguments, not absolute times or dates.  
It is a simple matter to transform an absolute time represented as a java.util.Date to the required form.  
For example, to schedule at a certain future date, you can use:

```
schedule(task, date.getTime() - System.currentTimeMillis(), TimeUnit.MILLISECONDS).
```

Beware however that expiration of a relative delay need not coincide with the current Date at which the task is
enabled due to network time synchronization protocols, clock drift, or other factors.  
The Executors class provides convenient factory methods for the ScheduledExecutorService implementations provided in
this package.

Introduction  
The following code shows how to use ScheduledExecutorService from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.Contract;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import static java.util.concurrent.TimeUnit.SECONDS;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-20 11:34
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    ScheduledExecutorService executorService = Executors.newScheduledThreadPool(3);
    ScheduledTask task1 = new ScheduledTask(1);
    ScheduledTask task2 = new ScheduledTask(2);
    // Task #1 will run after 2 seconds
    executorService.schedule(task1, 2, SECONDS);
    // Task #2 will run after 5 seconds delay and keep running every 10 seconds
    executorService.scheduleAtFixedRate(task2, 5, 10, SECONDS);
    try {
      SECONDS.sleep(60);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    executorService.shutdown();
  }

  private static class ScheduledTask implements Runnable {
    private int taskId;

    @Contract(pure = true)
    public ScheduledTask(int taskId) {
      this.taskId = taskId;
    }

    @Override
    public void run() {
      LocalDateTime currentDateTime = LocalDateTime.now();
      System.out.println("Task #" + taskId + " ran at " + currentDateTime);
    }
  }
}
```

+ schedule(Callable<V> callable, long delay, TimeUnit unit)  
  Java ScheduledExecutorService schedule(Callable<V> callable, long delay, TimeUnit unit) Submits a value-returning
  one-shot task that becomes enabled after the given delay.  
  The method schedule(Callable<V> callable, long delay, TimeUnit unit) from ScheduledExecutorService is declared as:
  ```
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit);
  ```
  Parameter  
  The method schedule(Callable<V> callable, long delay, TimeUnit unit) has the following parameter:
    + Callable callable - the function to execute
    + long delay - the time from now to delay execution
    + TimeUnit unit - the time unit of the delay parameter

  Return  
  The method schedule(Callable<V> callable, long delay, TimeUnit unit) returns a ScheduledFuture that can be used to
  extract result or cancel  
  Exception  
  The method schedule(Callable<V> callable, long delay, TimeUnit unit) throws the following exceptions:
    + RejectedExecutionException - if the task cannot be scheduled for execution
    + NullPointerException - if callable or unit is null


+ schedule(Runnable command, long delay, TimeUnit unit)  
  Java ScheduledExecutorService schedule(Runnable command, long delay, TimeUnit unit) Submits a one-shot task that
  becomes enabled after the given delay.  
  Syntax  
  The method schedule(Runnable command, long delay, TimeUnit unit) from ScheduledExecutorService is declared as:
  ```
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);
  ```
  Parameter  
  The method schedule(Runnable command, long delay, TimeUnit unit) has the following parameter:
    + Runnable command - the task to execute
    + long delay - the time from now to delay execution
    + TimeUnit unit - the time unit of the delay parameter

  Return  
  The method schedule(Runnable command, long delay, TimeUnit unit) returns a ScheduledFuture representing pending
  completion of the task and whose get() method will return null upon completion  
  Exception  
  The method schedule(Runnable command, long delay, TimeUnit unit) throws the following exceptions:
    + RejectedExecutionException - if the task cannot be scheduled for execution
    + NullPointerException - if command or unit is null


+ scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)  
  Introduction  
  Java ScheduledExecutorService scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
  Submits a periodic action that becomes enabled first after the given initial delay, and subsequently with the given
  period;  
  that is, executions will commence after initialDelay, then (initialDelay + period), then (initialDelay + 2 
  period), and so on.  
  The sequence of task executions continues indefinitely until one of the following exceptional completions occur:
    + The task is Future cancel explicitly cancelled via the returned future.
    + The executor terminates, also resulting in task cancellation.
    + An execution of the task throws an exception. In this case calling (Future#get() get) on the returned future will
      throw ExecutionException, holding the exception as its cause.

  Subsequent executions are suppressed.  
  Subsequent calls to (Future#isDone isDone()) on the returned future will return true.  
  If any execution of this task takes longer than its period, then subsequent executions may start late, but will not
  concurrently execute.  
  Syntax  
  The method scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) from
  ScheduledExecutorService is declared as:
  ```
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);
  ```
  Parameter  
  The method scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) has the following
  parameter:
    + Runnable command - the task to execute
    + long initialDelay - the time to delay first execution
    + long period - the period between successive executions
    + TimeUnit unit - the time unit of the initialDelay and period parameters

  Return  
  The method scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) returns a
  ScheduledFuture representing pending completion of the series of repeated tasks.  
  The future's (Future#get() get()) method will never return normally, and will throw an exception upon task
  cancellation or abnormal termination of a task execution.  
  Exception  
  The method scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) throws the
  following exceptions:
    + RejectedExecutionException - if the task cannot be scheduled for execution
    + NullPointerException - if command or unit is null
    + IllegalArgumentException - if period less than or equal to zero


+ shutdown()  
  Introduction  
  Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.  
  Invocation has no additional effect if already shut down.  
  This method does not wait for previously submitted tasks to complete execution.  
  Use (#awaitTermination awaitTermination) to do that.  
  Syntax  
  The method shutdown() from ScheduledExecutorService is declared as:
  ```
  void shutdown();
  ```
  Exception  
  The method shutdown() throws the following exceptions:
    + SecurityException - if a security manager exists and shutting down this ExecutorService may manipulate threads
      that the caller is not permitted to modify because it does not hold java.lang.RuntimePermission("
      modifyThread"), or the security manager's checkAccess method denies access.

### 3.8 ScheduledFuture

Introduction  
A delayed result-bearing action that can be cancelled.  
Usually a scheduled future is the result of scheduling a task with a ScheduledExecutorService.

+ cancel(boolean mayInterruptIfRunning)  
  Introduction  
  Attempts to cancel execution of this task.  
  This attempt will fail if the task has already completed, has already been cancelled, or could not be cancelled for
  some other reason.  
  If successful, and this task has not started when cancel is called, this task should never run.  
  If the task has already started, then the mayInterruptIfRunning parameter determines whether the thread executing
  this task should be interrupted in an attempt to stop the task.  
  After this method returns, subsequent calls to #isDone will always return true.  
  Subsequent calls to #isCancelled will always return true if this method returned true.  
  Syntax  
  The method cancel() from ScheduledFuture is declared as:
  ```
  boolean cancel(boolean mayInterruptIfRunning);
  ```
  Parameter  
  The method cancel() has the following parameter:
    + boolean mayInterruptIfRunning - true if the thread executing this task should be interrupted; otherwise,
      in-progress tasks are allowed to complete

  Return  
  The method cancel() returns false if the task could not be cancelled, typically because it has already
  completed normally; true otherwise

### 3.9 ScheduledThreadPoolExecutor

Introduction  
A ThreadPoolExecutor that can additionally schedule commands to run after a given delay, or to execute
periodically.  
This class is preferable to java.util.Timer when multiple worker threads are needed, or when the additional
flexibility or capabilities of ThreadPoolExecutor (which this class extends) are required.  
Delayed tasks execute no sooner than they are enabled, but without any real-time guarantees about when, after they are
enabled, they will commence.  
Tasks scheduled for exactly the same execution time are enabled in first-in-first-out (FIFO) order of submission.  
When a submitted task is cancelled before it is run, execution is suppressed.  
By default, such a cancelled task is not automatically removed from the work queue until its delay elapses.  
While this enables further inspection and monitoring, it may also cause unbounded retention of cancelled tasks.  
To avoid this, use #setRemoveOnCancelPolicy to cause tasks to be immediately removed from the work queue at time of
cancellation.  
Successive executions of a periodic task scheduled via (#scheduleAtFixedRate scheduleAtFixedRate) or (
scheduleWithFixedDelay scheduleWithFixedDelay) do not overlap.  
While different executions may be performed by different threads, the effects of prior executions happen-before those of
subsequent ones.  
While this class inherits from ThreadPoolExecutor, a few of the inherited tuning methods are not useful for it.  
In particular, because it acts as a fixed-sized pool using corePoolSize threads and an unbounded queue, adjustments
to maximumPoolSize have no useful effect.  
Additionally, it is almost never a good idea to set corePoolSize to zero or use allowCoreThreadTimeOut because
this may leave the pool without threads to handle tasks once they become eligible to run.  
As with ThreadPoolExecutor, if not otherwise specified, this class uses Executors#defaultThreadFactory as the
default thread factory, and ThreadPoolExecutor.AbortPolicy as the default rejected execution handler.  
Extension notes: This class overrides the (ThreadPoolExecutor#execute(Runnable) execute) and (
AbstractExecutorService#submit(Runnable) submit) methods to generate internal ScheduledFuture objects to control
per-task delays and scheduling.  
To preserve functionality, any further overrides of these methods in subclasses must invoke superclass versions, which
effectively disables additional task customization.  
However, this class provides alternative protected extension method decorateTask (one version each for Runnable
and Callable) that can be used to customize the concrete task types used to execute commands entered via execute
, submit, schedule, scheduleAtFixedRate, and scheduleWithFixedDelay.  
By default, a ScheduledThreadPoolExecutor uses a task type extending FutureTask.

Example  
The following code shows how to use ScheduledThreadPoolExecutor from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.Application;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.ScheduledThreadPoolExecutorExample1
  @createTime: 2022-01-20 12:39
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(3);
    executor.scheduleAtFixedRate(new Task1(), 0, 5, TimeUnit.SECONDS);
    executor.scheduleAtFixedRate(new Task2(), 1, 2, TimeUnit.SECONDS);
  }

  private static class Task1 implements Runnable {
    @Override
    public void run() {
      System.out.println("Task 1");
    }
  }

  private static class Task2 implements Runnable {
    @Override
    public void run() {
      for (int i = 1; i <= 100; i++) {
        System.out.println(i);
      }
    }
  }
}
```

+ ScheduledThreadPoolExecutor(int corePoolSize)  
  Java ScheduledThreadPoolExecutor ScheduledThreadPoolExecutor(int corePoolSize) Creates a new 
  ScheduledThreadPoolExecutor with the given core pool size.  
  The method ScheduledThreadPoolExecutor(int corePoolSize) is a constructor.  
  Syntax  
  The method ScheduledThreadPoolExecutor(int corePoolSize) from ScheduledThreadPoolExecutor is declared as:
  ```
  public ScheduledThreadPoolExecutor(int corePoolSize)
  ```
  Parameter  
  The method ScheduledThreadPoolExecutor(int corePoolSize) has the following parameter:
    + int corePoolSize - the number of threads to keep in the pool, even if they are idle, unless
      allowCoreThreadTimeOut is set

  Exception  
  The method ScheduledThreadPoolExecutor(int corePoolSize) throws the following exceptions:
    + IllegalArgumentException - if (corePoolSize < 0)


+ schedule(Callable<V> callable, long delay, TimeUnit unit)  
  Java ScheduledThreadPoolExecutor schedule(Callable<V> callable, long delay, TimeUnit unit) @throws
  RejectedExecutionException @throws NullPointerException  
  Syntax  
  The method schedule(Callable<V> callable, long delay, TimeUnit unit) from ScheduledThreadPoolExecutor is declared
  as:
  ```
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit)
  ```
  Parameter  
  The method schedule(Callable<V> callable, long delay, TimeUnit unit) has the following parameter:
    + Callable callable
    + long delay
    + TimeUnit unit


+ schedule(Runnable command, long delay, TimeUnit unit)  
  Java ScheduledThreadPoolExecutor schedule(Runnable command, long delay, TimeUnit unit) @throws
  RejectedExecutionException @throws NullPointerException  
  Syntax  
  The method schedule(Runnable command, long delay, TimeUnit unit) from ScheduledThreadPoolExecutor is declared as:
  ```
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit)
  ```

+ scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)  
  Introduction  
  Submits a periodic action that becomes enabled first after the given initial delay, and subsequently with the given
  period; that is, executions will commence after initialDelay, then (initialDelay + period), then (initialDelay + 2
     period), and so on.  
      The sequence of task executions continues indefinitely until one of the following exceptional completions occur:
        + The task is Future#cancel explicitly cancelled via the returned future.
        + Method #shutdown is called and the getContinueExistingPeriodicTasksAfterShutdownPolicy policy on whether to
          continue after shutdown is not set true, or method #shutdownNow is called; also resulting in task
          cancellation.
        + An execution of the task throws an exception. In this case calling (Future#get() get) on the returned future
          will throw ExecutionException, holding the exception as its cause.

  Subsequent executions are suppressed.  
  Subsequent calls to (Future#isDone isDone()) on the returned future will return true.  
  If any execution of this task takes longer than its period, then subsequent executions may start late, but will not
  concurrently execute.  
  Syntax  
  The method scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) from
  ScheduledThreadPoolExecutor is declared as:
  ```
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit)
  ```

### 3.10 ThreadPoolExecutor

Introduction  
An ExecutorService that executes each submitted task using one of possibly several pooled threads, normally
configured using Executors factory methods.  
Thread pools address two different problems: they usually provide improved performance when executing large numbers of
asynchronous tasks, due to reduced per-task invocation overhead, and they provide a means of bounding and managing the
resources, including threads, consumed when executing a collection of tasks.  
Each ThreadPoolExecutor also maintains some basic statistics, such as the number of completed tasks.  
To be useful across a wide range of contexts, this class provides many adjustable parameters and extensibility hooks.  
However, programmers are urged to use the more convenient Executors factory methods 
Executors#newCachedThreadPool (unbounded thread pool, with automatic thread reclamation), 
Executors#newFixedThreadPool (fixed size thread pool) and Executors#newSingleThreadExecutor (single background
thread), that preconfigure settings for the most common usage scenarios.  
Otherwise, use the following guide when manually configuring and tuning this class:

Core and maximum pool sizes  
A ThreadPoolExecutor will automatically adjust the pool size (see #getPoolSize) according to the bounds set by
corePoolSize (see #getCorePoolSize) and maximumPoolSize (see #getMaximumPoolSize).  
When a new task is submitted in method #execute(Runnable), if fewer than corePoolSize threads are running, a new
thread is created to handle the request, even if other worker threads are idle.  
Else if fewer than maximumPoolSize threads are running, a new thread will be created to handle the request only if the
queue is full.  
By setting corePoolSize and maximumPoolSize the same, you create a fixed-size thread pool.  
By setting maximumPoolSize to an essentially unbounded value such as Integer.MAX_VALUE, you allow the pool to
accommodate an arbitrary number of concurrent tasks.  
Most typically, core and maximum pool sizes are set only upon construction, but they may also be changed dynamically
using #setCorePoolSize and #setMaximumPoolSize.

On-demand construction  
By default, even core threads are initially created and started only when new tasks arrive, but this can be overridden
dynamically using method #prestartCoreThread or #prestartAllCoreThreads.  
You probably want to prestart threads if you construct the pool with a non-empty queue.

Creating new threads  
New threads are created using a ThreadFactory.  
If not otherwise specified, a Executors#defaultThreadFactory is used, that creates threads to all be in the same 
ThreadGroup and with the same NORM_PRIORITY priority and non-daemon status.  
By supplying a different ThreadFactory, you can alter the thread's name, thread group, priority, daemon status, etc.  
If a ThreadFactory fails to create a thread when asked by returning null from newThread, the executor will continue,
but might not be able to execute any tasks.  
Threads should possess the "modifyThread" RuntimePermission.  
If worker threads or other threads using the pool do not possess this permission, service may be degraded: configuration
changes may not take effect in a timely manner, and a shutdown pool may remain in a state in which termination is
possible but not completed.

Keep-alive times  
If the pool currently has more than corePoolSize threads, excess threads will be terminated if they have been idle for
more than the keepAliveTime (see #getKeepAliveTime(TimeUnit)).  
This provides a means of reducing resource consumption when the pool is not being actively used.  
If the pool becomes more active later, new threads will be constructed.  
This parameter can also be changed dynamically using method #setKeepAliveTime(long, TimeUnit).  
Using a value of Long.MAX_VALUE TimeUnit#NANOSECONDS effectively disables idle threads from ever terminating prior
to shut down.  
By default, the keep-alive policy applies only when there are more than corePoolSize threads, but method 
allowCoreThreadTimeOut(boolean) can be used to apply this time-out policy to core threads as well, so long as the
keepAliveTime value is non-zero.

Queuing  
Any BlockingQueue may be used to transfer and hold submitted tasks.  
The use of this queue interacts with pool sizing:

+ If fewer than corePoolSize threads are running, the Executor always prefers adding a new thread rather than queuing.
+ If corePoolSize or more threads are running, the Executor always prefers queuing a request rather than adding a new
  thread.
+ If a request cannot be queued, a new thread is created unless this would exceed maximumPoolSize, in which case, the
  task will be rejected.

There are three general strategies for queuing:

1. Direct handoffs.

A good default choice for a work queue is a SynchronousQueue that hands off tasks to threads without otherwise
holding them.  
Here, an attempt to queue a task will fail if no threads are immediately available to run it, so a new thread will be
constructed.  
This policy avoids lockups when handling sets of requests that might have internal dependencies.  
Direct handoffs generally require unbounded maximumPoolSizes to avoid rejection of new submitted tasks.  
This in turn admits the possibility of unbounded thread growth when commands continue to arrive on average faster than
they can be processed.

+ Unbounded queues.  
  Using an unbounded queue (for example a LinkedBlockingQueue without a predefined capacity) will cause new tasks to
  wait in the queue when all corePoolSize threads are busy.  
  Thus, no more than corePoolSize threads will ever be created.  
  (And the value of the maximumPoolSize therefore doesn't have any effect.) This may be appropriate when each task is
  completely independent of others, so tasks cannot affect each others execution; for example, in a web page server.  
  While this style of queuing can be useful in smoothing out transient bursts of requests, it admits the possibility of
  unbounded work queue growth when commands continue to arrive on average faster than they can be processed.

+ Bounded queues.  
  A bounded queue (for example, an ArrayBlockingQueue) helps prevent resource exhaustion when used with finite
  maximumPoolSizes, but can be more difficult to tune and control.  
  Queue sizes and maximum pool sizes may be traded off for each other: Using large queues and small pools minimizes CPU
  usage, OS resources, and context-switching overhead, but can lead to artificially low throughput.  
  If tasks frequently block (for example if they are I/O bound), a system may be able to schedule time for more threads
  than you otherwise allow.  
  Use of small queues generally requires larger pool sizes, which keeps CPUs busier but may encounter unacceptable
  scheduling overhead, which also decreases throughput.

Rejected tasks  
New tasks submitted in method #execute(Runnable) will be rejected when the Executor has been shut down, and also when
the Executor uses finite bounds for both maximum threads and work queue capacity, and is saturated.  
In either case, the execute method invokes the (RejectedExecutionHandler#rejectedExecution(Runnable,
ThreadPoolExecutor)) method of its RejectedExecutionHandler.  
Four predefined handler policies are provided:
> In the default ThreadPoolExecutor.AbortPolicy, the handler throws a runtime RejectedExecutionException upon
> rejection.  
> In ThreadPoolExecutor.CallerRunsPolicy, the thread that invokes execute itself runs the task.This provides a
> simple feedback control mechanism that will slow down the rate that new tasks are submitted.  
> In ThreadPoolExecutor.DiscardPolicy, a task that cannot be executed is simply dropped.  
> In ThreadPoolExecutor.DiscardOldestPolicy, if the executor is not shut down, the task at the head of the work queue
> is dropped, and then execution is retried (which can fail again, causing this to be repeated.) It is possible to
> define
> and use other kinds of RejectedExecutionHandler classes.

Doing so requires some care especially when policies are designed to work only under particular capacity or queuing
policies.

Hook methods  
This class provides protected overridable (#beforeExecute(Thread, Runnable)) and (#afterExecute(Runnable, Throwable))
methods that are called before and after execution of each task.  
These can be used to manipulate the execution environment; for example, reinitializing ThreadLocals, gathering
statistics, or adding log entries.  
Additionally, method #terminated can be overridden to perform any special processing that needs to be done once the
Executor has fully terminated.  
If hook, callback, or BlockingQueue methods throw exceptions, internal worker threads may in turn fail, abruptly
terminate, and possibly be replaced.

Queue maintenance  
Method #getQueue() allows access to the work queue for purposes of monitoring and debugging.  
Use of this method for any other purpose is strongly discouraged.  
Two supplied methods, #remove(Runnable) and #purge are available to assist in storage reclamation when large numbers
of queued tasks become cancelled.

Reclamation  
A pool that is no longer referenced in a program AND has no remaining threads may be reclaimed (garbage collected)
without being explicitly shutdown.  
You can configure a pool to allow all unused threads to eventually die by setting appropriate keep-alive times, using a
lower bound of zero core threads and/or setting #allowCoreThreadTimeOut(boolean).  
Extension example.  
Most extensions of this class override one or more of the protected hook methods.

Example  
The following code shows how to use ThreadPoolExecutor from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-20 14:47
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws ExecutionException, InterruptedException {
    BlockingQueue<Runnable> blockingQueue = new ArrayBlockingQueue<>(48);
    ThreadPoolExecutor executor = new ThreadPoolExecutor
        (6, 10, 1, TimeUnit.SECONDS, blockingQueue);
    List<Future<String>> futures = new ArrayList<>();
    for (int i = 1; i <= 20; i++) {
      Future<String> future = executor.submit(new SubTask(i));
      futures.add(future);
    }
    for (Future<String> future : futures) {
      System.out.println("Output Returned is: " + future.get());
    }
  }

  private static class SubTask implements Callable<String> {
    private int id;

    @Contract(pure = true)
    public SubTask(int id) {
      this.id = id;
    }

    @Override
    public @NotNull String call() throws Exception {
      System.out.println("Running " + this.id);
      return "Called " + this.id;
    }
  }
}
```

+ SHUTDOWN  
  Java ThreadPoolExecutor SHUTDOWN null  
  Syntax  
  The field SHUTDOWN() from ThreadPoolExecutor is declared as:
  ```
  private static final int SHUTDOWN = 0 << COUNT_BITS;
  ```

+ ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable>
  workQueue)  
  Introduction  
  Creates a new ThreadPoolExecutor with the given initial parameters, the default thread factory and the default
  rejected execution handler.  
  It may be more convenient to use one of the Executors factory methods instead of this general purpose
  constructor.  
  Syntax  
  The method ThreadPoolExecutor() from ThreadPoolExecutor is declared as:
  ```
  public ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
        BlockingQueue<Runnable> workQueue)
  ```
  Parameter  
  the method ThreadPoolExecutor() has the following parameter:
    + int corePoolSize - the number of threads to keep in the pool, even if they are idle, unless
      allowCoreThreadTimeOut is set
    + int maximumPoolSize - the maximum number of threads to allow in the pool
    + long keepAliveTime - when the number of threads is greater than the core, this is the maximum time that excess
      idle threads will wait for new tasks before terminating.
    + TimeUnit unit - the time unit for the keepAliveTime argument
    + BlockingQueue workQueue - the queue to use for holding tasks before they are executed. This queue will hold
      only the Runnable tasks submitted by the execute method.

  Exception  
  The method ThreadPoolExecutor() throws the following exceptions:
    + IllegalArgumentException - if one of the following holds:<br> (corePoolSize < 0)<br> (keepAliveTime < 0)<br> (
      maximumPoolSize <= 0)<br> (maximumPoolSize < corePoolSize)
    + NullPointerException - if workQueue is null


+ awaitTermination(long timeout, TimeUnit unit)  
  Java ThreadPoolExecutor awaitTermination(long timeout, TimeUnit unit) null  
  Syntax  
  The method awaitTermination() from ThreadPoolExecutor is declared as:
  ```
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException
  ```

+ execute(Runnable command)  
  Introduction  
  Executes the given task sometime in the future.  
  The task may execute in a new thread or in an existing pooled thread.  
  If the task cannot be submitted for execution, either because this executor has been shutdown or because its capacity
  has been reached, the task is handled by the current RejectedExecutionHandler.  
  Syntax  
  The method execute() from ThreadPoolExecutor is declared as:
  ```
  public void execute(Runnable command)
  ```
  Parameter  
  The method execute() has the following parameter:
    + Runnable command - the task to execute

  Exception  
  The method execute() throws the following exceptions:
    + RejectedExecutionException - at discretion of RejectedExecutionHandler, if the task cannot be accepted for
      execution
    + NullPointerException - if command is null


+ getPoolSize()  
  Java ThreadPoolExecutor getPoolSize() Returns the current number of threads in the pool.  
  Syntax  
  The method getPoolSize() from ThreadPoolExecutor is declared as:
  ```
  public int getPoolSize()
  ```
  Return  
  The method getPoolSize() returns the number of threads


+ prestartAllCoreThreads()  
  Introduction  
  Starts all core threads, causing them to idly wait for work.  
  This overrides the default policy of starting core threads only when new tasks are executed.  
  Syntax  
  The method prestartAllCoreThreads() from ThreadPoolExecutor is declared as:
  ```
  public int prestartAllCoreThreads()
  ```
  Return  
  The method prestartAllCoreThreads() returns the number of threads started


+ setRejectedExecutionHandler(RejectedExecutionHandler handler)  
  Java ThreadPoolExecutor setRejectedExecutionHandler(RejectedExecutionHandler handler) Sets a new handler for
  unexecutable tasks.  
  Syntax  
  The method setRejectedExecutionHandler() from ThreadPoolExecutor is declared as:
  ```
  public void setRejectedExecutionHandler(RejectedExecutionHandler handler)
  ```
  Parameter  
  The method setRejectedExecutionHandler() has the following parameter:
    + RejectedExecutionHandler handler - the new handler

  Exception  
  The method setRejectedExecutionHandler() throws the following exceptions:
    + NullPointerException - if handler is null

## 四.ForkJoin

### 4.1 Fork/Join Framework

What is Fork/Join Framework  
Parallel programming takes advantage of computers with two or more processors.  
The multi-processor environments significantly increase program performance.  
Java 7 Fork/Join Framework can support parallel programming.  
The Fork/Join Framework is defined in the java.util.concurrent package.  
The Fork/Join Framework simplifies the creation and use of multiple threads.  
And it automatically uses multiple processors.  
By using the Fork/Join Framework you enable your applications to use the available processors.

Thread vs Fork/Join Framework  
Thread class uses idle time on a single CPU, such as when a program is waiting for user input.  
On a single-CPU system, multithreading makes two or more tasks to share the CPU.  
This type of multithreading was not optimized for the multicore computers that have two or more CPUs.  
With two or more CPUs, we can run portions of a program simultaneously with each part executing on its own CPU.  
Fork/Join Framework is for the second type of multithreading.

Main Fork/Join Classes
The Fork/Join Framework is packaged in java.util.concurrent.  
At the core of the Fork/Join Framework are the following four classes:
> ForkJoinTask<V>        An abstract class that defines a task  
> ForkJoinPool Manages the execution of ForkJoinTask  
> RecursiveAction A subclass of ForkJoinTask<V> for tasks that do not return values  
> RecursiveTask<V>      A subclass of ForkJoinTask<V> for tasks that return values

A ForkJoinPool manages the execution of ForkJoinTasks.  
ForkJoinTask is an abstract class that is extended by the abstract classes RecursiveAction and RecursiveTask
.  
Our code will extend these classes to create a task.

### 4.2 ForkJoinTask<V>

ForkJoinTask<V> is an abstract class that defines a task that can be managed by a ForkJoinPool.  
The type parameter V specifies the result type of the task.  
ForkJoinTask represents lightweight abstraction of a task.  
ForkJoinTasks are executed by threads managed by a thread pool of type ForkJoinPool.  
This mechanism allows a large number of tasks to be managed by a small number of actual threads.  
ForkJoinTasks are efficient when compared to threads.  
ForkJoinTask defines many methods. fork() and join() are shown here:

```
final ForkJoinTask<V> fork()
```

The fork() method submits the invoking task for asynchronous execution of the invoking task.  
If fork() is not called while executing within a ForkJoinPool, then a common pool is automatically used.  
The join() method waits until the task on which it is called terminates.  
The result of the task is returned.  
Through the use of fork() and join(), you can start one or more new tasks and then wait for them to finish.  
ForkJoinTask method invoke() combines the fork and join operations into a single call because it begins a task and
then waits for it to end.  
It is shown here:

```
final V invoke()
```

The result of the invoking task is returned.  
You can invoke more than one task at a time by using invokeAll(). Two of its forms are shown here:

```
static void invokeAll(ForkJoinTask<?> taskA, ForkJoinTask<?> taskB)
static void invokeAll(ForkJoinTask<?> ... taskList)
```

In the first case, taskA and taskB are executed.  
In the second case, all specified tasks are executed.  
In both cases, the calling thread waits until all of the specified tasks have terminated.

### 4.3 RecursiveAction

Introduction  
A subclass of ForkJoinTask is RecursiveAction.  
This class encapsulates a task that does not return a result.  
Our code will extend RecursiveAction to create a task that has a void return type.  
RecursiveAction specifies four methods, but only one is often used: compute().  
The compute() method represents the computational portion of the task.

```
protected abstract void compute()
```

compute() is protected and abstract.

A recursive resultless ForkJoinTask.  
This class establishes conventions to parameterize resultless actions as Void ForkJoinTasks.  
Because null is the only valid value of type Void, methods such as join always return null upon
completion.

Example  
The following code shows how to use RecursiveAction from java.util.concurrent.

```java
package cn.tuyucheng.edu.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.concurrent.Application
  @createTime: 2022-01-20 15:49
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  private static int SEQ_THRESHOLD;

  public static void main(String[] args) throws Exception {
    int size = 100000;
    int[] v1 = new int[size];
    for (int i = 0; i < size; i++) {
      v1[i] = i;
    }
    for (SEQ_THRESHOLD = 10; SEQ_THRESHOLD < size; SEQ_THRESHOLD += 50) {
      double avgTime = 0.0;
      int samples = 5;
      for (int i = 0; i < samples; i++) {
        long startTime = System.nanoTime();
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.invoke(new MyAction(0, size, v1));
        long endTime = System.nanoTime();
        double secsTaken = (endTime - startTime) / 1.0e9;
        avgTime += secsTaken;
      }
      System.out.println(SEQ_THRESHOLD + " " + (avgTime / samples));
    }
  }

  private static class MyAction extends RecursiveAction {
    int SEQ_THRESHOLD = Application.SEQ_THRESHOLD;

    private int[] v1;
    private int start, end;

    public MyAction(int start, int end, int[] v1) {
      this.start = start;
      this.end = end;
      this.v1 = v1;
    }


    @Override
    protected void compute() {
      if (end - start < SEQ_THRESHOLD) {
        for (int i = start; i < end; i++) {
          v1[i] = v1[i] + i  i;
        }
      } else {
        int mid = (start + end) / 2;
        invokeAll(new MyAction(start, mid, v1), new MyAction(mid, end, v1));
      }
    }
  }
}
```

### 4.4 RecursiveTask<V> class

RecursiveTask<V> is a subclass of ForkJoinTask and encapsulates a task that returns a result.  
The result type is specified by V.  
Our code will extend RecursiveTask<V> to create a task that returns a value.  
RecursiveTask<V> specifies four methods, but often only the abstract compute() method is used, which represents
the computational portion of the task.  
When you extend RecursiveTask<V> to create a concrete class, put the code that represents the task inside compute()
.  
This code must also return the result of the task.  
The compute() method is defined by RecursiveTask<V> like this:

```
protected abstract V compute()
```

RecursiveTask is used to implement a recursive, divide-and-conquer strategy for tasks that return results.

Example  
To create a task that returns a result, use RecursiveTask.  
The following program demonstrates RecursiveTask.  
Its compute() method returns a result.  
We must aggregate the results, so that when the first invocation finishes, it returns the overall result.  
The following code creates a task called Sum that returns the summation of the values in an array of double.

```java
package cn.tuyucheng.edu.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.forkjoin.Application
  @createTime: 2021/12/30-23:57
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application { // A simple example that uses RecursiveTask<V>.
  public static void main(String[] args) {
    ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();
    double[] nums = new double[5000];
    for (int i = 0; i < nums.length; i++)
      nums[i] = i;
    Sum task = new Sum(nums, 0, nums.length);
    // Start the ForkJoinTasks. Notice that in this case, invoke() returns a result.
    double summation = forkJoinPool.invoke(task); // The calling code waits until invoke() returns.
    System.out.println("Summation " + summation);
  }

  // A RecursiveTask that computes the summation of an array of doubles.
  private static class Sum extends RecursiveTask<Double> {
    // The sequential threshold value.
    private final int seqThresHold = 500;
    // Array to be accessed.
    private double[] data;
    // Determines what part of data to process.
    private int start, end;

    public Sum(double[] data, int start, int end) {
      this.data = data;
      this.start = start;
      this.end = end;
    }

    protected Double compute() { // Find the summation of an array of doubles.
      double sum = 0;
      // If number of elements is below the sequential threshold, then process sequentially.
      if ((end - start) < seqThresHold) {
        // Sum the elements.
        for (int i = start; i < end; i++)
          sum += data[i];
      } else {
        // Otherwise, continue to break the data into smaller pieces. Find the midpoint.
        int middle = (start + end) / 2;
        // Invoke new tasks, using the subdivided data.
        Sum subTaskA = new Sum(data, start, middle);
        Sum subTaskB = new Sum(data, middle, end);
        // Start each subtask by forking.
        subTaskA.fork();
        subTaskB.fork();
        // Wait for the subtasks to return, and aggregate the results.
        sum = subTaskA.join() + subTaskB.join();
      }
      return sum; // Return the final sum.
    }
  }
}
```

Here's the output from the program:
> Summation 1.24975E7

Note  
Two subtasks are executed by calling fork(), as shown here:

```
subTaskA.fork();
subTaskB.fork();
```

In this case, fork() is used because it starts a task but does not wait for it to finish.  
It then adds the results of each and assigns the total to sum.  
Thus, the summation of each subtask is added to the running total.  
Finally, compute() ends by returning sum, which will be the final total when the first invocation returns.

Subtasks  
There are other ways to approach the handling of the asynchronous execution of the sub tasks.  
For example, the following sequence uses fork() to start subTaskA and uses invoke() to start and wait for 
subTaskB:

```
subTaskA.fork();
sum = subTaskB.invoke() + subTaskA.join();
```

Another alternative is to have subTaskB call compute() directly, as shown here:

```
subTaskA.fork();
sum = subTaskB.compute() + subTaskA.join();
```

+ join()  
  Introduction  
  Returns the result of the computation when it isDone.  
  This method differs from #get() in that abnormal completion results in RuntimeException or Error, not 
  ExecutionException, and that interrupts of the calling thread do not cause the method to abruptly return by
  throwing InterruptedException.  
  Syntax  
  The method join() from RecursiveTask is declared as:
  ```
  public final V join()
  ```
  Return  
  The method join() returns the computed result

### 4.5 ForkJoinPool

The execution of ForkJoinTasks takes place within a ForkJoinPool, which manages the execution of the tasks.  
In order to execute a ForkJoinTask, you must first have a ForkJoinPool.  
There are two ways to acquire a ForkJoinPool.  
First, you can explicitly create one by using a ForkJoinPool constructor.  
Second, you can use what is referred to as the common pool.  
The common pool added by JDK 8 is a static ForkJoinPool that is automatically available for your use.  
ForkJoinPool defines several constructors.  
Here are two commonly used ones:

```
ForkJoinPool()
ForkJoinPool(int pLevel )
```

The first creates a default pool that supports a level of parallelism equal to the number of processors available in the
system.  
The second lets you specify the level of parallelism.  
Its value must be greater than zero and not more than the limits of the implementation.  
The level of parallelism determines the number of threads that can execute concurrently.  
The level of parallelism does not limit the number of tasks that can be managed by the pool.  
A ForkJoinPool can manage many more tasks than its level of parallelism.  
The level of parallelism is only a target. It is not a guarantee.  
After you have created an instance of ForkJoinPool, you can start a task in a number of different ways.  
The first task started is often thought of as the main task.  
The main task begins subtasks that are also managed by the pool.  
One common way to begin a main task is to call invoke() on the ForkJoinPool.  
It is shown here:

```
<T> T invoke(ForkJoinTask<T> task)
```

This method begins the task specified by task, and it returns the result of the task.  
The calling code waits until invoke() returns.  
To start a task without waiting for its completion, you can use execute().  
Here is one of its forms:

```
void execute(ForkJoinTask<?>  task)
```

Beginning withJava8, we do not need to explicitly construct a ForkJoinPool because a common pool is available for your
use.  
If you are not using a pool that you explicitly created, then the common pool will automatically be used.  
You can obtain a reference to the common pool by calling commonPool(), which is defined by ForkJoinPool.

```
static ForkJoinPool commonPool()
```

A reference to the common pool is returned.  
The common pool provides a default level of parallelism.  
It can be set by use of a system property.  
The default common pool is a good choice for many applications.  
There are two basic ways to start a task using the common pool.  
First, you can obtain a reference to the pool by calling commonPool() and then use that reference to call 
invoke() or execute().  
Second, you can call ForkJoinTask methods such as fork() or invoke() on the task from outside its
computational portion.  
In this case, the common pool will automatically be used.  
fork() and invoke() will start a task using the common pool if the task is not already running within a 
ForkJoinPool.  
ForkJoinPool manages the execution of its threads using an approach called work-stealing.  
Each worker thread maintains a queue of tasks.  
If one worker thread's queue is empty, it will take a task from another worker thread.

Daemon threads  
ForkJoinPool uses daemon threads.  
A daemon thread is automatically terminated when all user threads have terminated.  
There is no need to explicitly shut down a ForkJoinPool.  
With the exception of the common pool, it is possible to do so by calling shutdown().  
The shutdown() method has no effect on the common pool.

Divide-and-Conquer Strategy  
Fork/Join Framework will employ a divide-and-conquer strategy that is based on recursion.  
This is why the two subclasses of ForkJoinTask are called RecursiveAction and RecursiveTask.  
The divide-and-conquer strategy is based on recursively dividing a task into smaller subtasks until the size of a
subtask is small enough to be handled sequentially.  
The divide-and-conquer strategy can occur in parallel.  
TheJavaAPI documentation for ForkJoinTask<T> states that a task should perform somewhere between 100 and 10,000
computational steps.

Introduction  
An ExecutorService for running ForkJoinTasks.  
A ForkJoinPool provides the entry point for submissions from non-ForkJoinTask clients, as well as management and
monitoring operations.  
A ForkJoinPool differs from other kinds of ExecutorService mainly by virtue of employing work-stealing: all
threads in the pool attempt to find and execute tasks submitted to the pool and/or created by other active tasks (
eventually blocking waiting for work if none exist).  
This enables efficient processing when most tasks spawn other subtasks (as do most ForkJoinTasks), as well as when
many small tasks are submitted to the pool from external clients.  
Especially when setting asyncMode to true in constructors, ForkJoinPools may also be appropriate for use with
event-style tasks that are never joined.  
All worker threads are initialized with Thread#isDaemon set true.  
A static #commonPool() is available and appropriate for most applications.  
The common pool is used by any ForkJoinTask that is not explicitly submitted to a specified pool.  
Using the common pool normally reduces resource usage (its threads are slowly reclaimed during periods of non-use, and
reinstated upon subsequent use).  
For applications that require separate or custom pools, a ForkJoinPool may be constructed with a given target
parallelism level; by default, equal to the number of available processors.  
The pool attempts to maintain enough active (or available) threads by dynamically adding, suspending, or resuming
internal worker threads, even if some tasks are stalled waiting to join others.  
However, no such adjustments are guaranteed in the face of blocked I/O or other unmanaged synchronization.  
The nested ManagedBlocker interface enables extension of the kinds of synchronization accommodated.  
The default policies may be overridden using a constructor with parameters corresponding to those documented in class
ThreadPoolExecutor.  
In addition to execution and lifecycle control methods, this class provides status check methods (for example 
getStealCount) that are intended to aid in developing, tuning, and monitoring fork/join applications.  
Also, method #toString returns indications of pool state in a convenient form for informal monitoring.  
As is the case with other ExecutorServices, there are three main task execution methods summarized in the following
table.  
These are designed to be used primarily by clients not already engaged in fork/join computations in the current pool.  
The main forms of these methods accept instances of ForkJoinTask, but overloaded forms also allow mixed execution of
plain Runnable- or Callable- based activities as well.  
However, tasks that are already executing in a pool should normally instead use the within-computation forms listed in
the table unless using async event-style tasks that are not usually joined, in which case there is little difference
among choice of methods.

|  | Call from non-fork/join clients | Call from within fork/join computations        |
| -----|-----|------------------------------------------------|
| Arrange async execution | #execute(ForkJoinTask) | ForkJoinTask#fork                              |
| Await and obtain result | #invoke(ForkJoinTask) | ForkJoinTask#invoke                            |
| Arrange exec and obtain Future | #submit(ForkJoinTask) | ForkJoinTask#fork (ForkJoinTasks are Futures)  |

The parameters used to construct the common pool may be controlled by setting the following System getProperty:

+ {@systemProperty java.util.concurrent.ForkJoinPool.common.parallelism} - the parallelism level, a non-negative integer
+ {@systemProperty java.util.concurrent.ForkJoinPool.common.threadFactory} - the class name of a 
  ForkJoinWorkerThreadFactory. The ClassLoader getSystemClassLoader() system class loader is used to load this class.
+ {@systemProperty java.util.concurrent.ForkJoinPool.common.exceptionHandler} - the class name of a 
  UncaughtExceptionHandler. The ClassLoader getSystemClassLoader() system class loader is used to load this class.
+ {@systemProperty java.util.concurrent.ForkJoinPool.common.maximumSpares} - the maximum number of allowed extra threads
  to maintain target parallelism (default 256).

If no thread factory is supplied via a system property, then the common pool uses a factory that uses the system class
loader as the Thread getContextClassLoader() thread context class loader.  
In addition, if a SecurityManager is present, then the common pool uses a factory supplying threads that have no 
Permissions enabled.  
Upon any error in establishing these settings, default parameters are used.  
It is possible to disable or limit the use of threads in the common pool by setting the parallelism property to zero,
and/or using a factory that may return null.  
However doing so may cause unjoined tasks to never be executed.  
Implementation notes: This implementation restricts the maximum number of running threads to 32767.  
Attempts to create pools with greater than the maximum number result in IllegalArgumentException.  
This implementation rejects submitted tasks (that is, by throwing RejectedExecutionException) only when the pool is
shut down or internal resources have been exhausted.

Example  
The following code shows how to use ForkJoinPool from java.util.concurrent.

```java
package cn.tuyucheng.edu.forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.forkjoin.Application
  @createTime: 2022-01-20 16:28
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    List<Integer> list = new ArrayList<>();
    long expectedSum = 0;
    for (int i = 0; i < 10000; i++) {
      int random = 1 + (int) (Math.random()  ((100 - 1) + 1));
      list.add(random);
      expectedSum += random;
    }
    System.out.println("expected sum: " + expectedSum);
    ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
    RecursiveSum recursiveSum = new RecursiveSum(list, 0, list.size());
    long resultSum = forkJoinPool.invoke(recursiveSum);
    System.out.println("recursive-sum: " + resultSum);
  }

  private static class RecursiveSum extends RecursiveTask<Long> {
    private static final int THRESHOLD = 1000;
    private List<Integer> list;
    private int begin;
    private int end;

    public RecursiveSum(List<Integer> list, int begin, int end) {
      super();
      this.list = list;
      this.begin = begin;
      this.end = end;
    }

    @Override
    protected Long compute() {
      final int size = end - begin;
      if (size < THRESHOLD) {
        long sum = 0;
        for (int i = begin; i < end; i++)
          sum += list.get(i);
        return sum;
      } else {
        final int middle = begin + ((end - begin) / 2);
        RecursiveSum sum1 = new RecursiveSum(list, begin, middle);
        sum1.fork();
        RecursiveSum sum2 = new RecursiveSum(list, middle, end);
        return sum2.compute() + sum1.join();
      }
    }
  }
}
```

+ ForkJoinPool()  
  Java ForkJoinPool ForkJoinPool() Creates a ForkJoinPool with parallelism equal to 
  java.lang.Runtime#availableProcessors, using defaults for all other parameters (see ForkJoinPool(int,
  ForkJoinWorkerThreadFactory, UncaughtExceptionHandler, boolean, int, int, int, Predicate, long, TimeUnit)).  
  Syntax  
  The method ForkJoinPool() from ForkJoinPool is declared as:
  ```
  public ForkJoinPool()
  ```
  Exception  
  The method ForkJoinPool() throws the following exceptions:
    + SecurityException - if a security manager exists and the caller is not permitted to modify threads because it does
      not hold java.lang.RuntimePermission("modifyThread")


+ ForkJoinPool(byte forCommonPoolOnly)  
  Java ForkJoinPool ForkJoinPool(byte forCommonPoolOnly) Constructor for common pool using parameters possibly
  overridden by system properties  
  Syntax  
  The method ForkJoinPool() from ForkJoinPool is declared as:
  ```
  private ForkJoinPool(byte forCommonPoolOnly)
  ```
  Parameter  
  The method ForkJoinPool() has the following parameter:
    + byte forCommonPoolOnly


+ ForkJoinPool(int parallelism)  
  Java ForkJoinPool ForkJoinPool(int parallelism) Creates a ForkJoinPool with the indicated parallelism level, using
  defaults for all other parameters (see ForkJoinPool(int, ForkJoinWorkerThreadFactory, UncaughtExceptionHandler,
  boolean, int, int, int, Predicate, long, TimeUnit)).  
  Syntax  
  The method ForkJoinPool() from ForkJoinPool is declared as:
  ```
  public ForkJoinPool(int parallelism)
  ```
  Parameter  
  The method ForkJoinPool() has the following parameter:
    + int parallelism - the parallelism level

  Exception  
  The method ForkJoinPool() throws the following exceptions:
    + IllegalArgumentException - if parallelism less than or equal to zero, or greater than implementation limit
    + SecurityException - if a security manager exists and the caller is not permitted to modify threads because it
      does not hold java.lang.RuntimePermission("modifyThread")


+ invoke(ForkJoinTask<T> task)  
  Introduction  
  Performs the given task, returning its result upon completion.  
  If the computation encounters an unchecked Exception or Error, it is rethrown as the outcome of this invocation.  
  Rethrown exceptions behave in the same way as regular exceptions, but, when possible, contain stack traces (as
  displayed for example using ex.printStackTrace()) of both the current thread as well as the thread actually
  encountering the exception; minimally only the latter.  
  Syntax  
  The method invoke() from ForkJoinPool is declared as:
  ```
  public <T> T invoke(ForkJoinTask<T> task)
  ```
  Parameter  
  The method invoke() has the following parameter:
    + ForkJoinTask task - the task

  Return  
  The method invoke() returns the task's result  
  Exception
  The method invoke() throws the following exceptions:
    + NullPointerException - if the task is null
    + RejectedExecutionException - if the task cannot be scheduled for execution


+ submit(Callable<T> task)  
  Java ForkJoinPool submit(Callable<T> task) @throws NullPointerException if the task is null @throws
  RejectedExecutionException if the task cannot be scheduled for execution  
  Syntax  
  The method submit() from ForkJoinPool is declared as:
  ```
  public <T> ForkJoinTask<T> submit(Callable<T> task)
  ```
  Parameter  
  The method submit() has the following parameter:
    + Callable task - the task

  Return  
  The method submit() returns  
  Exception  
  The method submit() throws the following exceptions:
    + NullPointerException - if the task is null
    + RejectedExecutionException - if the task cannot be scheduled for execution


+ submit(ForkJoinTask<T> task)  
  Java ForkJoinPool submit(ForkJoinTask<T> task) Submits a ForkJoinTask for execution.  
  Syntax  
  The method submit() from ForkJoinPool is declared as:
  ```
  public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task)
  ```
  Parameter  
  The method submit() has the following parameter:
    + ForkJoinTask task - the task to submit

  Return  
  The method submit() returns the task  
  Exception  
  The method submit() throws the following exceptions:
    + NullPointerException - if the task is null
    + RejectedExecutionException - if the task cannot be scheduled for execution


+ submit(Runnable task)  
  Java ForkJoinPool submit(Runnable task) @throws NullPointerException if the task is null @throws
  RejectedExecutionException if the task cannot be scheduled for execution  
  Syntax  
  The method submit() from ForkJoinPool is declared as:
  ```
    @SuppressWarnings("unchecked")
    public ForkJoinTask<?> submit(Runnable task)
  ```
  Parameter  
  The method submit() has the following parameter:
    + Runnable task - the task

  Exception  
  The method submit() throws the following exceptions:
    + NullPointerException - if the task is null
    + RejectedExecutionException - if the task cannot be scheduled for execution


+ toString()  
  Java ForkJoinPool toString() Returns a string identifying this pool, as well as its state, including indications
  of run state, parallelism level, and worker and task counts.  
  Syntax  
  The method toString() from ForkJoinPool is declared as:
  ```
  public String toString()
  ```
  Return  
  The method toString() returns a string identifying this pool, as well as its state

### 4.6 Parallelism Level

The following program section demonstrates different degrees of parallelism and threshold values. One way is to specify
it when you create a ForkJoinPool using this constructor:

```
ForkJoinPool(int pLevel )
```

Here, pLevel specifies the level of parallelism, which must be greater than zero and less than the implementation
defined limit.  
The following program creates a fork/join task that transforms an array of doubles.  
To use the program, specify the threshold value and the level of parallelism on the command line.

```java
package cn.tuyucheng.edu.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.forkjoin.Application
  @createTime: 2021/12/31-0:27
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    int parallelLevel = 1;
    int threshold = 1000;
    // These variables are used to time the task.
    long beginTime, endTime;

    // Create a task pool. Notice that the parallelism level is set.
    ForkJoinPool forkJoinPool = new ForkJoinPool(parallelLevel);
    double[] nums = new double[1000000];

    for (int i = 0; i < nums.length; i++)
      nums[i] = i;

    MyAction task = new MyAction(nums, 0, nums.length, threshold);
    beginTime = System.nanoTime();
    forkJoinPool.invoke(task);
    endTime = System.nanoTime();

    System.out.println("Level of parallelism: " + parallelLevel);
    System.out.println("Sequential threshold: " + threshold);
    System.out.println("Elapsed time: " + (endTime - beginTime) + " ns");
    System.out.println();
  }

  private static class MyAction extends RecursiveAction {
    private int seqThreshold;
    private double[] data;
    private int start, end;

    public MyAction(double[] data, int start, int end, int seqThreshold) {
      this.data = data;
      this.start = start;
      this.end = end;
      this.seqThreshold = seqThreshold;
    }

    protected void compute() {
      if ((end - start) < seqThreshold) {
        for (int i = start; i < end; i++) {
          data[i] = Math.sqrt(data[i]);
        }
      } else {
        int middle = (start + end) / 2;
        invokeAll(new MyAction(data, start, middle, seqThreshold), new MyAction(data, middle, end, seqThreshold));
      }
    }
  }
}
```

To use the program, specify the level of parallelism followed by the threshold limit.  
You can obtain the level of parallelism by calling getParallelism(), which is defined by ForkJoinPool.  
It is shown here:

```
int getParallelism()
```

It returns the parallelism level currently in effect.  
For pools that you create, this value will equal the number of available processors.  
To obtain the parallelism level for the common pool, you can use getCommonPoolParallelism().  
Second, you can obtain the number of processors available in the system by calling availableProcessors(), which is
defined by the Runtime class.  
It is shown here:

```
int availableProcessors()
```

The value returned may change from one call to the next because of other system demands.

## 五.Locks

### 5.1 Lock

Introduction  
Lock implementations provide more extensive locking operations than can be obtained using synchronized methods
and statements.  
They allow more flexible structuring, may have quite different properties, and may support multiple associated 
Condition objects.  
A lock is a tool for controlling access to a shared resource by multiple threads.  
Commonly, a lock provides exclusive access to a shared resource: only one thread at a time can acquire the lock and all
access to the shared resource requires that the lock be acquired first.  
However, some locks may allow concurrent access to a shared resource, such as the read lock of a ReadWriteLock.  
The use of synchronized methods or statements provides access to the implicit monitor lock associated with every
object, but forces all lock acquisition and release to occur in a block-structured way:  
when multiple locks are acquired they must be released in the opposite order, and all locks must be released in the same
lexical scope in which they were acquired.  
While the scoping mechanism for synchronized methods and statements makes it much easier to program with monitor
locks, and helps avoid many common programming errors involving locks, there are occasions where you need to work with
locks in a more flexible way.  
For example, some algorithms for traversing concurrently accessed data structures require the use of "hand-over-hand"
or "chain locking":  
you acquire the lock of node A, then node B, then release A and acquire C, then release B and acquire D and so on.  
Implementations of the Lock interface enable the use of such techniques by allowing a lock to be acquired and
released in different scopes, and allowing multiple locks to be acquired and released in any order.  
With this increased flexibility comes additional responsibility.  
The absence of block-structured locking removes the automatic release of locks that occurs with synchronized methods
and statements.  
In most cases, the following idiom should be used:

```
(Lock l = ...;)
l.lock(); 
try { 
  // access the resource protected by this lock
  finally { 
    l.unlock(); 
  }
}
```

When locking and unlocking occur in different scopes, care must be taken to ensure that all code that is executed while
the lock is held is protected by try-finally or try-catch to ensure that the lock is released when necessary.  
Lock implementations provide additional functionality over the use of synchronized methods and statements by providing a
non-blocking attempt to acquire a lock (tryLock()), an attempt to acquire the lock that can be interrupted (
lockInterruptibly, and an attempt to acquire the lock that can timeout (tryLock(long, TimeUnit)).  
A Lock class can also provide behavior and semantics that is quite different from that of the implicit monitor lock,
such as guaranteed ordering, non-reentrant usage, or deadlock detection.  
If an implementation provides such specialized semantics then the implementation must document those semantics.  
Note that Lock instances are just normal objects and can themselves be used as the target in a synchronized
statement.  
Acquiring the monitor lock of a Lock instance has no specified relationship with invoking any of the #lock
methods of that instance.  
It is recommended that to avoid confusion you never use Lock instances in this way, except within their own
implementation.  
Except where noted, passing a null value for any parameter will result in a NullPointerException being thrown.

Memory Synchronization  
All Lock implementations must enforce the same memory synchronization semantics as provided by the built-in monitor
lock, as described in Chapter 17 of TheJavaLanguage Specification:

+ A successful lock operation has the same memory synchronization effects as a successful Lock action.
+ A successful unlock operation has the same memory synchronization effects as a successful Unlock action.

Unsuccessful locking and unlocking operations, and reentrant locking/unlocking operations, do not require any memory
synchronization effects.

Implementation Considerations  
The three forms of lock acquisition (interruptible, non-interruptible, and timed) may differ in their performance
characteristics, ordering guarantees, or other implementation qualities.  
Further, the ability to interrupt the ongoing acquisition of a lock may not be available in a given Lock class.  
Consequently, an implementation is not required to define exactly the same guarantees or semantics for all three forms
of lock acquisition, nor is it required to support interruption of an ongoing lock acquisition.  
An implementation is required to clearly document the semantics and guarantees provided by each of the locking
methods.  
It must also obey the interruption semantics as defined in this interface, to the extent that interruption of lock
acquisition is supported: which is either totally, or only on method entry.  
As interruption generally implies cancellation, and checks for interruption are often infrequent, an implementation can
favor responding to an interrupt over normal method return.  
This is true even if it can be shown that the interrupt occurred after another action may have unblocked the thread.  
An implementation should document this behavior.

Example  
The following code shows how to use Lock from java.util.concurrent.locks.

```java
package cn.tuyucheng.edu.locks;

import java.io.FileOutputStream;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.locks.Application
  @createTime: 2022-01-20 19:09
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws Exception {
    Lock lock = new ReentrantLock();
    Random random = new Random();
    lock.lock();
    int number = random.nextInt(5);
    int result = 100 / number;
    System.out.println("A result is " + result);
    String file = "D:java-codeidea-workspacejavastudy-tuyucheng-edusection01-javase-concurrentfiles1.txt";
    FileOutputStream fileOutputStream = new FileOutputStream(file);
    fileOutputStream.write(result);
    fileOutputStream.close();
  }
}
```

+ lock()  
  Introduction  
  Acquires the lock.  
  If the lock is not available then the current thread becomes disabled for thread scheduling purposes and lies dormant
  until the lock has been acquired.  
  Implementation Considerations A Lock implementation may be able to detect erroneous use of the lock, such as an
  invocation that would cause deadlock, and may throw an (unchecked) exception in such circumstances.  
  The circumstances and the exception type must be documented by that Lock implementation.  
  Syntax  
  The method lock() from Lock is declared as:
  ```
  void lock();
  ```

+ unlock()  
  Introduction  
  Releases the lock.  
  Implementation Considerations A Lock implementation will usually impose restrictions on which thread can release a
  lock (typically only the holder of the lock can release it) and may throw an (unchecked) exception if the restriction
  is violated.  
  Any restrictions and the exception type must be documented by that Lock implementation.  
  Syntax  
  The method unlock() from Lock is declared as:
  ```
  void unlock();
  ```

### 5.2 ReadWriteLock

Introduction  
A ReadWriteLock maintains a pair of associated (Lock locks), one for read-only operations and one for writing.  
The readLock may be held simultaneously by multiple reader threads, so long as there are no writers.  
The (plain writeLock write lock) is exclusive.  
All ReadWriteLock implementations must guarantee that the memory synchronization effects of writeLock
operations (as specified in the Lock interface) also hold with respect to the associated readLock.  
That is, a thread successfully acquiring the read lock will see all updates made upon previous release of the write
lock.  
A read-write lock allows for a greater level of concurrency in accessing shared data than that permitted by a mutual
exclusion lock.  
It exploits the fact that while only a single thread at a time (a writer thread) can modify the shared data, in many
cases any number of threads can concurrently read the data (hence reader threads).  
In theory, the increase in concurrency permitted by the use of a read-write lock will lead to performance improvements
over the use of a mutual exclusion lock.  
In practice this increase in concurrency will only be fully realized on a multi-processor, and then only if the access
patterns for the shared data are suitable.  
Whether or not a read-write lock will improve performance over the use of a mutual exclusion lock depends on the
frequency that the data is read compared to being modified, the duration of the read and write operations, and the
contention for the data - that is, the number of threads that will try to read or write the data at the same time.  
For example, a collection that is initially populated with data and thereafter infrequently modified, while being
frequently searched (such as a directory of some kind) is an ideal candidate for the use of a read-write lock.  
However, if updates become frequent then the data spends most of its time being exclusively locked and there is little,
if any increase in concurrency.  
Further, if the read operations are too short the overhead of the read-write lock implementation (which is inherently
more complex than a mutual exclusion lock) can dominate the execution cost, particularly as many read-write lock
implementations still serialize all threads through a small section of code.  
Ultimately, only profiling and measurement will establish whether the use of a read-write lock is suitable for your
application.  
Although the basic operation of a read-write lock is straight-forward, there are many policy decisions that an
implementation must make, which may affect the effectiveness of the read-write lock in a given application.  
Examples of these policies include:

+ Determining whether to grant the read lock or the write lock, when both readers and writers are waiting, at the time
  that a writer releases the write lock. Writer preference is common, as writes are expected to be short and infrequent.
  Reader preference is less common as it can lead to lengthy delays for a write if the readers are frequent and
  long-lived as expected. Fair, or "in-order" implementations are also possible.
+ Determining whether readers that request the read lock while a reader is active and a writer is waiting, are granted
  the read lock. Preference to the reader can delay the writer indefinitely, while preference to the writer can reduce
  the potential for concurrency.
+ Determining whether the locks are reentrant: can a thread with the write lock reacquire it? Can it acquire a read lock
  while holding the write lock? Is the read lock itself reentrant?
+ Can the write lock be downgraded to a read lock without allowing an intervening writer? Can a read lock be upgraded to
  a write lock, in preference to other waiting readers or writers?

You should consider all of these things when evaluating the suitability of a given implementation for your application.

Example  
The following code shows how to use ReadWriteLock from java.util.concurrent.locks.

```java
package cn.tuyucheng.edu.lock;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.locks.Application
  @createTime: 2022-01-20 20:01
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    Service service = new Service();
    for (int i = 0; i < 5; i++) {
      new Thread(service::write).start();
    }
  }

  private static class Service {
    ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public void write() {
      try {
        readWriteLock.writeLock().lock();//申请获得写锁
        System.out.println(Thread.currentThread() + "获得写锁,开始修改数据时间:" + System.currentTimeMillis());
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      } finally {
        System.out.println(Thread.currentThread().getName() + "修改数据完毕时的时间:" + System.currentTimeMillis());
        readWriteLock.writeLock().unlock();//释放写锁
      }
    }
  }
}
```

+ readLock()  
  Java ReadWriteLock readLock() Returns the lock used for reading.  
  Syntax  
  The method readLock() from ReadWriteLock is declared as:
  ```
  Lock readLock();
  ```
  Return  
  The method readLock() returns the lock used for reading


+ writeLock()  
  Java ReadWriteLock writeLock() Returns the lock used for writing.  
  Syntax  
  The method writeLock() from ReadWriteLock is declared as:
  ```
  Lock writeLock();
  ```
  Return  
  The method writeLock() returns the lock used for writing

### 5.3 ReentrantLock

Introduction  
A reentrant mutual exclusion Lock with the same basic behavior and semantics as the implicit monitor lock accessed
using synchronized methods and statements, but with extended capabilities.  
A ReentrantLock is owned by the thread last successfully locking, but not yet unlocking it.  
A thread invoking lock will return, successfully acquiring the lock, when the lock is not owned by another thread.  
The method will return immediately if the current thread already owns the lock.  
This can be checked using methods #isHeldByCurrentThread, and #getHoldCount.  
The constructor for this class accepts an optional fairness parameter.  
When set true, under contention, locks favor granting access to the longest-waiting thread.  
Otherwise this lock does not guarantee any particular access order.  
Programs using fair locks accessed by many threads may display lower overall throughput (i.e., are slower; often much
slower) than those using the default setting, but have smaller variances in times to obtain locks and guarantee lack of
starvation.  
Note however, that fairness of locks does not guarantee fairness of thread scheduling.  
Thus, one of many threads using a fair lock may obtain it multiple times in succession while other active threads are
not progressing and not currently holding the lock.  
Also note that the untimed #tryLock() method does not honor the fairness setting.  
It will succeed if the lock is available even if other threads are waiting.  
It is recommended practice to always immediately follow a call to lock with a try block, most typically in a
before/after construction  
Some of these methods are only useful for instrumentation and monitoring.  
Serialization of this class behaves in the same way as built-in locks: a deserialized lock is in the unlocked state,
regardless of its state when serialized.  
This lock supports a maximum of 2147483647 recursive locks by the same thread.  
Attempts to exceed this limit result in Error throws from locking methods.

Example  
The following code shows how to use ReentrantLock from java.util.concurrent.locks.

```java
package cn.tuyucheng.edu.lock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.locks.Application
  @createTime: 2022-01-20 20:01
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) throws InterruptedException {
    SubThread t1 = new SubThread();
    SubThread t2 = new SubThread();
    t1.start();
    t2.start();
    t1.join();
    t2.join();
    System.out.println(SubThread.num);
  }

  private static class SubThread extends Thread {
    private static Lock lock = new ReentrantLock();
    public static int num = 0;

    @Override
    public void run() {
      for (int i = 0; i < 10000; i++) {
        try {
          lock.lock();
          lock.lock();//可重入锁指可以反复获得该锁,同时也需要多次释放
          num++;
        } finally {
          lock.unlock();
          lock.unlock();
        }
      }
    }
  }

}
```

The java.util.concurrent.locks package provides support for locks.  
The locks control access to a shared resource.  
Before accessing a shared resource, the lock that protects that resource is acquired.  
When access to the resource is complete, the lock is released.  
If a second thread attempts to acquire the lock when it is in use by another thread, the second thread will suspend
until the lock is released.  
Therefore, conflicting access to a shared resource is prevented.

Lock interface  
The Lock interface defines a lock.  
The methods defined by Lock are shown in the following table.

|  Return  | Description                                                                                                                                                                                                                                                                                                                         |
|  ----  |-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| void  | lock() -- Waits until the invoking lock can be acquired.                                                                                                                                                                                                                                                                            |
| void  | lockInterruptibly() throws InterruptedException -- Waits until the invoking lock can be acquired, unless interrupted.                                                                                                                                                                                                               |
| Condition| newCondition() -- Returns a Condition object that is associated with the invoking lock.                                                                                                                                                                                                                                             |
|  boolean     | tryLock() -- Attempts to acquire the lock.This method will not wait if the lock is unavailable.Instead, it returns true if the lock has been acquired and false if the lock is currently in use by another thread.                                                                                                                  |
| boolean      | tryLock(long wait, TimeUnit tu) throws InterruptedException -- Attempts to acquire the lock.If the lock is unavailable, this method will wait no longer than the period specified by wait, which is in tu units.It returns true if the lock has been acquired and false if the lock cannot be acquired within the specified period. |
|  void     | unlock() -- Releases the lock.                                                                                                                                                                                                                                                                                                      |

Note  
In general, to acquire a lock, call lock().  
If the lock is unavailable, lock() will wait.  
To release a lock, call unlock().  
To see if a lock is available, and to acquire it if it is, call tryLock().  
This method will not wait for the lock if it is unavailable, it returns true if the lock is acquired and false
otherwise.  
The newCondition() method returns a Condition object associated with the lock.  
Using a Condition, you gain detailed control of the lock through methods such as await() and signal(), which
provide functionality similar to Object.wait() and Object.notify().  
java.util.concurrent.locks supplies an implementation of Lock called ReentrantLock.  
ReentrantLock implements a reentrant lock, which is a lock that can be repeatedly entered by the thread that
currently holds the lock.  
In the case of a thread reentering a lock, all calls to lock() must be offset by an equal number of calls to 
unlock().  
Otherwise, a thread seeking to acquire the lock will suspend until the lock is not in use.

Example  
The following program demonstrates the use of a lock.  
It creates two threads that access a shared resource called Shared.count.  
Before a thread can access Shared.count, it must obtain a lock.  
After obtaining the lock, Shared.count is incremented and then, before releasing the lock, the thread sleeps.  
This causes the second thread to attempt to obtain the lock.  
However, because the lock is still held by the first thread, the second thread must wait until the first thread stops
sleeping and releases the lock.  
The output shows that access to Shared.count is, indeed, synchronized by the lock.

```java
package cn.tuyucheng.edu.classes;

import java.util.concurrent.locks.ReentrantLock;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.locks.Application
  @createTime: 2021/12/30-23:33
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    ReentrantLock lock = new ReentrantLock();
    new LockThread(lock, "A");
    new LockThread(lock, "B");
  }

  private static class LockThread implements Runnable {
    private String name;
    private ReentrantLock lock;

    public LockThread(ReentrantLock lock, String name) {
      this.lock = lock;
      this.name = name;
      new Thread(this).start();
    }

    public void run() {
      System.out.println("Starting " + name);
      try {
        // First, lock count.
        System.out.println(name + " is waiting to lock count.");
        lock.lock();
        System.out.println(name + " is locking count.");
        MySharedResource.count++;
        System.out.println(name + ": " + MySharedResource.count);
        Thread.sleep(1000);
        // Now, allow a context switch -- if possible.
        System.out.println(name + " is sleeping.");
        Thread.sleep(1000);
      } catch (InterruptedException exc) {
        exc.printStackTrace();
      } finally {
        System.out.println(name + " is unlocking count."); // Unlock
        lock.unlock();
      }
    }
  }

  private static class SharedResource {
    private static int count = 0;
  }
}
```

+ ReentrantLock()  
  Introduction  
  Creates an instance of ReentrantLock.  
  This is equivalent to using ReentrantLock(false).  
  The method ReentrantLock() is a constructor.  
  Syntax  
  The method ReentrantLock() from ReentrantLock is declared as:
  ```
  public ReentrantLock()
  ```

+ ReentrantLock(boolean fair)  
  Java ReentrantLock ReentrantLock(boolean fair) Creates an instance of ReentrantLock with the given fairness
  policy.  
  The method ReentrantLock() is a constructor.  
  Syntax  
  The method ReentrantLock() from ReentrantLock is declared as:
  ```
  public ReentrantLock(boolean fair)
  ```
  Parameter  
  The method ReentrantLock() has the following parameter:
    + boolean fair - true if this lock should use a fair ordering policy

### 5.4 ReentrantReadWriteLock

Introduction  
An implementation of ReadWriteLock supporting similar semantics to ReentrantLock.  
This class has the following properties:

Acquisition order  
This class does not impose a reader or writer preference ordering for lock access.  
However, it does support an optional fairness policy.

Non-fair mode (default)  
When constructed as non-fair (the default), the order of entry to the read and write lock is unspecified, subject to
reentrancy constraints.  
A nonfair lock that is continuously contended may indefinitely postpone one or more reader or writer threads, but will
normally have higher throughput than a fair lock.

Fair mode  
When constructed as fair, threads contend for entry using an approximately arrival-order policy.  
When the currently held lock is released, either the longest-waiting single writer thread will be assigned the write
lock, or if there is a group of reader threads waiting longer than all waiting writer threads, that group will be
assigned the read lock.  
A thread that tries to acquire a fair read lock (non-reentrantly) will block if either the write lock is held, or there
is a waiting writer thread.  
The thread will not acquire the read lock until after the oldest currently waiting writer thread has acquired and
released the write lock.  
Of course, if a waiting writer abandons its wait, leaving one or more reader threads as the longest waiters in the queue
with the write lock free, then those readers will be assigned the read lock.  
A thread that tries to acquire a fair write lock (non-reentrantly) will block unless both the read lock and write lock
are free (which implies there are no waiting threads).  
(Note that the non-blocking ReadLock#tryLock() and WriteLock#tryLock() methods do not honor this fair setting and
will immediately acquire the lock if it is possible, regardless of waiting threads.)

Reentrancy  
This lock allows both readers and writers to reacquire read or write locks in the style of a ReentrantLock.  
Non-reentrant readers are not allowed until all write locks held by the writing thread have been released.  
Additionally, a writer can acquire the read lock, but not vice-versa.  
Among other applications, reentrancy can be useful when write locks are held during calls or callbacks to methods that
perform reads under read locks.  
If a reader tries to acquire the write lock it will never succeed.

Lock downgrading  
Reentrancy also allows downgrading from the write lock to a read lock, by acquiring the write lock, then the read lock
and then releasing the write lock.  
However, upgrading from a read lock to the write lock is not possible.

Interruption of lock acquisition  
The read lock and write lock both support interruption during lock acquisition.

Condition support  
The write lock provides a Condition implementation that behaves in the same way, with respect to the write lock, as
the Condition implementation provided by ReentrantLock#newCondition does for ReentrantLock.  
This Condition can, of course, only be used with the write lock.  
The read lock does not support a Condition and readLock().newCondition() throws 
UnsupportedOperationException.

Instrumentation  
This class supports methods to determine whether locks are held or contended.  
These methods are designed for monitoring system state, not for synchronization control.  
Serialization of this class behaves in the same way as built-in locks: a deserialized lock is in the unlocked state,
regardless of its state when serialized.

Example  
The following code shows how to use ReentrantReadWriteLock from java.util.concurrent.locks.

```java
package cn.tuyucheng.edu.locks;

import org.jetbrains.annotations.Contract;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.locks.Application
  @createTime: 2022-01-20 20:53
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    SharedResource shared = new SharedResource();
    Thread[] threadsReader = new Thread[5];

    for (int i = 1; i <= 5; i++) { // Creates five readers and threads to run them
      threadsReader[i] = new Thread(new Reader(shared));
    }
    Thread threadWriter = new Thread(new Writer(shared));  // Creates a writer and a thread to run it
    for (int i = 0; i <= 5; i++) {
      threadsReader[i].start();
    }
    threadWriter.start();
  }

  private static class Reader implements Runnable {
    private SharedResource shared;

    @Contract(pure = true)
    public Reader(SharedResource pricesInfo) {
      this.shared = pricesInfo;
    }

    @Override
    public void run() {
      for (int i = 1; i <= 10; i++) {
        System.out.printf("%s: V1: %fn", Thread.currentThread().getName(), shared.getV1());
        System.out.printf("%s: V2: %fn", Thread.currentThread().getName(), shared.getV2());
      }
    }
  }

  private static class Writer implements Runnable {
    private SharedResource shared;

    @Contract(pure = true)
    public Writer(SharedResource s) {
      this.shared = s;
    }

    @Override
    public void run() {
      for (int i = 1; i <= 3; i++) {
        System.out.print("Writer: Attempt to modify the prices.n");
        shared.setPrices(Math.random()  10, Math.random()  8);
        System.out.print("Writer: Prices have been modified.n");
        try {
          Thread.sleep(2);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private static class SharedResource {
    private double v1;
    private double v2;

    private ReadWriteLock lock;

    public SharedResource() {
      v1 = 1.0;
      v2 = 2.0;
      lock = new ReentrantReadWriteLock();
    }

    public double getV1() {
      lock.readLock().lock();
      double value = v1;
      lock.readLock().unlock();
      return value;
    }

    public double getV2() {
      lock.readLock().lock();
      double value = v2;
      lock.readLock().unlock();
      return value;
    }

    public void setPrices(double price1, double price2) {
      lock.writeLock().lock();
      this.v1 = price1;
      this.v2 = price2;
      lock.writeLock().unlock();
    }
  }
}
```

+ ReentrantReadWriteLock()  
  Java ReentrantReadWriteLock ReentrantReadWriteLock() Creates a new ReentrantReadWriteLock with default (
  nonfair) ordering properties.  
  The method ReentrantReadWriteLock() is a constructor.  
  Syntax  
  The method ReentrantReadWriteLock() from ReentrantReadWriteLock is declared as:
  ```
  public ReentrantReadWriteLock()
  ```

## 六.AtomicOperation

### 6.1 AtomicBoolean

+ compareAndSet()  
  The java.util.concurrent.atomic.AtomicBoolean.compareAndSet() sets the value.  
  The function returns a boolean value which gives us an idea if the update was done or not.  
  Syntax
  ```
  public final boolean compareAndSet(boolean expect, boolean val)
  ```
  Parameter  
  The function accepts two mandatory parameters which are described below:
    + expect: the value that the atomic object should be.
    + val: the value to be updated if the atomic Boolean is equal to expect.

  Return  
  The function returns a boolean value, it returns true on success else false.  
  Below programs illustrate the above function:

```java
package cn.tuyucheng.edu.atomic;

import java.util.concurrent.atomic.AtomicBoolean;

/
  @author: tuyucheng(涂余成)
  @className: cn.tuyucheng.edu.atomic.Application
  @createTime: 2022-01-20 21:47
  @description: code is poem in my heart(代码是我心中的一首诗)
 /
public class Application {
  public static void main(String[] args) {
    AtomicBoolean value = new AtomicBoolean(false);
    System.out.println("Previous value: " + value);
    // check if previous value was false and then update it.
    boolean result = value.compareAndSet(false, true);
    // check if the value was updated.
    if (result)
      System.out.println("The value was" + " update and it is " + value);
    else
      System.out.println("The value was " + "not updated");
  }
}
```

+ get()  
  The java.util.concurrent.atomic.AtomicBoolean.get() returns the current value which is of date-type boolean.  
  Syntax
  ```
   public final boolean get()  
  ```
  Return  
  The function returns the current value  
  Below programs illustrate the above function:  
  Java Program to demonstrates the get() function

```java
public class Application {
  public static void main(String[] args) {
    AtomicBoolean val = new AtomicBoolean(false);
    // Gets the current value
    boolean result = val.get();
    System.out.println("current value: " + result);
  }
}
```

+ getAndSet(boolean val)  
  The Java.util.concurrent.atomic.AtomicBoolean.getAndSet() sets the given value and returns the value.  
  Syntax
  ```
  public final boolean getAndSet(boolean val)
  ```
  Parameter  
  specifies the value to be updated.  
  Return  
  returns the value before update operation is performed to the previous value.  
  Below programs illustrate the above method:  
  Java program that demonstrates the getAndSet() function

```java
public class Application {
  public static void main(String[] args) {
    AtomicBoolean value = new AtomicBoolean(false);
    // Updates and sets
    boolean result = val.getAndSet(true);
    System.out.println("Previous value: " + result);
    System.out.println("Current value: " + value);
  }
}
```

+ lazySet(boolean new_value)  
  The java.util.concurrent.atomic.AtomicBoolean.lazySet() sets a new value.  
  Syntax
  ```
  public final void lazySet(boolean new_value)
  ```
  Parameter
    + new_value - to be updated.

  Return  
  The function does not returns anything.  
  Below programs illustrate the above function:

```java
import java.util.concurrent.atomic.AtomicBoolean;

public class Application {
  public static void main(String[] args) {
    AtomicBoolean value = new AtomicBoolean(false);
    System.out.println("Previous value: " + value);
    value.lazySet(true);
    System.out.println("Current value: " + value);
  }
}
```

+ set(boolean new_value)  
  The java.util.concurrent.atomic.AtomicBoolean.set() sets the previous value and sets it to a new value which is
  passed in the parameter.  
  Syntax
  ```
  public final void set(boolean new_value)
  ```
  Parameter  
  The function accepts a single mandatory parameter new_value which is to be updated.  
  Return  
  The function does not returns anything.  
  Below programs illustrate the above function:

```java
public class Application {
  public static void main(String[] args) {
    AtomicBoolean val = new AtomicBoolean(false);
    System.out.println("Previous value: " + val);
    val.set(true);
    System.out.println("Current value: " + val);
  }
}
```

### 6.2 AtomicInteger  
