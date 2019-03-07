package org.serdaroquai.pml;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.serdaroquai.pml.NodeProto.TreeNode;
import org.serdaroquai.pml.NodeProto.TreeNode.Builder;

import com.google.common.io.BaseEncoding;
import com.google.protobuf.ByteString;

public class TrieTest {

	
	@Test
	public void testGet() {
		
		
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
		
		Builder builder = TreeNode.newBuilder();
		for (int i=0; i<17; i++) builder.addItem(ByteString.EMPTY);
		final TreeNode branchPrototype = builder.build();
		
		Store2 store = new MemoryStore2();
		TreeNode g = TreeNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x35}))
				.addItem(ByteString.copyFrom("coin".getBytes()))
				.build();
		ByteString hashG = store.put(g);
		
		TreeNode f = TreeNode.newBuilder(branchPrototype)
				.setItem(6, hashG)
				.setItem(16, ByteString.copyFrom("puppy".getBytes()))
				.build();
		ByteString hashF = store.put(f);
		
		TreeNode e = TreeNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x17}))
				.addItem(hashF)
				.build();
		ByteString hashE = store.put(e);
		
		TreeNode d = TreeNode.newBuilder(branchPrototype)
				.setItem(6, hashE)
				.setItem(16, ByteString.copyFrom("verb".getBytes()))
				.build();
		ByteString hashD = store.put(d);
		
		TreeNode b = TreeNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x00, (byte) 0x6f}))
				.addItem(hashD)
				.build();
		ByteString hashB = store.put(b);
		
		TreeNode c = TreeNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {
						(byte) 0x20, (byte) 0x6f, (byte) 0x72, (byte) 0x73, (byte) 0x65}))
				.addItem(ByteString.copyFrom("stallion".getBytes()))
				.build();
		ByteString hashC = store.put(c);
		
		TreeNode a = TreeNode.newBuilder(branchPrototype)
				.setItem(4, hashB)
				.setItem(8, hashC)
				.build();
		ByteString hashA = store.put(a);
		
		TreeNode root = TreeNode.newBuilder()
				.addItem(ByteString.copyFrom(new byte[] {(byte) 0x16}))
				.addItem(hashA)
				.build();
		ByteString hashRoot = store.put(root);
		
		TrieImpl2 trie = new TrieImpl2(store);
		
		assertEquals("verb", trie.get(root, ByteString.copyFromUtf8("do")).toStringUtf8());
		assertEquals("puppy", trie.get(root, ByteString.copyFromUtf8("dog")).toStringUtf8());
		assertEquals("coin", trie.get(root, ByteString.copyFromUtf8("doge")).toStringUtf8());
		assertEquals("stallion", trie.get(root, ByteString.copyFromUtf8("horse")).toStringUtf8());
		assertEquals(ByteString.EMPTY, trie.get(root, ByteString.copyFromUtf8("not exists")));
		
	}
	
	@Test
	public void testSomething() {
//		"9abc".chars().forEach(System.out::println);
//		byte[] bytes = "abcd".getBytes(StandardCharsets.UTF_8);
//		System.out.println(BaseEncoding.base16().encode(bytes));
//		Store store = new MemoryStore();
//		TrieImpl t = new TrieImpl(store, Util.sha256(Node.EMPTY_NODE));
//		t.get(Node.EMPTY_NODE, "abc");
	}
	
	@Test
	@Disabled
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
