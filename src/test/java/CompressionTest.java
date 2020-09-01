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
    List<Long> timestamps;

    @BeforeAll
    void setUp() {
        List<Integer> ints = Arrays.asList(1, 10, 20, 30 ,40, 50, 66, 70, 80, 99);
        timestamps =  CompressionTest.toLong(ints);
    }

    @ParameterizedTest
    @MethodSource("provideDifferenceDegree")
    void testComputeDifferenceList(int differenceDegree, List<Long> expected) {
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
    @MethodSource("provideNumberList")
    void testConcatenate(int numberOfNotTruncated, List<Long> numberList, String expected) {
        List<BitSets> bitSetsList = Compression.toBitSets(numberList, numberOfNotTruncated);
        CompressionTest.print(bitSetsList);

        BitSet bs = Compression.concatenate(bitSetsList);
        assertEquals(expected, BitSets.toString(bs));
    }

    private static Stream<Arguments> provideNumberList() {
        return Stream.of(
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(1, 0, -6, -1, 57)),
                        "0000000000000000000000000000000000000000000000000000000000000001" +
                                "0000010000100101000000110001110111001" + "000000000000000000000000000"),
                Arguments.of(2, CompressionTest.toLong(Arrays.asList(1, 0, -6, -1, 57)),
                        "0000000000000000000000000000000000000000000000000000000000000001" +
                                "0000000000000000000000000000000000000000000000000000000000000000" +
                                "000100101000000110001110111001" + "0000000000000000000000000000000000"),
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(-3, 7, 0, -5)),
                        "1111111111111111111111111111111111111111111111111111111111111101" +
                                "000100011100000100001001011" + "0000000000000000000000000000000000000"),
                Arguments.of(2, CompressionTest.toLong(Arrays.asList(-3, 7, 0, -5)),
                        "1111111111111111111111111111111111111111111111111111111111111101" +
                                "0000000000000000000000000000000000000000000000000000000000000111" +
                                "00000100001001011" + "00000000000000000000000000000000000000000000000")
        );
    }

    @ParameterizedTest
    @MethodSource("provideTimestamps")
    void testCompress(int differenceDegree, List<Long> timestamps, String expected) {
        BitSet bs = Compression.compress(timestamps, differenceDegree);
        assertEquals(expected, BitSets.toString(bs));
    }

    private static Stream<Arguments> provideTimestamps() {
        return Stream.of(
                // 1, 0, -6, -1, 57
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(1, 1, -5, -6, 51)),
                        "0000000000000000000000000000000000000000000000000000000000000001" +
                                "0000010000100101000000110001110111001" + "000000000000000000000000000"),
                // 1, 0, -6, -1, 57
                // 1,-1, -7, -8, 49
                Arguments.of(2, CompressionTest.toLong(Arrays.asList(1, 0, -7, -15, 34)),
                        "0000000000000000000000000000000000000000000000000000000000000001" +
                                "0000000000000000000000000000000000000000000000000000000000000000" +
                                "000100101000000110001110111001" + "0000000000000000000000000000000000"),
                // -3, 7, 0, -5
                Arguments.of(1, CompressionTest.toLong(Arrays.asList(-3, 4, 4, -1)),
                        "1111111111111111111111111111111111111111111111111111111111111101" +
                                "000100011100000100001001011" + "0000000000000000000000000000000000000"),
                // -3,  7,  0, -5
                // -3, 10, 10, 5
                Arguments.of(2, CompressionTest.toLong(Arrays.asList(-3, 7, 17, 22)),
                        "1111111111111111111111111111111111111111111111111111111111111101" +
                                "0000000000000000000000000000000000000000000000000000000000000111" +
                                "00000100001001011" + "00000000000000000000000000000000000000000000000")
        );
    }

    static List<Long> toLong(List<Integer> ints) {
        return ints.stream()
                .mapToLong(Integer::longValue)
                .boxed().collect(Collectors.toList());
    }

    private static void print(List<BitSets> bitSetsList) {
        bitSetsList.forEach(item -> System.out.println("length: " + item.length + "  bits: " + item.toString()));
    }
}