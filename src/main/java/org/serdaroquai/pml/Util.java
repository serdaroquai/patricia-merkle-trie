package org.serdaroquai.pml;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.serdaroquai.pml.NodeProto.TrieNode;
import org.serdaroquai.pml.NodeProto.TrieNode.Builder;

import com.google.protobuf.ByteString;

public class Util {

	private static final MessageDigest sha256digest;
	public static final TrieNode BRANCH_NODE_PROTOTYPE;
	
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

	public static String toString(TrieNode node) {
		if (node == TrieImpl.EMPTY_NODE) return "EMPTY_NODE";
		return node.toString();
	}
}
