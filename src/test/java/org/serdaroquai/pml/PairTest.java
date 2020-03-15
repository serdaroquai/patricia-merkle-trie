package org.serdaroquai.pml;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class PairTest {

    @Test
    public void testPairComparator() {

        Pair p0 = new Pair(Collections.emptyList(), null);
        Pair p1 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x01)), null);
        Pair p2 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x02)), null);
        Pair p3 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x02)), null);
        Pair p4 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x03)), null);
        Pair p5 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x02), Byte.valueOf((byte) 0x05)), null);
        Pair p6 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x03), Byte.valueOf((byte) 0x04)), null);
        Pair p7 = new Pair(Arrays.asList(Byte.valueOf((byte) 0x00), Byte.valueOf((byte) 0x04), Byte.valueOf((byte) 0x04)), null);
        // p2 and p3 are equal, therefore expected in insertion order p2 p3 after sort.
        List<Pair> pairs = Arrays.asList(p2, p7, p3, p0, p1, p4, p6, p5);
        Collections.sort(pairs);

        assertEquals(pairs, Arrays.asList(p0, p1,p2,p3,p4,p5,p6,p7));
    }
}
