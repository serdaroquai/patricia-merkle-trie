package org.serdaroquai.pml;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.serdaroquai.pml.NodeProto.TrieNode;
import org.serdaroquai.pml.NodeProto.TrieNode.Builder;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class Common {

	private static final MessageDigest sha256digest;
	public static final TrieNode BRANCH_NODE_PROTOTYPE;
	public static final TrieNode EMPTY_NODE = TrieNode.newBuilder().build();
	public static final ByteString EMPTY_NODE_BYTES = EMPTY_NODE.toByteString();

	// maps for fast conversion
	private static final byte[] hexToNibbles = new byte[103];
	private static final char[] nibblesToHex = new char[16];

	static {
		try {
			sha256digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}

		// build a prototype branch node
		Builder newBuilder = TrieNode.newBuilder();
		for (int i = 0; i < 17; i++)
			newBuilder.addItem(ByteString.EMPTY);
		BRANCH_NODE_PROTOTYPE = newBuilder.build();

		// hex to nibbles
		hexToNibbles[48] = 0x00;
		hexToNibbles[49] = 0x01;
		hexToNibbles[50] = 0x02;
		hexToNibbles[51] = 0x03;
		hexToNibbles[52] = 0x04;
		hexToNibbles[53] = 0x05;
		hexToNibbles[54] = 0x06;
		hexToNibbles[55] = 0x07;
		hexToNibbles[56] = 0x08;
		hexToNibbles[57] = 0x09;
		hexToNibbles[65] = 0x0A;
		hexToNibbles[66] = 0x0B;
		hexToNibbles[67] = 0x0C;
		hexToNibbles[68] = 0x0D;
		hexToNibbles[69] = 0x0E;
		hexToNibbles[70] = 0x0F;
		hexToNibbles[97] = 0x0A;
		hexToNibbles[98] = 0x0B;
		hexToNibbles[99] = 0x0C;
		hexToNibbles[100] = 0x0D;
		hexToNibbles[101] = 0x0E;
		hexToNibbles[102] = 0x0F;

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
	
	/**
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

	public static ByteString sha256(ByteString raw) {
		return ByteString.copyFrom(sha256digest.digest(raw.toByteArray()));
	}

	public static byte[] sha256(byte[] bytes) {
		return sha256digest.digest(bytes);
	}

	public static NodeType getNodeType(TrieNode node) {
		if (EMPTY_NODE.equals(node))
			return NodeType.BLANK;

		switch (node.getItemCount()) {
		case 1:
			return NodeType.HASH;
		case 2:
			ByteString key = node.getItem(0);
			return NibbleString.isTerminal(key) ? NodeType.LEAF : NodeType.EXTENSION;
		case 17:
			return NodeType.BRANCH;
		default:
			throw new AssertionError("Unrecognized encoded Node format");
		}
	}
	
	public static String hashToString(ByteString hashNodeEncoded) {
		NibbleString hashNibbles = NibbleString.from(hashNodeEncoded);
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (int i=4; i< 8; i++) sb.append(hashNibbles.nibbleAsChar(i));
		sb.append("..");
		for (int i=30; i< 34; i++) sb.append(hashNibbles.nibbleAsChar(i));
		sb.append(')');
		return sb.toString();
	}

	public static String toString(ByteString nodeEncoded) {
		try {
			TrieNode node = TrieNode.parseFrom(nodeEncoded);

			switch (getNodeType(node)) {
			case BLANK:
				return "";
			case HASH:
				return hashToString(nodeEncoded);
			case LEAF:
				return String.format("[%s,%s]", NibbleString.from(node.getItem(0)).toString(),
						node.getItem(1).toStringUtf8());
			case EXTENSION:
				return String.format("[%s,%s]", NibbleString.from(node.getItem(0)).toString(),
						toString(node.getItem(1)));
			case BRANCH:
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (int i = 0; i < 16; i++) {
					sb.append(toString(node.getItem(i))).append(",");
				}
				if (!ByteString.EMPTY.equals(node.getItem(16)))
					sb.append(node.getItem(16).toStringUtf8());
				sb.append("]");
				return sb.toString();
			default:
				throw new AssertionError("Unrecognized Node Type");
			}
		} catch (InvalidProtocolBufferException e) {
			throw new AssertionError("Not possible");
		}
	}
}
