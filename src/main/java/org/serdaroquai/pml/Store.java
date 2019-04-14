package org.serdaroquai.pml;

import java.nio.ByteBuffer;

public interface Store {

	/**
	 * Returns bytes associated with given 32 byte key
	 * 
	 * @param hash
	 * @return
	 */
	ByteBuffer get(ByteBuffer hash);
	
	/**
	 * Store given bytes along with its representative 32 byte key
	 * 
	 * @param hash
	 * @param encoded
	 */
	void put(ByteBuffer hash, ByteBuffer encoded);
	
	/*
	 * TODO remove this debug purposes only 
	 */
	void dumpAll();
	
	/**
	 * Persists all nodes all at once transactionally to the underlying medium.
	 * Has no effect in memory stores
	 * 
	 * @return true in case of success
	 */
	boolean commit();
	
	/**
	 * rollback is caled if commit retuns false and is responsible to
	 * discard changes
	 * 
	 */
	void rollback();
}
