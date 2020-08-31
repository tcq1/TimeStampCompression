import com.conimon.BitSets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
public class BitSetsTest {
    private BitSet bitSet;
    private BitSets bitSets;

    @BeforeAll
    public void setUp() {
        // bitSet = {0, 3, 4, 5} = 0011 1001 = 57
        bitSet = BitSet.valueOf(new long[] {0b00111001});

        bitSets = new BitSets(bitSet);
    }

    @ParameterizedTest
    @MethodSource("provideLong")
    public void testFromLong(long value, String expected) {
        assertEquals(expected, BitSets.fromLong(value).toString());
    }

    private static Stream<Arguments> provideLong() {
        return Stream.of(
                Arguments.of(1,  "0000000000000000000000000000000000000000000000000000000000000001"),
                Arguments.of(0,  "0000000000000000000000000000000000000000000000000000000000000000"),
                Arguments.of(-6, "1111111111111111111111111111111111111111111111111111111111111010"),
                Arguments.of(-1, "1111111111111111111111111111111111111111111111111111111111111111"),
                Arguments.of(57, "0000000000000000000000000000000000000000000000000000000000111001")
        );
    }

    @ParameterizedTest
    @MethodSource("provideLongTruncate")
    public void testTruncate(long value, String expected) {
        assertEquals(expected, BitSets.fromLong(value).truncate().toString());
    }

    private static Stream<Arguments> provideLongTruncate() {
        return Stream.of(
                // length 2:0b000010 01
                Arguments.of(1,  "00001001"),
                // length 1:0b000001 0
                Arguments.of(0,  "0000010"),
                // length 4:0b000100 1010
                Arguments.of(-6, "0001001010"),
                // length 1:0b000001 1
                Arguments.of(-1, "0000011"),
                // length 7:0b000111 0111001
                Arguments.of(57, "0001110111001")
        );
    }

    @Test
    public void testConcatenate() {
        List<Long>longs = BitSetsTest.toLong(Arrays.asList(1, 0, -6, -1, 57));
        List<BitSets> bitSetsList = longs.stream()
                .map(n -> BitSets.fromLong(n).truncate())
                .collect(Collectors.toList());
        BitSet bs = BitSets.concatenate(bitSetsList);
        assertEquals("0000100100000100001001010000001100011101110010000000000000000000", BitSets.toString(bs));
    }

    @Test
    public void testDissociateFirstValues() {

    }

    @Test
    public void testDissociateTruncatedValues() {

    }

    @Test
    public void testCalculateDissociatedValue() {
        BitSet expected = BitSet.valueOf(new long[] {0b01110});
        assertEquals(expected, BitSets.calculateDissociatedValue(bitSet, 6, 5));
    }

    @Test
    public void testMakeNegative() {
        BitSet expected = bitSet;
        expected.set(6, 64);
        assertEquals(expected, BitSets.makeNegative(bitSet));
    }

    static List<Long> toLong(List<Integer> ints) {
        return ints.stream()
                .mapToLong(Integer::longValue)
                .boxed().collect(Collectors.toList());
    }
}
