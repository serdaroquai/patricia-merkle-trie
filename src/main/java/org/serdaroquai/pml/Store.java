package org.serdaroquai.pml;

import java.nio.ByteBuffer;

public interface Store {

	ByteBuffer get(ByteBuffer hash);
	void put(ByteBuffer hash, ByteBuffer encoded);
	void dumpAll();
	boolean commit();
}
