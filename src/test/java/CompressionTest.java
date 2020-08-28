import com.conimon.Compression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompressionTest {
    private Compression compression;

    @BeforeAll
    void setUp() {
        List<Integer> ints = Arrays.asList(10, 20, 30 ,40, 50, 66, 70, 80, 99);
        List<Long> longs = ints.stream()
                .mapToLong(Integer::longValue)
                .boxed().collect(Collectors.toList());
        compression = new Compression(longs);
    }

    @Test
    void computeDifferenceListTest() {
        List<Integer> ints = Arrays.asList(10, 10, 10 ,10, 16, 4, 10, 19);
        List<Long> longs = ints.stream()
                .mapToLong(Integer::longValue)
                .boxed().collect(Collectors.toList());

        List<Long> differences = compression.computeDifferenceList(1);
        assertEquals(longs, differences);
    }
}
