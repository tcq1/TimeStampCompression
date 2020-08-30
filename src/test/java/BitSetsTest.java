import com.conimon.BitSets;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.BitSet;

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

    @Test
    public void testDeconcatenateFirstValues() {

    }

    @Test
    public void testDeconcatenateTruncatedValues() {

    }

    @Test
    public void testCalculateDeconcatenatedValue() {
        BitSet expected = BitSet.valueOf(new long[] {0b01110});
        assertEquals(expected, BitSets.calculateDeconcatenatedValue(bitSet, 6, 5));
    }

    @Test
    public void testMakeNegative() {
        BitSet expected = bitSet;
        expected.set(6, 64);
        assertEquals(expected, BitSets.makeNegative(bitSet));
    }
}
