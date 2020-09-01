import com.conimon.BitSets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BitSetsTest {
    private BitSet bitSet;

    @BeforeAll
    public void setUp() {
        // bitSet = {0, 3, 4, 5} = 0011 1001 = 57
        bitSet = BitSet.valueOf(new long[] {0b00111001});
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
                Arguments.of(57, "0000000000000000000000000000000000000000000000000000000000111001"),
                Arguments.of(-3, "1111111111111111111111111111111111111111111111111111111111111101"),
                Arguments.of(7,  "0000000000000000000000000000000000000000000000000000000000000111"),
                Arguments.of(-5, "1111111111111111111111111111111111111111111111111111111111111011")
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
                Arguments.of(57, "0001110111001"),
                // length 3:0b000011 101
                Arguments.of(-3, "000011101"),
                // length 4:0b000100 0111
                Arguments.of(7, "0001000111"),
                // length 4:0b000100 1011
                Arguments.of(-5, "0001001011")
        );
    }

    @ParameterizedTest
    @MethodSource("provideBitSetsList")
    public void testConcatenate(List<BitSets> bitSetsList, String expected) {
        BitSet bs = BitSets.concatenate(bitSetsList);
        System.out.println(BitSets.toString(bs));
        assertEquals(expected, BitSets.toString(bs));
    }

    private static Stream<Arguments> provideBitSetsList() {
        return Stream.of(
                Arguments.of(BitSetsTest.toLong(Arrays.asList(1, 0, -6, -1, 57)).stream()
                        .map(n -> BitSets.fromLong(n).truncate())
                        .collect(Collectors.toList()), "000010010000010000100101000000110001110111001" + "0000000000000000000"),
                Arguments.of(BitSetsTest.toLong(Arrays.asList(-3, 7, 0, -5)).stream()
                        .map(n -> BitSets.fromLong(n).truncate())
                        .collect(Collectors.toList()), "000011101000100011100000100001001011" + "0000000000000000000000000000")
        );
    }

    @Test
    public void testDissociateFirstValues() {
        // bs1: (0, 3, 5, 2)
        int[] setIndex1 = new int[] {37, 39, 40, 45, 47, 51, 55, 56, 58, 59};

        // bs2: (1, 0, 3, 5, 2)
        int[] setIndex2 = new int[] {37, 39, 40, 45, 47, 51, 55, 56, 58, 59, 128};

        List<BitSets> expected1 = new ArrayList<>();
        expected1.add(BitSets.fromLong(0));

        List<BitSets> expected2 = new ArrayList<>();
        expected2.add(BitSets.fromLong(1));
        expected2.add(BitSets.fromLong(0));

        assertEquals(expected1, BitSets.dissociateFirstValues(generateBitSet(setIndex1, 128), 1));
        assertEquals(expected2, BitSets.dissociateFirstValues(generateBitSet(setIndex2, 192), 2));
    }

    @Test
    public void testDissociateTruncatedValues() {
        // bs1: (0, 3, 5, 2)
        int[] setIndex1 = new int[] {37, 39, 40, 45, 47, 51, 55, 56, 58, 59};

        // bs2: (1, 0, 3, 5, 2)
        int[] setIndex2 = new int[] {37, 39, 40, 45, 47, 51, 55, 56, 58, 59, 128};

        List<BitSets> expected = new ArrayList<>();
        expected.add(BitSets.fromLong(3));
        expected.add(BitSets.fromLong(5));
        expected.add(BitSets.fromLong(2));

        assertEquals(expected, BitSets.dissociateTruncatedValues(new ArrayList<>(),
                generateBitSet(setIndex1, 128), 1));
        assertEquals(expected, BitSets.dissociateTruncatedValues(new ArrayList<>(),
                generateBitSet(setIndex2, 192), 2));
    }

    private BitSet generateBitSet(int[] setIndex, int minSize) {
        BitSet bs = new BitSet(minSize);

        for (int index : setIndex) {
            bs.set(index);
        }

        return bs;
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
