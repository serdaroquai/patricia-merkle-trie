# patricia-merkle-trie

A java implementation of patricia-merkle-trie data structure. Uses `Protobuff` to encode TrieNodes and `sha256` to generate keys. 

### QuickStart:
```java
Trie<String,String> trie = Trie.create(); // Creates a Trie<String,String> backed by a hashmap.
t.put("do", "verb");
t.get("do") // returns "verb"
t.put("dog", "puppy");

ByteString rootHash = t.put("doge", "coin"); // a 34 byte merkle root representing all state
ByteString newRootHash = t.put("horse", "stallion"); // rootHash != newRootHash
```
