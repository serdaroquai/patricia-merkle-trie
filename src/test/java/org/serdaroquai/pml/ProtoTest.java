package org.serdaroquai.pml;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class ProtoTest {

	@Test
	public void testBranchNode() throws InvalidProtocolBufferException {
		// TODO mb a good idea to make a prototype empty branch node so that 
		// we can utilize TreeNode.newBuilder(prototype)?
		
		TreeNode.Builder b = NodeProto.TreeNode.newBuilder();
		
		for (int i=0; i<17; i++) b.addArray(ByteString.EMPTY);
		TreeNode n = b.build();
		ByteString bytes = n.toByteString();
		n = TreeNode.parseFrom(bytes);

		assertEquals(17, n.getArrayCount());
		assertEquals(ByteString.EMPTY, n.getArray(16));
	}
}
