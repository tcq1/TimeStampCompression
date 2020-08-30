import com.conimon.Decompression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DecompressionTest {
    private List<Long> timestamps;
    private List<Long> difference1;
    private List<Long> difference2;

    @BeforeAll
    public void setUp() {
        timestamps = CompressionTest.toLong(Arrays.asList(0, 10, 21, 30 ,40, 50, 66, 70, 80, 99));
        difference1 = CompressionTest.toLong(Arrays.asList(0, 10, 11, 9, 10, 10, 16, 4, 10, 19));
        difference2 = CompressionTest.toLong(Arrays.asList(0, 10, 1, -2, 1, 0, 6, -12, 6, 9));
    }

    @Test
    public void testCalculateTimestamps() {
        List<Long> actual1 = Decompression.calculateTimestamps(difference1, 1, 1);
        List<Long> actual2 = Decompression.calculateTimestamps(difference2, 2, 2);

        assertEquals(timestamps, actual1);
        assertEquals(timestamps, actual2);
    }

    @Test
    public void testCalculateNextDifferences() {
        assertEquals(timestamps, Decompression.calculateNextDifferences(difference1, 1));
        assertEquals(difference1, Decompression.calculateNextDifferences(difference2, 2));
    }
}
