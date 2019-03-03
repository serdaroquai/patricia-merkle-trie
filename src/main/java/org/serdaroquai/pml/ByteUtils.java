package org.serdaroquai.pml;

public class ByteUtils {

	/*
	 * first nibble is 1 for odd length, 0 for even. even length is accompanied by another
	 * 0 nibble in order to pad correctly
	 */
	private static final byte ODD_START = 0x10;
	private static final byte EVEN_START = 0x00;
	
	private static final byte[] alphabet = new byte[103];
	static {
		//digits
		alphabet[48]=0x00;alphabet[49]=0x01;alphabet[50]=0x02;alphabet[51]=0x03;alphabet[52]=0x04;
		alphabet[53]=0x05;alphabet[54]=0x06;alphabet[55]=0x07;alphabet[56]=0x08;alphabet[57]=0x09;
		
		// uppercase chars
		alphabet[65]=0x0A;alphabet[66]=0x0B;alphabet[67]=0x0C;
		alphabet[68]=0x0D;alphabet[69]=0x0E;alphabet[70]=0x0F;
		
		// lowercase chars
		alphabet[97]=0x0A;alphabet[98]=0x0B;alphabet[99]=0x0C;
		alphabet[100]=0x0D;alphabet[101]=0x0E;alphabet[102]=0x0F;
	}
			
	public static String encode(byte[] bytes) {
		//TODO
		throw new UnsupportedOperationException("not yet implemented :(");
	}

	// TODO nibble Iterator
	
	public static byte[] decode(CharSequence cs) {
		int len = cs.length();
		if (len == 0) throw new IllegalStateException("Can not be empty");
		
		byte[] result = new byte[(len >> 1) + 1];
		boolean odd = (len & 0x01) == 1;
		
		if (odd) result[0] = (byte) (ODD_START | toNibble(cs.charAt(0), false));
		else result[0] = EVEN_START;
		
		int read = odd ? 1 : 0;
		int write = 1;
		while (read < len) {
			result[write++] = (byte) (toNibble(cs.charAt(read++), true) | toNibble(cs.charAt(read++), false));
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
	private static byte toNibble(char hex, boolean alignLeft) {
		//TODO bounds check valid hex? 0..9
		return (byte) (alignLeft ? alphabet[hex] << 4 : alphabet[hex]);
	}
}
