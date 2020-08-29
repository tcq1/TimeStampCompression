import com.conimon.BitSets;
import com.conimon.Compression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CompressionTest {
    private Compression compression;
    List<Long> timestamps;

    @BeforeAll
    void setUp() {
        List<Integer> ints = Arrays.asList(1, 10, 20, 30 ,40, 50, 66, 70, 80, 99);
        timestamps =  CompressionTest.toLong(ints);
    }

    static List<Long> toLong(List<Integer> ints) {
        return ints.stream()
                .mapToLong(Integer::longValue)
                .boxed().collect(Collectors.toList());
    }

    @ParameterizedTest
    @MethodSource("provideDifferenceDegree")
    void computeDifferenceListTest(int differenceDegree, List<Long> expected) {
        List<Long> differences = Compression.computeDifferenceList(timestamps, differenceDegree);
        assertEquals(expected, differences);
    }

    private static Stream<Arguments> provideDifferenceDegree() {
        return Stream.of(
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(1, 9, 10, 10, 10 ,10, 16, 4, 10, 19))),
                Arguments.of(2, CompressionTest.toLong(Arrays.asList(1, 10, 1,  0,  0 , 0,  6,-12, 6, 9)))
        );
    }

    @ParameterizedTest
    @MethodSource("provideTimestamps")
    void concatenateTest(int differenceDegree, List<Long> timestamps) {
        List<BitSets> bitSetsList = Compression.toBitSets(timestamps, differenceDegree);
        CompressionTest.print(bitSetsList);

        BitSet bs = Compression.concatenate(bitSetsList);
        System.out.println(BitSets.toString(bs));
    }

    @ParameterizedTest
    @MethodSource("provideTimestamps")
    void toBitSetsTest(int differenceDegree, List<Long> timestamps) {
        List<BitSets> bitSetsList = Compression.toBitSets(timestamps, differenceDegree);
        CompressionTest.print(bitSetsList);
    }

    @ParameterizedTest
    @MethodSource("provideTimestamps")
    void compressTest(int differenceDegree, List<Long> timestamps) {
        BitSet bs = Compression.compress(timestamps, differenceDegree);
        System.out.println(BitSets.toString(bs));
    }

    private static Stream<Arguments> provideTimestamps() {
        return Stream.of(
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(1, 2, 3))),
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(1, 10, 20, 30 ,40, 50, 66, 70, 80, 99))),
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(0, 7, -2, -5)))
        );
    }

    private static void print(List<BitSets> bitSetsList) {
        bitSetsList.forEach(item -> System.out.println("length: " + item.length + "  bits: " + item.toString()));
    }
}