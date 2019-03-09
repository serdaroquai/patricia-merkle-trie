package org.serdaroquai.pml;

import org.serdaroquai.pml.NodeProto.TrieNode;

import com.google.protobuf.ByteString;

public interface Store {

	ByteString get(ByteString hash);
	@Deprecated
	ByteString put(TrieNode node);
	void put(ByteString hash, ByteString encoded);
	
	void dumpAll();
}
