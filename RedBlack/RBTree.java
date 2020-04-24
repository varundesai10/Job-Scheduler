package RedBlack;


public class RBTree<T extends Comparable, E> implements RBTreeInterface<T, E>  {

	RedBlackNode<T,E> root;
	
    @Override
    public void insert(T key, E value) {
    	//insert as if you would normaly insert.
    	
    	root =  internalInsert(root, key, value);
    	root.color = 'B';
    	rightRotate(null);
    }
    
    public RBTree() {
    }
    
    RedBlackNode<T,E> internalInsert(RedBlackNode<T,E> myNode,T key,E value){
    	if(myNode == null) {
    	return new RedBlackNode<T,E>(key, value);
    	}
    	else if(key.compareTo(myNode.getKey()) < 0) {
    		myNode.left = internalInsert(myNode.left, key, value);
    	}
    	else if(key.compareTo(myNode.getKey()) > 0){
    		myNode.right = internalInsert(myNode.right, key, value);
    	}
    	else if(key.equals(myNode.getKey())) {
    		myNode.values.add(value);
    		return myNode;
    	}
    	
    	// now we check for cases.
    	/*
    	if((myNode.right != null && myNode.right.color == 'R') && (myNode.left == null || myNode.left.color == 'B')) {
    		myNode = rightRotate(myNode);
    		//Swapping colors!
    		char temp = myNode.color;
    		myNode.color = myNode.left.color;
    		myNode.left.color = temp;
    	}
    	
    	if(myNode.left.color == 'R' && (myNode.right != null && myNode.right.color == 'R')) {
    		if(myNode.color == 'B') {
    			myNode.color = 'R';
    		}
    		else myNode.color = 'B';
    		myNode.left.color = 'B';
    		myNode.right.color = 'B';
    	}
    	
    	if( (myNode.left != null && myNode.left.color == 'R') && (myNode.left != null && myNode.left.left != null && myNode.left.left.color == 'R')) {
    		myNode = rightRotate(myNode);
    		char temp = myNode.color;
    		myNode.color = myNode.right.color;
    		myNode.right.color = temp;
    	} 
    	return myNode;
    	*/
    	myNode = adjustAfterInsertion(myNode);
    	return myNode;
    }
    
    RedBlackNode<T,E> adjustAfterInsertion(RedBlackNode<T,E> myNode){
    	//right red child, left black child.
    	if(color(myNode.right) == 'R' && color(myNode.left) == 'B') {
    		myNode = leftRotate(myNode);
    		rightRotate(null);
    		//Swapping colors!
    		if(myNode.left != null) {
    		char temp = myNode.color;
    		myNode.color = myNode.left.color;
    		myNode.left.color = temp;
    		}
    	}
    	//double red child
    	if((color(myNode.right) == 'R') && (color(myNode.left)) == 'R') {
    		if(myNode.color == 'B') {
    			myNode.color = 'R';
    		}
    		else myNode.color = 'B';
    		myNode.left.color = 'B';
    		myNode.right.color = 'B';
    	}
    	//left red child and left red grand child
    	if( color(myNode.left) == 'R' && (myNode.left != null) && color(myNode.left.left) == 'R') {
    		myNode = rightRotate(myNode);
    		leftRotate(null);
    		char temp = myNode.color;
    		myNode.color = myNode.right.color;
    		myNode.right.color = temp;
    	} 
    	return myNode;
    }
    
    char color(RedBlackNode node) {
    	if(node == null) return 'B';
    	else return node.color;
    }
    RedBlackNode<T,E> rightRotate(RedBlackNode<T,E> myNode){
    	if(myNode == null) {
    		return null;
    	}
    	if(myNode.left != null) {
    	RedBlackNode<T,E> x = myNode.left;
    	RedBlackNode<T,E> temp = x.right;
    	//myNode.left = x.right;
    	
    	x.right = myNode;
    	myNode.left = temp;
    	return x;
    	}
    	else return myNode;
    }
    
    RedBlackNode<T,E> leftRotate(RedBlackNode<T,E> myNode){
    	if(myNode == null) {
    		return null;
    	}
    	if(myNode.right != null) {
    	RedBlackNode<T,E> x = myNode.right;
    	RedBlackNode<T,E> temp = x.left;
    	x.left = myNode;
    	myNode.right = temp;
    	return x;
    	}
    	else return myNode;
    }
       
    @Override
    public RedBlackNode<T, E> search(T key) {
        if(root == null) return new RedBlackNode<T,E>();
        return internalSearch(root, key);
    }
    
    RedBlackNode<T,E> internalSearch(RedBlackNode<T,E> node,T key){
    	if(node.key.equals(key)) {
    		return node;
    	}
    	else if(key.compareTo(node.key) < 0) {
    		if(node.left != null) return internalSearch(node.left, key);
    		else return new RedBlackNode<T,E>();
    	}
    	else {
    		if(node.right != null) return internalSearch(node.right, key);
    		else return new RedBlackNode<T,E>();
    	}
    }
    RedBlackNode<T,E> uncle(RedBlackNode<T,E> node){
    	RedBlackNode<T,E> g = node.parent.parent;
    	if(node.parent.key.compareTo(g.key) < 0) {
    		//implies that parent is left child. so we return right.
    		return g.right;
    	}
    	else return g.left;
    }
}