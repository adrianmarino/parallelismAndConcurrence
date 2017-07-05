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

    @Test(timeout = 2500L)
    public void testAsyncPush() throws InterruptedException {
        // Prepare
        Thread asyncPush = new Thread(() -> { sleep(1000L); queue.push(1);
            sleep(1000L); queue.push(2); });

        // Perform
        asyncPush.start();

        // Asserts
        assertThat(queue.pop(), is(equalTo(1)));
        assertThat(queue.pop(), is(equalTo(2)));
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
