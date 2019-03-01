package org.serdaroquai.pml;

public interface Encoder<K, V> {

	byte[] encodeKey(K key);
	
	K decodeKey(byte[] key);
	
	byte[] encodeValue(V value);
	
	V decodeValue(V value);
}
