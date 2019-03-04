package org.serdaroquai.pml;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class ByteUtilsTest {

	@Test
	public void testIterator() {
		byte[] bytes = new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, 
				(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
		Iterator<Character> it = ByteUtils.NibbleIterator.from(bytes);
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
		byte[] bytes = new byte[0];
		Iterator<Character> it = ByteUtils.NibbleIterator.from(bytes);
		assertFalse(it.hasNext());
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testNoSuchElementIterator() {
		byte[] bytes = new byte[] { (byte) 0x12 };
		Iterator<Character> it = ByteUtils.NibbleIterator.from(bytes);
		it.next(); // 1
		it.next(); // 2
		it.next(); // Exception
	}
	
	@Test
	public void testEncodeOdd() {
		String expected = "123456789abcdef";
		byte[] bytes = new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, 
				(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
		String actual = ByteUtils.encode(bytes);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testEncodeEven() {
		String expected = "36";
		byte[] bytes = new byte[] {(byte) 0x00, (byte) 0x36};
		String actual = ByteUtils.encode(bytes);
		assertEquals(expected, actual);
	}
	
	@Test
	public void testDecodeOdd() {
		byte[] expectedOdd = new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, 
				(byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
		byte[] actualOdd = ByteUtils.decode("123456789abcdef");
		assertArrayEquals(expectedOdd, actualOdd);
		
		byte[] expectedEven = new byte[] {(byte) 0x00, (byte) 0x0F};
		byte[] actualEven = ByteUtils.decode("0F");
 		assertArrayEquals(expectedEven, actualEven);
	}
	
	@Test
	public void testDecodeEven() {
		byte[] expectedEven = new byte[] {(byte) 0x00, (byte) 0x0F};
		byte[] actualEven = ByteUtils.decode("0F");
 		assertArrayEquals(expectedEven, actualEven);
	}
	
	@Test
	public void testUpperCaseLowerCase() {
		byte[] expectedEven = new byte[] {(byte) 0x00, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
		byte[] actualEven = ByteUtils.decode("abcdef");
 		assertArrayEquals(expectedEven, actualEven);
 		
 		expectedEven = new byte[] {(byte) 0x00, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
		actualEven = ByteUtils.decode("ABCDEF");
 		assertArrayEquals(expectedEven, actualEven);
	}
	
}
