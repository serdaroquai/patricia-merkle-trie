package org.serdaroquai.pml;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.google.protobuf.ByteString;

public class MemoryStore implements Store {

	private Map<ByteString, ByteString> map = new HashMap<>();
	
	@Override
	public ByteString get(ByteString hash) { return map.get(hash); }

	@Override
	public void put(ByteString hash, ByteString encoded) { map.put(hash, encoded); }

	@Override
	public void dumpAll() {
		for (Entry<ByteString, ByteString> e : map.entrySet()) {
			System.out.println(String.format("%s: %s", 
					Common.hashToShortString(e.getKey()),
					Common.toString(e.getValue())));
		}	
	}
}
