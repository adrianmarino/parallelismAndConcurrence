
import org.junit.Test;

import java.util.List;
import java.util.concurrent.*;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class ExecutorServiceTest {

    @Test
    public void testExecuteUsingARunnableBlock() throws InterruptedException {
        // Prepare
        List<String> threadNames = newArrayList();
        Runnable lambda = () -> threadNames.add(Thread.currentThread().getName());
        ExecutorService service = Executors.newSingleThreadExecutor();

        // Perform
        service.execute(lambda);

        // Asserts
        service.awaitTermination(1, SECONDS);
        assertThat(threadNames, hasItem("pool-3-thread-1"));
    }

    @Test
    public void testSubmitUsingACallableBlock() throws ExecutionException, InterruptedException {
        // Prepare
        Callable<String> lambda = () -> Thread.currentThread().getName();
        ExecutorService service = Executors.newSingleThreadExecutor();

        // Perform
        Future<String> threadName = service.submit(lambda);

        // Asserts
        assertThat(threadName.get(), is(equalTo("pool-7-thread-1")));
    }

    @Test(expected = CancellationException.class)
    public void testCancelAndInterruptCallableWhileRun() throws ExecutionException, InterruptedException {
        // Prepare
        final boolean mayInterruptIfRunning = true;
        final Boolean[] wasInterrupted = {false};
        Callable<Integer> lambda = () -> {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                wasInterrupted[0] = true;
            }
            return 1;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(lambda);

        // Perform
        Thread.sleep(50L);
        future.cancel(mayInterruptIfRunning);

        // Asserts
        Thread.sleep(150L);
        assertThat(wasInterrupted[0], is(equalTo(true)));
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void testCancelCallableWhileRun() throws ExecutionException, InterruptedException {
        // Prepare
        final boolean mayInterruptIfRunning = false;
        final Boolean[] wasInterrupted = {false};
        Callable<Integer> lambda = () -> {
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                wasInterrupted[0] = true;
            }
            return 1;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(lambda);
        Thread.sleep(50L);

        // Perform
        future.cancel(mayInterruptIfRunning);

        // Asserts
        assertThat(future.isCancelled(), is(equalTo(true)));
        Thread.sleep(100L);
        assertThat(wasInterrupted[0], is(equalTo(false)));
        future.get();
    }

    @Test(expected = CancellationException.class)
    public void testCancelCallableBeforeRun() throws ExecutionException, InterruptedException {
        // Prepare
        final boolean mayInterruptIfRunning = false;
        Callable<Integer> lambda = () -> {
            Thread.sleep(9999999999L);
            return 1;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(lambda);

        // Perform
        future.cancel(mayInterruptIfRunning);

        // Asserts
        assertThat(future.isCancelled(), is(equalTo(true)));
        future.get();
    }

    @Test
    public void testCancelCallableAfterRun() throws ExecutionException, InterruptedException {
        // Prepare
        Callable<Integer> lambda = () -> {
            Thread.sleep(50L);
            return 1;
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        Future<Integer> future = service.submit(lambda);
        Thread.sleep(100L);
        final boolean mayInterruptIfRunning = false;

        // Perform
        future.cancel(mayInterruptIfRunning);

        // Asserts
        assertThat(future.isCancelled(), is(equalTo(false)));
        assertThat(future.get(), is(equalTo(1)));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvokeAll() throws InterruptedException, ExecutionException {
        // Prepare
        Callable<String> lambda = () -> Thread.currentThread().getName();
        List<Callable<String>> invocations = newArrayList(lambda, lambda);
        Function<Future<String>, String> getName = future -> {
            try {
                return future.get();
            } catch (Exception exception) {
                fail(exception.getMessage());
                return "";
            }
        };
        ExecutorService service = Executors.newFixedThreadPool(2);

        // Perform
        List<String> threadNames = service.invokeAll(invocations).stream()
                .map(getName)
                .collect(toList());

        // Asserts
        assertThat(threadNames, hasItem("pool-1-thread-1"));
        assertThat(threadNames, hasItem("pool-1-thread-2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testInvokeAny() throws ExecutionException, InterruptedException {
        // Prepare
        Callable<String> lambda = () -> Thread.currentThread().getName();
        List<Callable<String>> invocations = newArrayList(lambda, lambda);
        ExecutorService service = Executors.newFixedThreadPool(2);

        // Perform
        String threadName = service.invokeAny(invocations);

        // Asserts
        assertThat(newArrayList("pool-2-thread-1", "pool-2-thread-2"), hasItem(threadName));
    }
}
