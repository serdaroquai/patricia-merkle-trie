package org.serdaroquai.pml;


import java.util.Iterator;

import com.google.protobuf.ByteString;

/**
 * Immutable class representing an arbitrary length of nibbles.
 * 
 * Since underlying byte[] is immutable substring() methods return a view of same array 
 * so they are fast.
 * 
 * @author tr1b6162
 *
 */
public class NibbleString implements Iterable<Byte>{

	public static final byte EVEN_START 	= 0b0000_0000;
	public static final byte ODD_START 		= 0b0001_0000;
	public static final byte TERMINAL 		= 0b0010_0000;
	
	private static final char[] base16 = new char[]{'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
	
	// each nibble is one byte despite the obvious waste of space
	// each nibble is right aligned
	private byte[] nibbles; 
	private int offset;
	private int length;
	
	private NibbleString() {};
	
	private NibbleString(byte[] nibbles, int begin, int length) {
		this.nibbles = nibbles;
		this.offset = begin;
		this.length = length;
	}
	
	public char nibbleAsChar(int pos) {
		return base16[nibbleAsByte(pos)];
	}
	
	public byte nibbleAsByte(int pos) {
		if (pos < 0 || pos >= length) throw new IllegalArgumentException("Out of bounds");
		return nibbles[pos + offset];
	}
	
	public int size() {
		return length;
	}
	
	/**
	 * Returns a view of the underlying NibbleString bounded by the given indices,
	 * without copying its underlying byte[].
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public NibbleString substring(int startIndex) {
		return substring(startIndex, size());
	}
	
	/**
	 * Returns a view of the underlying NibbleString bounded by the given indices,
	 * without copying its underlying byte[].
	 * 
	 * @param startIndex
	 * @param endIndex
	 * @return
	 */
	public NibbleString substring(int startIndex, int endIndex) {
		final int newLength = endIndex - startIndex;
		if ((startIndex | endIndex | newLength | (length - newLength)) < 0) 
			throw new IllegalArgumentException("Out of bounds");
		
		return new NibbleString(nibbles, offset + startIndex, newLength);
	}
	
	/**
	 * Converts given ByteString to NibbleString by copying the underlying byte[] 
	 * 
	 * Resulting NibbleString is always even length and is not packed.
	 * 
	 * @param bytes
	 * @return
	 */
	public static NibbleString from(ByteString bytes) {
		NibbleString instance = new NibbleString();
		int len = bytes.size() == 0 ? 0 : bytes.size() << 1;
		instance.nibbles = new byte[len];
		
		int w = 0;
		for (byte b : bytes) {
			instance.nibbles[w++] = (byte) ((b & 0xf0) >> 4);
			instance.nibbles[w++] = (byte) ((b & 0x0f));
		}
		
		instance.offset = 0;
		instance.length = instance.nibbles.length;
		
		return instance;
		
	}

	/**
	 * Unpacks a packed ByteString into a NibbleString. 
	 * 
	 * First nibble of a packed ByeString always contains leading flags representing 
	 *  1) nibble is odd/even length. (Since a byte can store 2 nibbles)
	 * 	2) nibble is a key for a terminal node 
	 * 
	 * @param bytes
	 * @return
	 */
	public static NibbleString unpack(ByteString bytes) {
		if (bytes.size() == 0) throw new IllegalArgumentException("Can not be empty");
		
		int len = bytes.size();
		NibbleString instance = new NibbleString();
		
		int w = 0, r = 1;
		if ((bytes.byteAt(0) & ODD_START) == ODD_START) {
			instance.nibbles = new byte[(len << 1) - 1];
			instance.nibbles[w++] = (byte) (bytes.byteAt(0) & 0x0f);
		} else {
			instance.nibbles = new byte[(len << 1) - 2];
		}
		
		while (r<len) {
			instance.nibbles[w++] = (byte) ((bytes.byteAt(r) & 0xf0) >> 4);
			instance.nibbles[w++] = (byte) (bytes.byteAt(r++) & 0x0f);
		}
		
		instance.offset = 0;
		instance.length = instance.nibbles.length;
				
		return instance;
	}
	
	/**
	 * Packs a NibbleString into a packed ByteString.
	 * 
	 * First nibble of a packed ByeString always contains leading flags representing 
	 *  1) nibble is odd/even length. (Since a byte can store 2 nibbles)
	 * 	2) nibble is a key for a terminal node
	 * 
	 * @param bytes
	 * @param isTerminal
	 * @return
	 */
	public static ByteString pack(NibbleString n, boolean isTerminal) {
		int len = n.size();
		
		byte[] result = new byte[(len >> 1) + 1];
		boolean odd = (len & 0x01) == 1;
		
		byte flag = odd ? ODD_START : EVEN_START;
		flag = (byte) (isTerminal ? flag | TERMINAL : flag);
		
		// hexToNibble(n, align left)
		
		result[0] = odd ? (byte) (flag | n.nibbleAsByte(0)) : flag;
		
		int read = odd ? 1 : 0;
		int write = 1;
		while (read < len) {
			result[write++] = (byte) ((n.nibbleAsByte(read++) << 4) | n.nibbleAsByte(read++));
		}
		
		return ByteString.copyFrom(result);
	}
	
	public static boolean isTerminal(ByteString packed) {
		return (packed.byteAt(0) & TERMINAL) == TERMINAL;
	}
	
	@Override
	public int hashCode() {
        if (nibbles == null)
            return 0;

        int result = 1;
        for (int i=0; i<length; i++) {
        	result = 31 * result + nibbleAsChar(i);
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
			if (nibbleAsChar(i) != other.nibbleAsChar(i))
				return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i=0; i<length; i++) {
			sb.append(nibbleAsChar(i));
		}
		return sb.toString();
	}

	@Override
	public Iterator<Byte> iterator() {
		return new Iterator<Byte>() {
			int i=0;
			@Override public boolean hasNext() { return length > i;}
			@Override public Byte next() { return nibbleAsByte(i++);}
		};
	}
}
