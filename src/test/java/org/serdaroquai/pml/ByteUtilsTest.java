package org.serdaroquai.pml;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

public class ByteUtilsTest {

//	@Test
//	public void testByteIterator() {
//		byte[] bytes = new byte[] {(byte) 0x00};
////		ByteString bs = ByteString.copyFrom(bytes);
//		ByteString bs = ByteString.copyFrom("1",StandardCharsets.UTF_8);
////		System.out.println(bs);
////		System.out.println(BaseEncoding.base16().encode(bytes));
//	}
//	
//	@Test
//	public void encode() {
//		byte[] expecteds = "someKey".getBytes(StandardCharsets.UTF_8);
//		String encoded = BaseEncoding.base16().encode(expecteds);
//		byte[] actuals = BaseEncoding.base16().decode(encoded);
//		assertArrayEquals(expecteds, actuals);
//	}
//	
//	@Test
//	public void concat() {
//		byte[] bytes = new byte[] {(byte) 0x00};
//		ByteString bs = ByteString.copyFrom(bytes);
//		ByteString longer = bs.concat(bs);
//	}
	
	@Test
	public void testDecodeOdd() {
		byte[] expectedOdd = new byte[] {(byte) 0x11, (byte) 0x23, (byte) 0x45, (byte) 0x67, (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF};
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
