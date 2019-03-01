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
	byte[] get(byte[] key);
	
	/**
	 * Updates or inserts the value associated with given key
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	boolean put(byte[] key, byte[] value);
	
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
