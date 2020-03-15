package org.serdaroquai.pml;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static org.serdaroquai.pml.Common.getNodeType;
import static org.serdaroquai.pml.Common.toByteBuffer;

/**
 * Pair of path, TrieNode. Used as a convenience object for level traversaling a root node.
 * Comparator is based on path only, and equals and hashCode are not implemented.
 */
public class Pair implements Comparable<Pair>{
    public final static Pair DUMMY = new Pair(null, null);

    final List<Byte> path;
    final NodeProto.TrieNode node;

    public Pair(List<Byte> path, NodeProto.TrieNode value) {
        this.path = path == null ? Collections.emptyList() : path;
        this.node = value == null ? Common.EMPTY_NODE : value;
    }

    @Override
    public int compareTo(Pair o) {
        if ((this == DUMMY) != (o == DUMMY)) return this == DUMMY ? 1 : -1; // DUMMY is bigger then everything (simulates largest key)
        if (this.path.size() != o.path.size())
            return this.path.size() - o.path.size();

        int l = this.path.size(), compare = 0;
        for (int i=0; i<l && compare == 0; i++) {
            compare = this.path.get(i).compareTo(o.path.get(i));
        }
        return compare;
    }

    @Override
    public String toString() {
        return String.format("[%s,%s]", NibbleString.from(toByteBuffer(path)).toString(), getNodeType(node));
    }
}
