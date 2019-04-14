package org.serdaroquai.pml;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MemoryStore implements Store {

	private Map<ByteBuffer, ByteBuffer> map = new HashMap<>();
	
	@Override
	public ByteBuffer get(ByteBuffer hash) { return map.get(hash); }

	@Override
	public void put(ByteBuffer hash, ByteBuffer encoded) { map.put(hash, encoded); }

	@Override
	public void dumpAll() {
		for (Entry<ByteBuffer, ByteBuffer> e : map.entrySet()) {
			System.out.println(String.format("%s: %s", 
					Common.hashToShortString(e.getKey()),
					Common.toString(e.getValue())));
		}	
	}
	
	@Override
	public boolean commit() { return true;}
	
	@Override
	public void rollback() {}
}
