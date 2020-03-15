package org.serdaroquai.pml;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.serdaroquai.pml.NodeProto.TrieNode;
import org.serdaroquai.pml.NodeProto.TrieNode.Builder;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class Common {

	private static final MessageDigest sha256digest;
	public static final TrieNode BRANCH_NODE_PROTOTYPE;
	public static final TrieNode EMPTY_NODE;
	public static final ByteBuffer EMPTY_NODE_BYTES;
	public static final ByteBuffer EMPTY;
	public static final NibbleString EMPTY_NIBBLE;
	
	static {
		try {
			sha256digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(e);
		}
		
		EMPTY_NODE = TrieNode.newBuilder().build();
		EMPTY_NODE_BYTES = sha256(ByteBuffer.wrap(EMPTY_NODE.toByteArray()));
		EMPTY = ByteBuffer.allocate(0);
		EMPTY_NIBBLE = NibbleString.from(EMPTY);
		
		// build a prototype branch node
		Builder newBuilder = TrieNode.newBuilder();
		for (int i = 0; i < 17; i++)
			newBuilder.addItem(ByteString.EMPTY);
		
		BRANCH_NODE_PROTOTYPE = newBuilder.build();
		
	}
	
	//TODO test me
	/**
	 * Converts a list of nibbles (each represented by a single byte) into a byte buffer
	 *
	 * @param nibbles a list of nibbles. Each byte represents a single nibble and is right aligned
	 * @return an unpacked byteBuffer of nibbles
	 */
	public static ByteBuffer toByteBuffer(List<Byte> nibbles) {
		int len = nibbles.size();
		byte[] bytes = new byte[len>>1];
		int w = 0, r = 0;
		while (r < len) {
			bytes[w++] = (byte) ((nibbles.get(r++) << 4) | nibbles.get(r++));
		}
		return ByteBuffer.wrap(bytes);
	}
	
	public static ByteBuffer sha256(ByteBuffer raw) {
		return ByteBuffer.wrap(sha256digest.digest(raw.array()));
	}

	public static NodeType getNodeType(TrieNode node) {
		if (EMPTY_NODE.equals(node))
			return NodeType.BLANK;

		switch (node.getItemCount()) {
		case 1:
			return NodeType.HASH;
		case 2:
			ByteBuffer key = node.getItem(0).asReadOnlyByteBuffer();
			return NibbleString.isTerminal(key) ? NodeType.LEAF : NodeType.EXTENSION;
		case 17:
			return NodeType.BRANCH;
		default:
			throw new AssertionError("Unrecognized encoded Node format");
		}
	}
	
	public static String hashToShortString(ByteBuffer hash) {
		NibbleString hashNibbles = NibbleString.from(hash);
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for (int i=0; i< 4; i++) sb.append(hashNibbles.nibbleAsChar(i));
		sb.append("..");
		for (int i=28; i< 32; i++) sb.append(hashNibbles.nibbleAsChar(i));
		sb.append(')');
		return sb.toString();
	}

	public static String toString(ByteBuffer nodeEncoded) {
		try {
			TrieNode node = TrieNode.parseFrom(nodeEncoded);

			switch (getNodeType(node)) {
			case BLANK:
				return "";
			case HASH:
				return hashToShortString(node.getItem(0).asReadOnlyByteBuffer());
			case LEAF:
				return String.format("[%s,%s]", NibbleString.from(node.getItem(0).asReadOnlyByteBuffer()).toString(),
						node.getItem(1).toStringUtf8());
			case EXTENSION:
				return String.format("[%s,%s]", NibbleString.from(node.getItem(0).asReadOnlyByteBuffer()).toString(),
						toString(node.getItem(1).asReadOnlyByteBuffer()));
			case BRANCH:
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (int i = 0; i < 16; i++) {
					sb.append(toString(node.getItem(i).asReadOnlyByteBuffer())).append(",");
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
