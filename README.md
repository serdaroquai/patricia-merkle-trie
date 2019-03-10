# patricia-merkle-trie
[![Build Status](https://travis-ci.com/serdaroquai/patricia-merkle-trie.svg?branch=master)](https://travis-ci.org/serdaroquai/patricia-merkle-trie)  

A java implementation of patricia-merkle-trie data structure. Uses `Protobuff` to encode TrieNodes and `sha256` to generate keys. 

### QuickStart:
```java
Trie<String,String> trie = Trie.create(); // Creates a Trie<String,String> backed by a hashmap.
t.put("do", "verb");
t.get("do") // returns "verb"
t.put("dog", "puppy");

// root hash returned by a put operation cryptographically represents the entire contents
ByteString rootHash = t.put("doge", "coin"); // a 32 byte merkle root representing all state
ByteString newRootHash = t.put("doge", "no-coin"); // rootHash != newRootHash

// you can query any past state by passing its root hash
t.get("doge"); // returns no-coin
t.get(rootHash, "doge") // returns coin

// build a map
t.toMap(); // returns a Map<String,String> of all key-value pairs
t.toMap(someOldRootHash); // a map of the past
```
