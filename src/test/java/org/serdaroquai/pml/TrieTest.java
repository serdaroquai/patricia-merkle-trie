package org.serdaroquai.pml;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.serdaroquai.pml.NodeProto.TrieNode;

import com.google.protobuf.ByteString;

/*
do <64 6f> : 'verb'
dog <64 6f 67> : 'puppy'
doge <64 6f 67 65> : 'coin'
horse <68 6f 72 73 65> : 'stallion'

rootHash: [ <16>, hashA ]
hashA:    [ <>, <>, <>, <>, hashB, <>, <>, <>, hashC, <>, <>, <>, <>, <>, <>, <>, <> ]
hashC:    [ <20 6f 72 73 65>, 'stallion' ]
hashB:    [ <00 6f>, hashD ]
hashD:    [ <>, <>, <>, <>, <>, <>, hashE, <>, <>, <>, <>, <>, <>, <>, <>, <>, 'verb' ]
hashE:    [ <17>, hashF ]
hashF:    [ <>, <>, <>, <>, <>, <>, hashG, <>, <>, <>, <>, <>, <>, <>, <>, <>, 'puppy' ]
hashG:    [ <35>, 'coin' ]
*/
public class TrieTest {
	
	TrieImpl t;
	Store s;
	
	@BeforeEach
	public void setup() {
		s = new MemoryStore();
		t = new TrieImpl(s);
	}
	
	@Test
//	@Disabled
	public void testUpdateBranch() {

		t.update("d", "vd");
		t.update("e", "ve");
		t.update("p", "vp");

		s.dumpAll();
		
		assertEquals("vd", t.get("d"));
		assertEquals("ve", t.get("e"));
		assertEquals("vp", t.get("p"));
		
	}

	@Test
	@Disabled
	public void testUpdateKV() {
		// root = []
		t.update("d", "vd");
		System.out.println("---");
		// root 	= [<2064>, "vd"]
		t.update("e", "ve");
		System.out.println("---");
		// root 	= [<16>, hashA]
		// hashA	= [,,,,hashB,hashC,,,,,]
		// hashB	= [<20>, "vd"]
		// hashC	= [<20>, "ve"]
		assertEquals("vd", t.get("d"));
		assertEquals("ve", t.get("e"));
	}
	
	@Test
	@Disabled
	public void testUpdateNodeBlank() {
		t.update("key", "value");
		assertEquals("value", t.get("key"));
	}
	
	@Test
	@Disabled
	public void testUpdateTrie() {
		t.update("do","verb");
		System.out.println("---");
		t.update("dog","puppy");
		System.out.println("---");
		t.update("doge","coin");
		System.out.println("---");
		t.update("horse","stallion");
		System.out.println("---");
		
		assertEquals("verb", t.get("do"));
		assertEquals("puppy", t.get("dog"));
		assertEquals("coin", t.get("doge"));
		assertEquals("stallion", t.get("horse"));
	}
	
	@Test
	@Disabled
	public void testGet() {
		
		TrieNode.Builder builder = TrieNode.newBuilder();
		for (int i=0; i<17; i++) builder.addItem(ByteString.EMPTY);
		final TrieNode branchPrototype = builder.build();
		
		Store store = new MemoryStore();
		TrieNode g = TrieNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x35}))
				.addItem(ByteString.copyFrom("coin".getBytes()))
				.build();
		ByteString hashG = store.put(g);
		
		TrieNode f = TrieNode.newBuilder(branchPrototype)
				.setItem(6, hashG)
				.setItem(16, ByteString.copyFrom("puppy".getBytes()))
				.build();
		ByteString hashF = store.put(f);
		
		TrieNode e = TrieNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x17}))
				.addItem(hashF)
				.build();
		ByteString hashE = store.put(e);
		
		TrieNode d = TrieNode.newBuilder(branchPrototype)
				.setItem(6, hashE)
				.setItem(16, ByteString.copyFrom("verb".getBytes()))
				.build();
		ByteString hashD = store.put(d);
		
		TrieNode b = TrieNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0x6f}))
				.addItem(hashD)
				.build();
		ByteString hashB = store.put(b);
		
		TrieNode c = TrieNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {
						(byte) 0x20, (byte) 0x6f, (byte) 0x72, (byte) 0x73, (byte) 0x65}))
				.addItem(ByteString.copyFrom("stallion".getBytes()))
				.build();
		ByteString hashC = store.put(c);
		
		TrieNode a = TrieNode.newBuilder(branchPrototype)
				.setItem(4, hashB)
				.setItem(8, hashC)
				.build();
		ByteString hashA = store.put(a);
		
		TrieNode root = TrieNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x16}))
				.addItem(hashA)
				.build();
		ByteString rootHash = store.put(root);
		
		TrieImpl trie = new TrieImpl(store);
		
		assertEquals("verb", trie.get(rootHash, ByteString.copyFromUtf8("do")).toStringUtf8());
		assertEquals("puppy", trie.get(rootHash, ByteString.copyFromUtf8("dog")).toStringUtf8());
		assertEquals("coin", trie.get(rootHash, ByteString.copyFromUtf8("doge")).toStringUtf8());
		assertEquals("stallion", trie.get(rootHash, ByteString.copyFromUtf8("horse")).toStringUtf8());
		assertEquals(ByteString.EMPTY, trie.get(rootHash, ByteString.copyFromUtf8("not exists")));
		
	}
}
