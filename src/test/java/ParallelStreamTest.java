import com.google.common.base.Stopwatch;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static com.nonosoft.ForkJoinUtils.submit;
import static java.lang.Math.sqrt;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.stream.IntStream.range;
import static java.util.stream.IntStream.rangeClosed;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ParallelStreamTest {

    @Test
    public void testCountPrimesWithSequentialStream() {
        // Prepare
        final Integer MAX = 10000000;
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Perform
        long count = range(1, MAX).asLongStream().filter(this::isPrime).count();

        // Asserts
        assertThat(count, is(equalTo(664579L)));
        stopwatch.stop();
        assertThat(stopwatch.elapsed(MILLISECONDS), is(greaterThan(15000L)));
        System.out.printf("Sequential stream time: %s\n", stopwatch);
    }

    @Test
    public void testCountPrimesWithParallelStream() {
        // Prepare
        final Integer MAX = 10000000;
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Perform
        long count = range(1, MAX).parallel().filter(this::isPrime).count();

        // Asserts
        assertThat(count, is(equalTo(664579L)));
        stopwatch.stop();
        assertThat(stopwatch.elapsed(MILLISECONDS), is(lessThan(15000L)));
        System.out.printf("Parallel stream time: %s\n", stopwatch);
    }

    @Test
    public void testCountPrimesWithParallelStreamFromNewForkJoinPool() throws ExecutionException, InterruptedException {
        // Prepare
        final Integer MAX = 10000000, POOL_SIZE = 10;
        Stopwatch stopwatch = Stopwatch.createStarted();
        ForkJoinPool pool = new ForkJoinPool(POOL_SIZE);

        // Perform
        long count = pool.submit(() -> range(1, MAX).parallel().filter(this::isPrime).count()).get();

        // Asserts
        assertThat(count, is(equalTo(664579L)));
        assertThat(pool.getParallelism(), is(equalTo(POOL_SIZE)));
        assertThat(ForkJoinPool.commonPool().getParallelism(), is(equalTo(3)));
        stopwatch.stop();
        assertThat(stopwatch.elapsed(MILLISECONDS), is(lessThan(15000L)));
        System.out.printf("Parallel stream from new pool(%s) time: %s\n", pool.getParallelism(), stopwatch);
    }

    @Test
    public void testCountPrimesWithParallelStreamUsingForkJoinUtils() throws ExecutionException, InterruptedException {
        // Prepare
        final Integer MAX = 10000000, POOL_SIZE = 10;
        Stopwatch stopwatch = Stopwatch.createStarted();

        // Perform
        long count = submit(POOL_SIZE, () -> range(1, MAX).parallel().filter(this::isPrime).count());

        // Asserts
        assertThat(count, is(equalTo(664579L)));
        stopwatch.stop();
        assertThat(stopwatch.elapsed(MILLISECONDS), is(lessThan(15000L)));
        System.out.printf("Parallel stream from new pool(%s) time: %s\n", POOL_SIZE, stopwatch);
    }

    private boolean isPrime(long n) {
        return n > 1 && rangeClosed(2, (int) sqrt(n)).noneMatch(divisor -> n % divisor == 0);
    }
}
