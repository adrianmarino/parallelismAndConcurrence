import com.nonosoft.Queue;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class QueueTest {

    @Test
    public void testPopInOrder() throws InterruptedException {
        // Perform
        queue.push(1, 2, 3, 4);

        // Asserts
        assertThat(queue.pop(), is(equalTo(1)));
        assertThat(queue.pop(), is(equalTo(2)));
        assertThat(queue.pop(), is(equalTo(3)));
        assertThat(queue.pop(), is(equalTo(4)));
    }

    @Test(timeout = 2000L)
    public void testAsyncPush() throws InterruptedException {
        // Prepare
        Integer value = 1;
        Thread asyncPush = new Thread(() -> {
            sleep(1000L);
            queue.push(value);
        });

        // Perform
        asyncPush.start();

        // Asserts
        assertThat(queue.pop(), is(equalTo(value)));
    }

    @Before
    public void setUp() {
        queue = new Queue<>();
    }

    //-----------------------------------------------------------------------------
    // Private methods
    //-----------------------------------------------------------------------------

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //-----------------------------------------------------------------------------
    // Attributes
    //-----------------------------------------------------------------------------

    private Queue<Integer> queue;
}
