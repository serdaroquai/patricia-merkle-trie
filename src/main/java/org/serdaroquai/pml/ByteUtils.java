package org.serdaroquai.pml;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteUtils {

	/*
	 * first nibble is 1 for odd length, 0 for even. even length is accompanied by another
	 * 0 nibble in order to pad correctly
	 */
	private static final byte ODD_START = 0x10;
	private static final byte EVEN_START = 0x00;
	
	private static final byte[] hexToNibbles = new byte[103];
	private static final char[] nibblesToHex = new char[16];
	static {
		//hex to nibbles
		hexToNibbles[48]=0x00;
		hexToNibbles[49]=0x01;
		hexToNibbles[50]=0x02;
		hexToNibbles[51]=0x03;
		hexToNibbles[52]=0x04;
		hexToNibbles[53]=0x05;
		hexToNibbles[54]=0x06;
		hexToNibbles[55]=0x07;
		hexToNibbles[56]=0x08;
		hexToNibbles[57]=0x09;
		hexToNibbles[65]=0x0A;
		hexToNibbles[66]=0x0B;
		hexToNibbles[67]=0x0C;
		hexToNibbles[68]=0x0D;
		hexToNibbles[69]=0x0E;
		hexToNibbles[70]=0x0F;
		hexToNibbles[97]=0x0A;
		hexToNibbles[98]=0x0B;
		hexToNibbles[99]=0x0C;
		hexToNibbles[100]=0x0D;
		hexToNibbles[101]=0x0E;
		hexToNibbles[102]=0x0F;
		
		// nibbles to Hex
		nibblesToHex[0] = '0';
		nibblesToHex[1] = '1';
		nibblesToHex[2] = '2';
		nibblesToHex[3] = '3';
		nibblesToHex[4] = '4';
		nibblesToHex[5] = '5';
		nibblesToHex[6] = '6';
		nibblesToHex[7] = '7';
		nibblesToHex[8] = '8';
		nibblesToHex[9] = '9';
		nibblesToHex[10] = 'a';
		nibblesToHex[11] = 'b';
		nibblesToHex[12] = 'c';
		nibblesToHex[13] = 'd';
		nibblesToHex[14] = 'e';
		nibblesToHex[15] = 'f';
		
	}
	
	public static class NibbleIterator implements Iterator<Character> {

		private byte[] bytes;
		private int i = -1;
		
		public static Iterator<Character> from(byte[] bytes) {
			return new NibbleIterator(bytes);
		}
		
		private NibbleIterator(byte[] bytes) { this.bytes = bytes;}
		
		@Override
		public boolean hasNext() { return ((i + 1) >> 1) < bytes.length;}

		@Override
		public Character next() {
			if (!hasNext()) throw new NoSuchElementException();
			i++;
			return nibbleToHex(bytes[i >> 1], (i & 0x01) == 0);
		}
		
	}
	
	
	
	public static String encode(byte[] bytes) {
		if (bytes.length == 0) throw new IllegalStateException("Can not be empty");
		
		StringBuilder sb = new StringBuilder();
		
		if (nibbleToHex(bytes[0], true) == '1') sb.append(nibbleToHex(bytes[0], false));
		
		for (int i=1; i < bytes.length; i++) {
			sb.append(nibbleToHex(bytes[i], true)).append(nibbleToHex(bytes[i], false));
		}
		
		return sb.toString();
	}
	
	public static byte[] decode(CharSequence cs) {
		int len = cs.length();
		if (len == 0) throw new IllegalStateException("Can not be empty");
		
		byte[] result = new byte[(len >> 1) + 1];
		boolean odd = (len & 0x01) == 1;
		
		if (odd) result[0] = (byte) (ODD_START | hexToNibble(cs.charAt(0), false));
		else result[0] = EVEN_START;
		
		int read = odd ? 1 : 0;
		int write = 1;
		while (read < len) {
			result[write++] = (byte) (hexToNibble(cs.charAt(read++), true) | hexToNibble(cs.charAt(read++), false));
		}
		return result;
	}
	
	/*
	 * Converts a single hex char to a nibble representation. Since the result has to be a byte
	 * alignment to left or right is optional
	 * for example:  
	 * 		left  aligned 'B' ==> 1011 000
	 * 		right aligned 'B' ==> 0000 1011
	 */
	protected static byte hexToNibble(char hex, boolean alignLeft) {
		//TODO bounds check valid hex? 0..9
		return (byte) (alignLeft ? hexToNibbles[hex] << 4 : hexToNibbles[hex]);
	}
	
	
	protected static char nibbleToHex(byte b, boolean leftNibble) {
		return leftNibble ? nibblesToHex[((b & 0xF0) >> 4)] : nibblesToHex[(b & 0x0F)];
	}
	
}
