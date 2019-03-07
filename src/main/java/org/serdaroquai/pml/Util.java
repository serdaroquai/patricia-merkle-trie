package org.serdaroquai.pml;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.google.protobuf.ByteString;

public class Util {

	private static final MessageDigest sha256digest;
	
	static {
        try {
            sha256digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Can't happen", e); 
        }
    }
	
	public static ByteString sha256(ByteString raw) {
		return ByteString.copyFrom(sha256digest.digest(raw.toByteArray()));
	}
	
	public static byte[] sha256(byte[] bytes) {
		return sha256digest.digest(bytes);
	}
}
