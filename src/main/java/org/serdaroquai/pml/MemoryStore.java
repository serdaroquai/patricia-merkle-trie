package org.serdaroquai.pml;

import java.util.HashMap;
import java.util.Map;

import com.google.protobuf.ByteString;

public class MemoryStore implements Store{
	
	Map<ByteString, Node> map = new HashMap<>();

	@Override
	public Node get(byte[] hash) {
		return map.get(ByteString.copyFrom(hash));
	}

	@Override
	public void store(Node n) {
		map.put(ByteString.copyFrom(Util.sha256(n)), n);
	}

}
