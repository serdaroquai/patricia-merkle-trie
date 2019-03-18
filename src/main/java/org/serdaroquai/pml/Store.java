package org.serdaroquai.pml;

import com.google.protobuf.ByteString;

public interface Store {

	ByteString get(ByteString hash);
	void put(ByteString hash, ByteString encoded);
	void dumpAll();
	boolean commit();
}
