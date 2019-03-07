package org.serdaroquai.pml;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.serdaroquai.pml.NodeProto.TreeNode;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;

public class Util {

	public static final ObjectMapper mapper = new ObjectMapper();
	private static final JsonFactory jsonFactory = new JsonFactory();
	
	private static final MessageDigest sha256digest;
	
	static {
        try {
            sha256digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't happen", e); 
        }
    }
	
	public static byte[] sha256(Node n) {
		return sha256digest.digest(n.getHashableString().getBytes(StandardCharsets.UTF_8));
	}
	
	public static ByteString sha256(ByteString raw) {
		return ByteString.copyFrom(sha256digest.digest(raw.toByteArray()));
	}
	
	public static byte[] sha256(TreeNode n) {
		return sha256digest.digest(n.toByteArray());
	}
	
	public static byte[] sha256(byte[] bytes) {
		return sha256digest.digest(bytes);
	}
	
	public static String serialize(Object o){
		try {
			return mapper.writeValueAsString(o);
		} catch (JsonProcessingException e) {
			throw new IllegalStateException("Should never happen", e);
		}
	}
	
	public static Node deserialize(String nodeString) {
		try (JsonParser p = jsonFactory.createParser(nodeString)) {
			p.nextToken();
			if (p.isExpectedStartObjectToken()) {
				p.nextToken(); // k || endObject
				if (p.currentToken() == JsonToken.END_OBJECT) {
					return Node.EMPTY_NODE;
				}
				
				ExtensionNode.Builder b = new ExtensionNode.Builder();
				b.withPartialKey(p.nextTextValue());
				p.nextToken(); // v
				b.withValue(p.nextTextValue());
				return b.build();
			}
			
			if (p.isExpectedStartArrayToken()) {
				BranchNode.Builder b = new BranchNode.Builder();
				for (int i=0; i<17; i++) {
					b.withElement(i, p.nextTextValue());
				}
				return b.build();
			}
				
		} catch (Exception e) {}
		throw new IllegalStateException("Should never happen!");
	}
	
}
