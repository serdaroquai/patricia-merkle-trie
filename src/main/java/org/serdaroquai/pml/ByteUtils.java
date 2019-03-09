package org.serdaroquai.pml;

@Deprecated
public class ByteUtils {

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
	
	/*
	 * Converts a single hex char to a nibble representation. Since the result has to be a byte
	 * alignment to left or right is optional
	 * for example:  
	 * 		left  aligned 'B' ==> 1011 0000
	 * 		right aligned 'B' ==> 0000 1011
	 */
	public static byte hexToNibble(char hex, boolean alignLeft) {
		//TODO bounds check valid hex? 0..9
		return (byte) (alignLeft ? hexToNibbles[hex] << 4 : hexToNibbles[hex]);
	}
	
	
	public static char nibbleToHex(byte b, boolean leftNibble) {
		return leftNibble ? nibblesToHex[((b & 0xF0) >> 4)] : nibblesToHex[(b & 0x0F)];
	}
	
	public static int nibbleToIndex(char hexNibble) {
		return hexToNibbles[hexNibble];
	}
	
}
