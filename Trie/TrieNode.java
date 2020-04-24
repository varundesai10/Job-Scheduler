package Trie;


import Util.NodeInterface;


public class TrieNode<T> implements NodeInterface<T> {

    static final int ALPHABET_SIZE = 126-32+1;
   // boolean isPartofWord = false;
    String key;
    T value;
    boolean isLeaf;
    TrieNode<T>[] children;
    int level;
    
    public TrieNode(T value) {
    	this.value = value;
    	children = new TrieNode[ALPHABET_SIZE];
    	for(int i = 0; i<ALPHABET_SIZE;i++) {
    		children[i] = null;
    	}
    	isLeaf = false;
    	key = "";
    }
    
    public TrieNode(T value, int level) {
    	this.value = value;
    	this.level = level;
    	children = new TrieNode[ALPHABET_SIZE];
    	for(int i = 0; i<ALPHABET_SIZE;i++) {
    		children[i] = null;
    	}
    	key = "";
    }
    public TrieNode(T value, String key) {
    	this.value = value;
    	this.key = key;
    	this.level = key.length();
    	children = new TrieNode[ALPHABET_SIZE];
    	for(int i = 0; i<ALPHABET_SIZE;i++) {
    		children[i] = null;
    	}
    }
    
    
    
    @Override
    public T getValue() {
        return value;
    }
    
    public String getKey() {
    	return key;
    }

}