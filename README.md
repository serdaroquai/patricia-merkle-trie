# patricia-merkle-trie
[![Build Status](https://travis-ci.com/serdaroquai/patricia-merkle-trie.svg?branch=master)](https://travis-ci.org/serdaroquai/patricia-merkle-trie)  

A java implementation of patricia-merkle-trie data structure. Uses `Protobuff` to encode TrieNodes and `sha256` to generate keys. 

### Installation
Add below dependency and repository to your pom.xml

```xml
  <dependency>
    <groupId>org.serdaroquai.pml</groupId>
    <artifactId>patricia-merkle-trie</artifactId>
    <version>0.0.2</version>
  </dependency>

//..
  
<repositories>
  <repository>
    <id>serdaroquai</id>
    <url>http://raw.github.com/serdaroquai/patricia-merkle-trie/repository/</url>
  </repository>
</repositories>
```



### Getting started
```java
Trie<String,String> trie = new Trie.TrieBuilder<String,String>()
				.keySerializer(Serializer.STRING_UTF8)
				.valueSerializer(Serializer.STRING_UTF8)
				.build(); // creates an in memory trie
        
t.put("do", "verb");
t.get("do") // returns "verb"
t.put("dog", "puppy"); 

// root hash returned by a put operation cryptographically represents the entire contents
ByteBuffer rootHash = t.put("doge", "coin"); // a 32 byte merkle root representing all state
ByteBuffer newRootHash = t.put("doge", "no-coin"); // rootHash != newRootHash

// you can access any past state by passing its root hash
t.get("doge"); // returns no-coin
t.get(rootHash, "doge") // returns coin

// build a map
t.toMap(); // returns a Map<String,String> of all key-value pairs
t.toMap(someOldRootHash); // also works for any past state
```
