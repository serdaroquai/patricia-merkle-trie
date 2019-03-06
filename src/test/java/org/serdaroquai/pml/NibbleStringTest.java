package org.serdaroquai.pml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.google.protobuf.ByteString;

public class NibbleStringTest {
	
	private static final byte[] ODD_LENGTH_BYTES = new byte[]{(byte) 0x12, (byte) 0x34};
	private static final byte[] EVEN_LENGTH_BYTES = new byte[]{(byte) 0x00, (byte) 0x34};
	private static final byte[] SAME_BYTES = new byte[]{(byte) 0x00, (byte) 0x34, (byte) 0x34};
	private static final byte[] LONG_BYTES = new byte[]{(byte) 0x10, (byte) 0x12, (byte) 0x34,
			(byte) 0x56, (byte) 0x78, (byte) 0x9a, (byte) 0xbc, (byte) 0xde};

	@Test
	@Disabled
	public void testOddFlag() {
		byte b = 0b10;
		System.out.println(b);
//		a useless test I keep here to remind myself the pretty byte representation
	}
	@Test
	public void testSubstring() {
		NibbleString n = NibbleString.compactDecode(ByteString.copyFrom(ODD_LENGTH_BYTES));
		final NibbleString s = n.substring(1); // 34
		assertThrows(IllegalArgumentException.class, () -> s.nibbleAt(-1));
		assertTrue(s.nibbleAt(0) == '3');
		assertTrue(s.nibbleAt(1) == '4');
		assertThrows(IllegalArgumentException.class, () -> s.nibbleAt(2));
	}
	
	@Test
	public void testSubstringEquals() {
		NibbleString n = NibbleString.compactDecode(ByteString.copyFrom(SAME_BYTES));
		final NibbleString s1 = n.substring(0,2); // 34
		final NibbleString s2 = n.substring(2,4); // 34
		final NibbleString s3 = n.substring(1,3); // 43
		
		assertEquals(s1,s2);
		assertNotEquals(s2, s3);
		assertNotEquals(s1, s3);
	}
	
	@Test
	public void testSubstringOfSubstring2() {
		ByteString b = ByteString.copyFromUtf8("doge"); // 64 6f 67 65
		NibbleString n = NibbleString.from(b);
		
		assertEquals("646f6765", n.toString());
		n = n.substring(1);
		assertEquals("46f6765", n.toString());
		n = n.substring(1);
		assertEquals("6f6765", n.toString());
	}
	
	@Test
	public void testSubstringOfSubstring() {
		NibbleString n = NibbleString.compactDecode(ByteString.copyFrom(LONG_BYTES));
		final NibbleString s = n.substring(2,9); // 23 45 67 8
		
		assertEquals(7, s.size());
		assertThrows(IllegalArgumentException.class, () -> s.nibbleAt(-1));
		assertTrue(s.nibbleAt(0) == '2');
		assertTrue(s.nibbleAt(1) == '3');
		assertTrue(s.nibbleAt(2) == '4');
		assertTrue(s.nibbleAt(3) == '5');
		assertTrue(s.nibbleAt(4) == '6');
		assertTrue(s.nibbleAt(5) == '7');
		assertTrue(s.nibbleAt(6) == '8');
		assertThrows(IllegalArgumentException.class, () -> s.nibbleAt(7));
		
		final NibbleString s2 = s.substring(2,5); // 45 6
		assertEquals(3, s2.size());
		assertThrows(IllegalArgumentException.class, () -> s2.nibbleAt(-1));
		assertTrue(s2.nibbleAt(0) == '4');
		assertTrue(s2.nibbleAt(1) == '5');
		assertTrue(s2.nibbleAt(2) == '6');
		assertThrows(IllegalArgumentException.class, () -> s2.nibbleAt(3));
	}
	
	@Test
	public void testOddLengthCompactDecode() {
		NibbleString n = NibbleString.compactDecode(ByteString.copyFrom(ODD_LENGTH_BYTES));
		assertTrue(n.nibbleAt(0) == '2');
		assertTrue(n.nibbleAt(1) == '3');
		assertTrue(n.nibbleAt(2) == '4');
		assertEquals(n.size(), 3);
	}
	
	@Test
	public void testEvenLengthCompactDecode() {
		NibbleString n = NibbleString.compactDecode(ByteString.copyFrom(EVEN_LENGTH_BYTES));
		assertTrue(n.nibbleAt(0) == '3');
		assertTrue(n.nibbleAt(1) == '4');
		assertEquals(n.size(), 2);
	}
	
	@Test
	public void testOddLengthCompactEncode() {
		ByteString expected = ByteString.copyFrom(ODD_LENGTH_BYTES);
		NibbleString n = NibbleString.compactDecode(expected);
		ByteString actual = NibbleString.compactEncode(n);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEvenLengthCompactEncode() {
		ByteString expected = ByteString.copyFrom(EVEN_LENGTH_BYTES);
		NibbleString n = NibbleString.compactDecode(expected);
		ByteString actual = NibbleString.compactEncode(n);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testBoundsCheck() {
		final NibbleString n = NibbleString.compactDecode(ByteString.copyFrom(EVEN_LENGTH_BYTES));
		assertThrows(IllegalArgumentException.class, () -> n.nibbleAt(2));
		assertThrows(IllegalArgumentException.class, () -> n.nibbleAt(-1));
	}
}
