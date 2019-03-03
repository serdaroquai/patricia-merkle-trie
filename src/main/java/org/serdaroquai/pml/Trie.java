package org.serdaroquai.pml;

/**
 * 
 * @author tr1b6162
 *
 */
public interface Trie {

	/**
	 * Returns the value associated with given key, null otherwise
	 * 
	 * @param key
	 * @return
	 */
	String get(byte[] key);
	
	/**
	 * Updates or inserts the value associated with given key
	 * 
	 * @param key
	 * @param value
	 * @returns new root hash
	 */
	byte[] put(byte[] key, String value);
	
	/**
	 * Removes the value associated with given key
	 * 
	 * @param key
	 * @return
	 */
	boolean remove(byte[] key);
	
	/**
	 * Returns 32 byte hash value associated with the root node
	 * 
	 * @return
	 */
	public byte[] getRootHash();
}
