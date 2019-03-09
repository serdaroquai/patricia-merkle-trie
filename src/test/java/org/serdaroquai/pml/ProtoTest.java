package org.serdaroquai.pml;



import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.serdaroquai.pml.NodeProto.TrieNode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoTest {

	@Test
	public void testBranchNode() throws InvalidProtocolBufferException {

		TrieNode n = TrieNode.newBuilder(Common.BRANCH_NODE_PROTOTYPE).build();
		ByteString bytes = n.toByteString();
		n = TrieNode.parseFrom(bytes);

		assertEquals(17, n.getItemCount());
		for (int i=0; i <n.getItemCount(); i++) {
			assertEquals(ByteString.EMPTY, n.getItem(i));			
		}
	}
}
