package org.serdaroquai.pml;

import com.google.protobuf.ByteString;
import com.google.protobuf.ByteString.ByteIterator;

/**
 * Immutable class representing an arbitrary length of nibbles. Can only be copied from
 * ByteStrings that are compact encoded.
 * 
 * Since underlying char[] is immutable substring() methods return a view of same array 
 * so they are fast.
 * 
 * @author tr1b6162
 *
 */
public class NibbleString {

	public static final byte EVEN_START 	= 0b0000_0000;
	public static final byte ODD_START 		= 0b0001_0000;
	public static final byte TERMINAL 		= 0b0010_0000;
	
	private char[] nibbles; 
	private int offset;
	private int length;
	
	private NibbleString() {};
	
	private NibbleString(char[] nibbles, int begin, int length) {
		this.nibbles = nibbles;
		this.offset = begin;
		this.length = length;
	}
	
	public char nibbleAt(int pos) {
		if (pos < 0 || pos >= length) throw new IllegalArgumentException("Out of bounds");
		return nibbles[pos + offset];
	}
	
	public int size() {
		return length;
	}
	
	public NibbleString substring(int startIndex) {
		return substring(startIndex, size());
	}
	
	public NibbleString substring(int startIndex, int endIndex) {
		final int newLength = endIndex - startIndex;
		if ((startIndex | endIndex | newLength | (length - newLength)) < 0) 
			throw new IllegalArgumentException("Out of bounds");
		
		return new NibbleString(nibbles, offset + startIndex, newLength);
	}
	
	// TODO, this will be needed to convert an even length 
	// unencoded bytestring into a nibblestring. perhaps used as key?
	// TODO refactor and test
	public static NibbleString from(ByteString bytes) {
		NibbleString instance = new NibbleString();
		int len = bytes.size() == 0 ? 0 : bytes.size() << 1;
		instance.nibbles = new char[len];
		
		int w = 0;
		byte b;
		ByteIterator it = bytes.iterator();
		while (it.hasNext()) {
			b = it.nextByte();
			instance.nibbles[w++] = ByteUtils.nibbleToHex(b, true);
			instance.nibbles[w++] = ByteUtils.nibbleToHex(b, false);
		}
		
		instance.offset = 0;
		instance.length = instance.nibbles.length;
		
		return instance;
		
	}

	public static NibbleString unpack(ByteString bytes) {
		if (bytes.size() == 0) throw new IllegalArgumentException("Can not be empty");
		
		int len = bytes.size();
		NibbleString instance = new NibbleString();
		
		int w = 0, r = 1;
		// TODO FIX ME UGLY CONVERSION
		byte flag = (byte) (ByteUtils.nibbleToHex(bytes.byteAt(0), true) - '0'); 
		if ((flag & 0x01) == 1) {
			instance.nibbles = new char[(len << 1) - 1];
			instance.nibbles[w++] = ByteUtils.nibbleToHex(bytes.byteAt(0), false);
		} else {
			instance.nibbles = new char[(len << 1) - 2];
		}
		
		while (r<len) {
			instance.nibbles[w++] = ByteUtils.nibbleToHex(bytes.byteAt(r), true);
			instance.nibbles[w++] = ByteUtils.nibbleToHex(bytes.byteAt(r++), false);
		}
		
		instance.offset = 0;
		instance.length = instance.nibbles.length;
				
		return instance;
	}
	
	public static ByteString pack(NibbleString n, boolean isTerminal) {
		int len = n.size();
		if (len == 0) throw new IllegalStateException("Can not be empty");
		
		byte[] result = new byte[(len >> 1) + 1];
		boolean odd = (len & 0x01) == 1;
		
		byte flag = odd ? ODD_START : EVEN_START;
		flag = (byte) (isTerminal ? flag | TERMINAL : flag);
		
		result[0] = odd ? (byte) (flag | ByteUtils.hexToNibble(n.nibbleAt(0), false)) : flag;
		
		int read = odd ? 1 : 0;
		int write = 1;
		while (read < len) {
			result[write++] = (byte) (ByteUtils.hexToNibble(n.nibbleAt(read++), true) 
					| ByteUtils.hexToNibble(n.nibbleAt(read++), false));
		}
		
		return ByteString.copyFrom(result);
	}
	
//	public static ByteString concat(NibbleString ...nibbles) {
//		if (nibbles == null || nibbles.length == 0)
//			throw new IllegalStateException("Can not be empty");
//		
//		int len = 0;
//		for (NibbleString n : nibbles) len += n.size();
//		
//		byte[] result = new byte[(len >> 1) + 1];
//		boolean odd = (len & 0x01) == 1;
//		
//		NibbleString n = nibbles[0];
//		if (odd) result[0] = (byte) (ODD_START | ByteUtils.hexToNibble(n.nibbleAt(0), false));
//		else result[0] = EVEN_START;
//	}

	@Override
	public int hashCode() {
        if (nibbles == null)
            return 0;

        int result = 1;
        for (int i=0; i<length; i++) {
        	result = 31 * result + nibbleAt(i);
        }

        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NibbleString other = (NibbleString) obj;
		if (length != other.length)
			return false;
		for (int i=0; i<length; i++) {
			if (nibbleAt(i) != other.nibbleAt(i))
				return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<length; i++) {
			sb.append(nibbleAt(i));
		}
		return sb.toString();
	}
	
	
}
