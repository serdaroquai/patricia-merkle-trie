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
	
	static {
        try {
            sha256digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't happen", e); 
        }
        
        Builder newBuilder = TrieNode.newBuilder();
        for (int i=0; i<17; i++) newBuilder.addItem(ByteString.EMPTY);
        BRANCH_NODE_PROTOTYPE  = newBuilder.build();
    }
	
	public static ByteString sha256(ByteString raw) {
		return ByteString.copyFrom(sha256digest.digest(raw.toByteArray()));
	}
	
	public static byte[] sha256(byte[] bytes) {
		return sha256digest.digest(bytes);
	}
	
	public static NodeType getNodeType(TrieNode node) {
		if (EMPTY_NODE.equals(node)) return NodeType.BLANK;
		
		switch(node.getItemCount()) {
		case 1: 
			return NodeType.HASH;
		case 2: 
			ByteString key = node.getItem(0);
			return isTerminal(key) ? NodeType.LEAF : NodeType.EXTENSION;
		case 17: 
			return NodeType.BRANCH;
		default:
			throw new AssertionError("Unrecognized encoded Node format");
		}
	}
	
	public static boolean isTerminal(ByteString bs) {
		return (bs.byteAt(0) & NibbleString.TERMINAL) == NibbleString.TERMINAL;
	}
	
	public static String toString(ByteString nodeEncoded) {
		try {
			TrieNode node = TrieNode.parseFrom(nodeEncoded);
		
			switch(getNodeType(node)) {
			case BLANK:
				return "[]";
			case HASH:
				return NibbleString.from(node.getItem(0)).toString();
			case LEAF:
				return String.format("[%s,%s]", 
						NibbleString.from(node.getItem(0)).toString(),
						node.getItem(1).toStringUtf8()
						);
			case EXTENSION:
				return String.format("[%s,%s]", 
						NibbleString.from(node.getItem(0)).toString(),
						toString(node.getItem(1))
						);
			case BRANCH:
				StringBuilder sb = new StringBuilder();
				sb.append("[");
				for (int i=0; i<16; i++) {
					sb.append(toString(node.getItem(i))).append(",");
				}
				if (!ByteString.EMPTY.equals(node.getItem(16))) sb.append(node.getItem(16).toStringUtf8());
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
