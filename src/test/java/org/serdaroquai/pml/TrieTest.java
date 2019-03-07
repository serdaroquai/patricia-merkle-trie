package org.serdaroquai.pml;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.serdaroquai.pml.NodeProto.TreeNode;
import org.serdaroquai.pml.NodeProto.TreeNode.Builder;

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
		ByteString rootHash = store.put(root);
		
		TrieImpl2 trie = new TrieImpl2(store);
		
		assertEquals("verb", trie.get(rootHash, ByteString.copyFromUtf8("do")).toStringUtf8());
		assertEquals("puppy", trie.get(rootHash, ByteString.copyFromUtf8("dog")).toStringUtf8());
		assertEquals("coin", trie.get(rootHash, ByteString.copyFromUtf8("doge")).toStringUtf8());
		assertEquals("stallion", trie.get(rootHash, ByteString.copyFromUtf8("horse")).toStringUtf8());
		assertEquals(ByteString.EMPTY, trie.get(rootHash, ByteString.copyFromUtf8("not exists")));
		
	}
}
