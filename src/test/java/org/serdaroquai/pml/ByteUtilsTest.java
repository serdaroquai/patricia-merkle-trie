package org.serdaroquai.pml;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;

import com.google.protobuf.ByteString;

public class ByteUtilsTest {

	
	@Test
	public void testIteratorWithString() {
		ByteString b = ByteString.copyFrom("doge", StandardCharsets.UTF_8); // <64 6f 67 65>
		Iterator<Character> it = ByteUtils.NibbleIterator.from(b);
		assertTrue('6' == it.next());
		assertTrue('4' == it.next());
		assertTrue('6' == it.next());
		assertTrue('f' == it.next());
		assertTrue('6' == it.next());
		assertTrue('7' == it.next());
		assertTrue('6' == it.next());
		assertTrue('5' == it.next());
	}
	
	@Test
	public void testIterator() {
		byte[] bytes = new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, 
				(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
		Iterator<Character> it = ByteUtils.NibbleIterator.from(ByteString.copyFrom(bytes));
		assertNotNull(it);
		
		int i=0;
		
		while (it.hasNext()) {
			
			char c1 = ByteUtils.nibbleToHex(bytes[i], true);
			char c2 = ByteUtils.nibbleToHex(bytes[i], false);
			
			assertTrue(it.hasNext());
			char c3 = it.next();
			assertTrue(it.hasNext());
			char c4 = it.next();
			
			assertTrue(c1 == c3);
			assertTrue(c2 == c4);
			
			i++;
		}
		
		assertTrue(i == bytes.length);
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testEmptyIterator() {
		Iterator<Character> it = ByteUtils.NibbleIterator.from(ByteString.EMPTY);
		assertFalse(it.hasNext());
	}
	
	@Test
	public void testNoSuchElementIterator() {
		byte[] bytes = new byte[] { (byte) 0x12 };
		final Iterator<Character> it = ByteUtils.NibbleIterator.from(ByteString.copyFrom(bytes));
		it.next(); // 1
		it.next(); // 2
		assertThrows(NoSuchElementException.class, () -> it.next());
	}
	
	@Test
	public void testEncodeOdd() {
		String expected = "123456789abcdef";
		ByteString bytes = ByteString.copyFrom(new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, 
				(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF});
		String actual = ByteUtils.compactDecode(bytes);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEncodeEven() {
		String expected = "36";
		ByteString bytes = ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0x36});
		String actual = ByteUtils.compactDecode(bytes);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDecodeOdd() {
		ByteString expectedOdd = ByteString.copyFrom(new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, 
				(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF});
		ByteString actualOdd = ByteUtils.compactEncode("123456789abcdef");
		assertEquals(expectedOdd, actualOdd);
		
		ByteString expectedEven = ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0x0F});
		ByteString actualEven = ByteUtils.compactEncode("0F");
 		assertEquals(expectedEven, actualEven);
	}
	
	@Test
	public void testDecodeEven() {
		ByteString expectedEven = ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0x0F});
		ByteString actualEven = ByteUtils.compactEncode("0F");
 		assertEquals(expectedEven, actualEven);
	}
	
	@Test
	public void testUpperCaseLowerCase() {
		ByteString expectedEven = ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0xAB, 
				(byte) 0xCD, (byte) 0xEF});
		ByteString actualEven = ByteUtils.compactEncode("abcdef");
 		assertEquals(expectedEven, actualEven);
 		
 		expectedEven = ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0xAB,
 				(byte) 0xCD, (byte) 0xEF});
		actualEven = ByteUtils.compactEncode("ABCDEF");
 		assertEquals(expectedEven, actualEven);
	}
	
}
