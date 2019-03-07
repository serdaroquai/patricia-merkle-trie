package org.serdaroquai.pml;

import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;

public interface Store2 {

	ByteString get(ByteString hash);
	@Deprecated
	ByteString put(TreeNode node);
	void put(ByteString hash, ByteString encoded);
}
