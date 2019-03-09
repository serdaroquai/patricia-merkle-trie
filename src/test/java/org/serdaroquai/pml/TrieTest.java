package org.serdaroquai.pml;



import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.google.protobuf.ByteString;

public class TrieTest {
	
	Trie<String,String> t;
	Store s;
	
	@Before
	public void setup() {
		s = new MemoryStore();
		t = Trie.create(s);
	}
	
	@Test
	public void testRootHashEquality() {
		// same state = same root hash?
		t.put("do", "verb");
		t.put("dog", "puppy");
		t.put("doge", "coin");
		ByteString expected = t.put("horse", "stallion"); // expected roothash
		
		t.put("do", "no-verb");
		ByteString expected2 = t.put("dog", "no-puppy");
		t.put("doge", "no-coin");
		ByteString diffHash = t.put("horse", "no-stallion");
		
		t.put("doge", "coin");
		ByteString actual2 = t.put("horse", "stallion");
		t.put("do", "verb");
		ByteString actual = t.put("dog", "puppy");
		
		assertNotEquals(expected, diffHash);
		assertEquals(expected, actual);
		assertEquals(expected2, actual2);
	}
	
	@Test
	public void someRandomInsertions() {
		
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
		
//		(ba5d..e55a): [16,(0e07..52af)]
//		(0e07..52af): [,,,,(3540..1733),,,,[206f727365,stallion],,,,,,,,]
//		(3540..1733): [006f,(f86e..8547)]
//		(f86e..8547): [,,,,,,(9dd0..bef5),,,,,,,,,,verb]
//		(9dd0..bef5): [17,(c71b..2737)]
//		(c71b..2737): [,,,,,,[35,coin],,,,,,,,,,puppy]
		
		t.put("do", "verb");
		t.put("dog", "puppy");
		t.put("doge", "coin");
		t.put("horse", "stallion");
		
		assertEquals("verb", t.get("do"));
		assertEquals("puppy", t.get("dog"));
		assertEquals("coin", t.get("doge"));
		assertEquals("stallion", t.get("horse"));
		
	}
	
	@Test
	public void testUpdateBranchNodeExistingSlot() {
//		(bce6..0419): [006b6579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
//		---
//		(cba0..29e1): [006b6579,(0c33..b722)]
//		(0c33..b722): [,,,,,,(aa92..f5cd),,,,,,,,,,value]
//		(aa92..f5cd): [1c6f,(6cb2..2962)]
//		(6cb2..2962): [,,,,,,[3e67,newValue],[3265,someValue],,,,,,,,,]
		
		t.put("key", "value");
		ByteString rootHash = t.put("keylong", "newValue");
		ByteString newRootHash = t.put("keylore", "someValue");
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("value", t.get("key"));
		assertEquals("newValue", t.get("keylong"));
		assertEquals("someValue", t.get("keylore"));
		
	}
	
	@Test
	public void testUpdateBranchNodeNewSlot() {
//		(bce6..0419): [006b6579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
//		---
//		(cc0e..c865): [006b6579,(1aeb..aa54)]
//		(1aeb..aa54): [,,,,,,(c4ea..13cc),,,,,,,,,,value]
//		(c4ea..13cc): [,,,,,,,,,,,,[206f6e67,newValue],[206f7265,someValue],,,]
		
		t.put("key", "value");
		ByteString rootHash = t.put("keylong", "newValue");
		ByteString newRootHash = t.put("keymore", "someValue");
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("value", t.get("key"));
		assertEquals("newValue", t.get("keylong"));
		assertEquals("someValue", t.get("keymore"));
		
	}
	
	@Test
	public void testUpdateExtensionNodeWithRemainingKeySizeOne() {
//		(bce6..0419): [006b6579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
//		---
//		(cded..ab0f): [16b657,(65ae..11d9)]
//		(65ae..11d9): [,,,,,[20,someValue],,,,(2b41..e6be),,,,,,,]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
		
		t.put("key", "value");
		ByteString rootHash = t.put("keylong", "newValue");
		ByteString newRootHash = t.put("keu", "someValue"); // key <6b 65 79> keu <6b 65 75>
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("value", t.get("key"));
		assertEquals("newValue", t.get("keylong"));
		assertEquals("someValue", t.get("keu"));
		
	}
	
	@Test
	public void testUpdateExtensionNode() {
//		(bce6..0419): [006b6579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
//		---
//		(365a..55cb): [006b6579,(a24f..41bd)]
//		(a24f..41bd): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,someValue]
		
		t.put("key", "value");
		ByteString rootHash = t.put("keylong", "newValue");
		ByteString newRootHash = t.put("key", "someValue");
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("newValue", t.get("keylong"));
		assertEquals("someValue", t.get("key"));
		
	}
	
	@Test
	public void testUpdateExtensionNodeWithSubstringPath() {
//		(bce6..0419): [006b6579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
//		----
//		(a677..f634): [006b,(3895..f01e)]
//		(3895..f01e): [,,,,,,(c849..0f38),,,,,,,,,,someValue]
//		(c849..0f38): [1579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]
		
		t.put("key", "value");
		ByteString rootHash = t.put("keylong", "newValue");
		ByteString newRootHash = t.put("k", "someValue");
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("value", t.get("key"));
		assertEquals("newValue", t.get("keylong"));
		assertEquals("someValue", t.get("k"));
		
	}
	
	@Test
	public void testUpdateLeafNodeWithSupersetPath() {
//		(99ff..d771): [206b6579,value]
//		----
//		(bce6..0419): [006b6579,(2b41..e6be)]
//		(2b41..e6be): [,,,,,,[3c6f6e67,newValue],,,,,,,,,,value]

		ByteString rootHash = t.put("key", "value");
		assertEquals("value", t.get("key"));
		
		ByteString newRootHash = t.put("keylong", "newValue");
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("value", t.get("key"));
		assertEquals("newValue", t.get("keylong"));
		
	}
	
	@Test
	public void testUpdateLeafNodeWithSubstringPath() {
//		(99ff..d771): [206b6579,value]
//		----
//		(3b47..d929): [006b,(00d0..6d1b)]
//		(00d0..6d1b): [,,,,,,[3579,value],,,,,,,,,,newValue]


		ByteString rootHash = t.put("key", "value");
		assertEquals("value", t.get("key"));
		
		ByteString newRootHash = t.put("k", "newValue");
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		
		assertEquals("value", t.get("key"));
		assertEquals("newValue", t.get("k"));
		
	}
	
	@Test
	public void testUpdateLeafNode() {
//		(99ff..d771): [206b6579,value]
//		---
//		(8403..2b86): [206b6579,newValue]
		
		ByteString rootHash = t.put("key", "value");
		assertEquals("value", t.get("key"));
		
		ByteString newRootHash = t.put("key", "newValue");
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		assertNotEquals("value", t.get("key"));
		assertEquals("newValue", t.get("key"));
		
	}
	
	@Test
	public void testPutToBlankRoot() {
//		insert a leaf node into blank state
//		----
//		(99ff..d771): [206b6579,value]
		
		ByteString rootHash = t.getRootHash();
		ByteString newRootHash = t.put("key", "value");
		
		assertNotNull(newRootHash);
		assertNotEquals(rootHash, newRootHash);
		assertEquals("value", t.get("key"));
		
	}
	
	@Test
	public void testCreation() {
		t = Trie.create();
		assertNotNull(t);
		t.put("a", "va");
		assertEquals("va", t.get("a"));
	}

}
