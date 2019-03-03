package org.serdaroquai.pml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;

public class TrieTest {

	@Test
	public void testPutToEmptyTrie() {
		Store store = new MemoryStore();
		
		ExtensionNode e = new ExtensionNode.Builder().withPartialKey("3").withValue("33yay").build();
		String eHash = BaseEncoding.base16().encode(Util.sha256(e));
		
		BranchNode _0 = new BranchNode.Builder()
				.withElement(0, "00yay")
				.withElement(1, "01yay")
				.withElement(2, "02yay").build();
		String _0Hash = BaseEncoding.base16().encode(Util.sha256(_0));
		
		BranchNode _1 = new BranchNode.Builder()
				.withElement(0, "10yay")
				.withElement(1, "11yay")
				.withElement(2, "12yay").build();
		String _1Hash = BaseEncoding.base16().encode(Util.sha256(_1));
		
		BranchNode _2 = new BranchNode.Builder()
				.withElement(0, "20yay")
				.withElement(1, "21yay")
				.withElement(2, "22yay").build();
		String _2Hash = BaseEncoding.base16().encode(Util.sha256(_2));
		
		BranchNode b = new BranchNode.Builder()
				.withElement(0, _0Hash)
				.withElement(1, _1Hash)
				.withElement(2, _2Hash)
				.withElement(3, eHash).build();
		
		store.store(_0);
		store.store(_1);
		store.store(_2);
		store.store(b);
		store.store(e);
		
		Trie t = new TrieImpl(store, Util.sha256(b));
		
		assertEquals("00yay", t.get(BaseEncoding.base16().decode("00")));
		assertEquals("01yay", t.get(BaseEncoding.base16().decode("01")));
		assertEquals("02yay", t.get(BaseEncoding.base16().decode("02")));
		assertEquals("10yay", t.get(BaseEncoding.base16().decode("10")));
		assertEquals("11yay", t.get(BaseEncoding.base16().decode("11")));
		assertEquals("12yay", t.get(BaseEncoding.base16().decode("12")));
		assertEquals("20yay", t.get(BaseEncoding.base16().decode("20")));
		assertEquals("21yay", t.get(BaseEncoding.base16().decode("21")));
		assertEquals("22yay", t.get(BaseEncoding.base16().decode("22")));
		
		assertNull(t.get(BaseEncoding.base16().decode("23")));
		
		assertEquals("33yay", t.get(BaseEncoding.base16().decode("33")));
		// need a good nibble iterator
		assertNull(t.get(ByteString.copyFrom("3", StandardCharsets.UTF_8).toByteArray()));
		
	}
}
