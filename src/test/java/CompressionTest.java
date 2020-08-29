import com.conimon.BitSets;
import com.conimon.Compression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompressionTest {
    private Compression compression;
    List<Long> timestamps;

    @BeforeAll
    void setUp() {
        List<Integer> ints = Arrays.asList(1, 10, 20, 30 ,40, 50, 66, 70, 80, 99);
        timestamps =  CompressionTest.toLong(ints);
        compression = new Compression();
    }

    static List<Long> toLong(List<Integer> ints) {
        return ints.stream()
                .mapToLong(Integer::longValue)
                .boxed().collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("provideDifferenceDegree")
    void computeDifferenceListTest(int differenceDegree, List<Long> expected) {
        List<Long> differences = compression.computeDifferenceList(timestamps, differenceDegree);
        assertEquals(expected, differences);
    }

    private static Stream<Arguments> provideDifferenceDegree() {
        return Stream.of(
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(1, 9, 10, 10, 10 ,10, 16, 4, 10, 19))),
                Arguments.of(2, CompressionTest.toLong(Arrays.asList(1, 10, 1,  0,  0 , 0,  6,-12, 6, 9)))
        );
    }

    /*
    @Test
    void toByteListTest() {
        List<byte[]> byteList = compression.toByteList(timestamps);
        byteList.forEach(BitSets::print);
    }

    @Test
    void toByteArrayTest() {
        byte[] expected = {1, 10, 1, 20, 1, 30, 1, 40, 1, 50, 1, 66, 1, 70, 1, 80, 1, 99};
        byte[] byteArray = compression.toByteArray(timestamps);
        BitSets.print(byteArray);
        assertTrue(Arrays.equals(expected, byteArray));
    }

     */

    @ParameterizedTest
    @MethodSource("provideTimestamps")
    void toBitSetsTest(List<Long> timestamps) {
        List<BitSets> bitSetsList = compression.toBitSets(timestamps);
        bitSetsList.forEach(item -> System.out.println(item.toString()));
    }

    private static Stream<Arguments> provideTimestamps() {
        return Stream.of(
                Arguments.of(CompressionTest.toLong(Arrays.asList(1, 10, 20, 30 ,40, 50, 66, 70, 80, 99))),
                Arguments.of(CompressionTest.toLong(Arrays.asList(0, 7, -2, -5)))
        );
    }
}
