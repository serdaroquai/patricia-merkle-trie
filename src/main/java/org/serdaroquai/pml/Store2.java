package org.serdaroquai.pml;

import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;

public interface Store2 {

	TreeNode get(ByteString hash);
}
