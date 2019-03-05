package org.serdaroquai.pml;


import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;

public class HashTest {

	@Test
	public void testNodeHash() {
		ExtensionNode n = new ExtensionNode.Builder().withPartialKey("A").withValue("B").build();
		ExtensionNode m = new ExtensionNode.Builder().withPartialKey("A").withValue("B").build();
		ExtensionNode o = new ExtensionNode.Builder().withPartialKey("1").withValue("2").build();
		
		assertEquals(n, m);
		assertNotEquals(n, o);
		assertNotEquals(m, o);
		
		assertArrayEquals(Util.sha256(n), Util.sha256(m));
		assertFalse(Arrays.equals(Util.sha256(n), Util.sha256(o)));
		assertFalse(Arrays.equals(Util.sha256(m), Util.sha256(o)));
	}
	
	@Test
	public void hashAndByteString() {
		ExtensionNode n = new ExtensionNode.Builder().withPartialKey("A").withValue("B").build();
		byte[] bytes = Util.sha256(n);
		System.out.println(BaseEncoding.base16().encode(bytes));

		
		ByteString byteString = ByteString.copyFrom(bytes);
		byte bb = byteString.byteAt(0);
//		byte bb = bytes[0];
		byte left = (byte) ((bb & 0xF0) >> 4);	// 0000 0001 (left nibble with left padding)
		byte right = (byte) (bb & 0x0F);    	// 0000 1100 (right nibble with left padding)
		byte whole = (byte) ((left << 4) | right);
		
		System.out.println(bb);
		System.out.println(BaseEncoding.base16().encode(new byte[] {left}));
		System.out.println(BaseEncoding.base16().encode(new byte[] {right}));
		System.out.println(BaseEncoding.base16().encode(new byte[] {whole}));
		
		
		
	}
}
