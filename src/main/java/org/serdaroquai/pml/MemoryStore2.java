package org.serdaroquai.pml;

import java.util.HashMap;
import java.util.Map;

import org.serdaroquai.pml.NodeProto.TreeNode;

import com.google.protobuf.ByteString;

public class MemoryStore2 implements Store2{

	Map<ByteString, TreeNode> map = new HashMap<>();
	
	@Override
	public TreeNode get(ByteString hash) {
		return map.get(hash);
	}
	
	@Override
	public ByteString put(TreeNode n) {
		byte[] bytes = n.toByteArray();
		byte[] hashBytes = Util.sha256(bytes); 
		ByteString hash = ByteString.copyFrom(hashBytes);
		map.put(hash, n);
		return hash;
	}

}
