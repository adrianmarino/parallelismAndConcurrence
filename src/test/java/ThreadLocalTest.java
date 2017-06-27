
import com.google.common.collect.Lists;
import org.junit.Test;

import java.text.NumberFormat;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.concurrent.Executors.newFixedThreadPool;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.fail;

public class ThreadLocalTest {

    @Test
    public void testFormantNumbersUsingExecutors() {
        // Prepare
        List<Double> totals = newArrayList(1500.15D, 2500.15D, 3500.15D);
        ExecutorService service = newFixedThreadPool(3);
        ThreadLocal<NumberFormat> formatter = ThreadLocal.withInitial(NumberFormat::getCurrencyInstance);
        Function<Future<String>, String> getValue = future -> {
            try {
                return future.get();
            } catch (Exception exception) {
                fail(exception.getMessage());
                return null;
            }
        };

        // Perform
        List<String> stringTotals = totals
                .stream()
                .map(total -> service.submit(() -> formatter.get().format(total)))
                .map(getValue)
                .collect(toList());

        // Asserts
        assertThat(stringTotals, hasItem("$1,500.15"));
        assertThat(stringTotals, hasItem("$2,500.15"));
        assertThat(stringTotals, hasItem("$3,500.15"));
    }

    @Test
    public void testFormantNumbersUsingParallelStream() {
        // Prepare
        List<Double> totals = newArrayList(1500.15D, 2500.15D, 3500.15D);
        ThreadLocal<NumberFormat> formatter = ThreadLocal.withInitial(NumberFormat::getCurrencyInstance);

        // Perform
        List<String> stringTotals = totals
                .parallelStream()
                .map(total -> formatter.get().format(total))
                .collect(toList());

        // Asserts
        assertThat(stringTotals, hasItem("$1,500.15"));
        assertThat(stringTotals, hasItem("$2,500.15"));
        assertThat(stringTotals, hasItem("$3,500.15"));
    }
}
