import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class ConcurrentHashMapAtomicMethodsTest {

    @Test
    public void incrementKeyValueUsingCompute() {
        // Perform
        map.compute(KEY, (key, value) -> value == null ? 1 : value + 1);

        // Asserts
        assertThat(map.get(KEY), is(equalTo(1)));
    }

    @Test
    public void incrementKeyValueUsingMerge() {
        // Perform
        map.merge(KEY, 1, (currentValue, defaultValue) -> currentValue + defaultValue);
        map.merge(KEY, 1, (currentValue, defaultValue) -> currentValue + defaultValue);

        // Asserts
        assertThat(map.get(KEY), is(equalTo(2)));
    }


    @Test
    public void incrementKeyValueUsingIfAbsentAndIfPresent() {
        // Perform
        map.computeIfAbsent(KEY, key -> {
            return 0;
        });
        map.computeIfPresent(KEY, (key, value) -> {
            return value + 1;
        });

        // Asserts
        assertThat(map.get(KEY), is(equalTo(1)));
    }

    @Test
    public void incrementKeyValueUsingPutIfAbsentAndIfPresent() {
        // Perform
        map.putIfAbsent(KEY, 0);
        map.computeIfPresent(KEY, (key, value) -> {
            return value + 1;
        });

        // Asserts
        assertThat(map.get(KEY), is(equalTo(1)));
    }

    @Before
    public void setUp() {
        map = new ConcurrentHashMap<>();
    }

    //-----------------------------------------------------------------------------
    // Constants
    //-----------------------------------------------------------------------------

    private static final String KEY = "key";

    //-----------------------------------------------------------------------------
    // Attributes
    //-----------------------------------------------------------------------------

    private Map<String, Integer> map;
}
