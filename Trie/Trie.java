	package Trie;

public class Trie<T> implements TrieInterface {
	int height;
	static int mapAscii(char c) {
		/*if(!Character.isAlphabetic(c)) {
			if(c == ' ') {
				return 52;
			}
			return -1;
		}
		else if(c <= 'Z') {
			return (int) (c - 'A');
		}
		else return ( (int) (c-'a') + 26);*/
		return (int) c - 32;
	}
	
	TrieNode<T> root;
   
	public Trie() {
	   root = new TrieNode<T>(null, 0);
	   height = 0;
   }
   
	@Override
   public boolean delete(String word){
		/*char searchChar = ' ';
        int i = 0;
        boolean found = true;
        TrieNode<T> toBeReturned = root;
        while(i < word.length() && found) {
        	searchChar = word.charAt(i);
        	int index = mapAscii(searchChar);
        	if(index >= 0 && toBeReturned.children[index] != null) {
        		toBeReturned = toBeReturned.children[index];
        	}
        	else if(index >= 0){
        		found = false;
        	}
        	i = i + 1;
        }
        if(found && toBeReturned.isLeaf) {
        	toBeReturned.isLeaf = false;
        	System.out.println("DELETED");
        	return true;
        	
        }
        System.out.println("ERROR DELETING");
		return false; */
		if(this.search(word) == null) {
			//System.out.println("ERROR DELETING");
			return false;
		}
		else {
		boolean hello = recursiveDelete(root, word);
			//System.out.println("DELETED");
			return true;
		}
		
    } 
	
	
	boolean isEmpty(TrieNode trieNode) {
		boolean p = true;
		for(int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
			if(trieNode.children[i] != null) {p = false; break; }
		}
		return p;
	}
	
	boolean recursiveDelete(TrieNode trieNode, String searchKey) {
		if(trieNode.getKey().length() == searchKey.length()) {
			//now we know that this is the node that we want to delete.
			if(isEmpty(trieNode)) {
				trieNode = null;
				return true;
			}
			else {
				trieNode.isLeaf = false;
				return false;
					}
			}
		else {
			boolean deletedChild = recursiveDelete(trieNode.children[mapAscii(searchKey.charAt(trieNode.getKey().length()))], searchKey);
			if(!deletedChild) {
				return false;
			}
			else {
				trieNode.children[mapAscii(searchKey.charAt(trieNode.getKey().length()))] = null;
				if(isEmpty(trieNode)) {
					if(!trieNode.isLeaf) {
						trieNode = null;
						return true;
					}
				}
			}
		}
		return false;
	}

    @Override
    public TrieNode search(String word) {
        
    	char searchChar = ' ';
        int i = 0;
        boolean found = true;
        TrieNode<T> toBeReturned = root;
        while(i < word.length() && found) {
        	searchChar = word.charAt(i);
        	int index = mapAscii(searchChar);
        	if(index >= 0 && toBeReturned.children[index] != null) {
        		toBeReturned = toBeReturned.children[index];
        	}
        	else if(index >= 0){
        		found = false;
        	}
        	i = i + 1;
        }
        if(found && toBeReturned.isLeaf) {
        	return toBeReturned;
        }
    	return null;
    
    }

    @Override
    public TrieNode startsWith(String prefix) {
        char searchChar = ' ';
        int i = 0;
        boolean found = true;
        TrieNode<T> toBeReturned = root;
        while(i < prefix.length() && found) {
        	searchChar = prefix.charAt(i);
        	int index = mapAscii(searchChar);
        	if(index >= 0 && toBeReturned.children[index] != null) {
        		toBeReturned = toBeReturned.children[index];
        	}
        	else if(index >= 0) {
        		found = false;
        	}
        	i = i + 1;
        }
        if(found) return toBeReturned;
    	return null;
    }

    @Override
    public void printTrie(TrieNode trieNode) {
    	/*String output = "";
    	output = recursivePrintTrie(trieNode, output);
    	System.out.println(output);*/
    	if (trieNode.isLeaf) {
    		System.out.println(trieNode.getValue().toString());
    	}
    	for(int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
    		if(trieNode.children[i] != null) {
    			printTrie(trieNode.children[i]);
    		}
    	}
    }
    
    String recursivePrintTrie(TrieNode trieNode, String s) {
    	s = returnKey(trieNode, s);
    	for(int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
    		if(trieNode.children[i] != null) {
    			s = recursivePrintTrie(trieNode.children[i],s);
    		}
    	}
    	return s;
    }
    String returnKey(TrieNode trieNode, String s) {
    	if (trieNode.isLeaf) {
    		s = s + " " + trieNode.key;
    	}
    	return s;
    }

    @Override
    public boolean insert(String word, Object value) {
    	
    	int i = 0;
    	TrieNode<T> traverser = root;
    	String insertingKey = "";
    	while(i<word.length()) {
    		//System.out.println("o");
    		int index = mapAscii(word.charAt(i));
    		if(index >= 0) {
    		insertingKey = insertingKey + word.charAt(i); //if it is space we dont add it to the key because it doesn't count
    		if(traverser.children[index] == null) {
    			traverser.children[index] = new TrieNode<T>(null,insertingKey);
    		}
    		//traverser.isPartofWord = true;
    		traverser = traverser.children[index];
    		}
    		i = i + 1;
    	}
    	
    	if(traverser.isLeaf == false) {
    	traverser.isLeaf = true;
    	traverser.value = (T) value;
    	return true;
    	}
    	else return false;
    }

    @Override
    public void printLevel(int level) {
    	String s = "";
    	s = recursivePrintLevel(root, level,s);
    	s = returnFormatted(s);
    	System.out.println("Level " + level + ": "+ s);
    }
    
    String returnFormatted(String s) {
    	String x = "";
    	Character[] c = new Character[s.length()];
    	for(int i =0; i<s.length(); i++) {
    		c[i] = s.charAt(i);
    	}
    	//sorting Array
    	for(int i = 0; i < c.length; i++) {
    		for(int j = i+1; j < c.length;j++ ) {
    			if(c[j] < c[i]) {
    				char temp;
    				temp = c[j];
    				c[j] = c[i];
    				c[i] = temp;
    			}
    		}
    	}
    	//c sorted.
    	boolean insertedFirst = false;
    	for(int i = 0; i<c.length; i++) {
    		if(c[i] != ' ') {if(!insertedFirst) {x = x + c[i]; insertedFirst = true;}
    		else x = x + "," + c[i];}
    	}
    	return x;
    }
    String recursivePrintLevel(TrieNode trieNode,int level, String s) {
    	if(trieNode.getKey().length() == level) {
    		//if(trieNode.getKey().charAt(level-1) != ' ') {
    		//if(s!="") {
    		//s = s + "," + trieNode.getKey().charAt(level-1);
    		//}
    		//else s = s + trieNode.getKey().charAt(level-1);
    		//}
    		s = s + trieNode.getKey().charAt(level - 1);
    	}
    	else if(trieNode.getKey().length() < level) {
    		for(int i = 0; i < TrieNode.ALPHABET_SIZE; i++) {
    			if(trieNode.children[i] != null) {
    				s = recursivePrintLevel(trieNode.children[i], level, s);
    			}
    		}
    	}
    	
    	return s;
    }

    @Override
    public void print() {
    	System.out.println("-------------");
    	System.out.println("Printing Trie");
    	int level = 1;
    	
    	while(true) {
    		String s = "";
    		s = recursivePrintLevel(root, level, s);
    		if(s == "") break;
    		else System.out.println("Level " + level + ": "+ returnFormatted(s));
    		level = level + 1;
    	}
    	System.out.println("Level " + level + ": ");
    	System.out.println("-------------");
    }
}