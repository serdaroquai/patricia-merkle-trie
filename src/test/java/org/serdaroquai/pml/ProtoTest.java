package org.serdaroquai.pml;


import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoTest {

	@Test
	public void testBranchNode() throws InvalidProtocolBufferException {
		// TODO mb a good idea to make a prototype empty branch node so that 
		// we can utilize TreeNode.newBuilder(prototype)?
		
		TreeNode.Builder b = NodeProto.TreeNode.newBuilder();
		
		for (int i=0; i<17; i++) b.addItem(ByteString.EMPTY);
		TreeNode n = b.build();
		ByteString bytes = n.toByteString();
		n = TreeNode.parseFrom(bytes);

		assertEquals(17, n.getItemCount());
		assertEquals(ByteString.EMPTY, n.getItem(16));
	}
}
