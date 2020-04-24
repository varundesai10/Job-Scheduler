package RedBlack;

import Util.RBNodeInterface;

import java.util.List;
import java.util.ArrayList;

public class RedBlackNode<T extends Comparable, E> implements RBNodeInterface<E> {
	T key;
	ArrayList<E> values;
	RedBlackNode<T,E> left;
	RedBlackNode<T,E> right;
	RedBlackNode<T,E> parent;
	char color = 'R';
	
	public RedBlackNode() {
		key = null;
		values = null;
		left = null; right = null;
		color = 'R';
	}
	
	public RedBlackNode(T key) {
		this.key = key;
		values = null;
		left = null; right = null;
		color = 'R';
		parent = null;
	}
	
	public RedBlackNode(T key, E value) {
		this.key = key;
		values = new ArrayList<E>();
		values.add(value);
		left = null; right = null;
		color = 'R';
		parent = null;
	}
	
	public RedBlackNode(T key, E value, RedBlackNode<T,E> parent) {
		this.key = key;
		values = new ArrayList<E>();
		values.add(value);
		left = null; right = null;
		color = 'R';
		this.parent = parent;
	}
	
	public RedBlackNode(T key, E value, char color) {
		this.key = key;
		values = new ArrayList<E>();
		values.add(value);
		left = null; right = null;
		this.color = color;
	}
	
    @Override
    public E getValue() {
        return values.get(0);
    }

    @Override
    public List<E> getValues() {
        return values;
    }
    void setColor(char c) {
    	color = c;
    }
    void reColor() {
    	if(color == 'B') color = 'R';
    	else color = 'B';
    }
    public T getKey() {
    	return key;
    }
}
