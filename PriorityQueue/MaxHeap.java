package PriorityQueue;
import java.util.List;
import java.util.ArrayList;

public class MaxHeap<T extends Comparable> implements PriorityQueueInterface<T> {

	ArrayList<ArrayList<T>> heap = new ArrayList<ArrayList<T>>(0);
	public static int left(int i) {
		return 2*i + 1;
	}
    public static int right(int i) {
    	return 2*i + 2;
    }
    
    public static int parent(int i) {
    	return (i-2)/2;
    }
    public MaxHeap() {
    	//root = new Node();
    }
    @Override
    public void insert(T element) {
    	//insert as in normaly into a heap. 
    	//System.out.println(heap.size());
    	//first we check whether that priority element already exists in there or not.
    	for(int i = 0; i < heap.size(); i++) {
    		if(element.compareTo(heap.get(i).get(0)) == 0){
    			//this means that, that priority already exists.
    			heap.get(i).add(element);
    			return;
    		}
    	}
    	//if that priority does not exist already
    	ArrayList<T> toBeAdded = new ArrayList<T>();
    	toBeAdded.add(element);
    	heap.add(toBeAdded);
    	int index = heap.size() - 1;
    	heapify(index);
    	//System.out.println(heap);
    }
    void heapify(int index) {
    	if(index <= 0) return;
    	if((heap.get(parent(index)).get(0)).compareTo(heap.get(index).get(0)) < 0) {
    		ArrayList<T> temp = heap.get(index);
    		heap.set(index, heap.get(parent(index)));
    		heap.set(parent(index), temp);
    	}
    	heapify(parent(index));
    }
    
    void swap(int x, int y) {
    	ArrayList<T> temp = heap.get(x);
    	heap.set(x, heap.get(y));
    	heap.set(y, temp);
    }
    void heapify2(int index) {
    	//for removing.
    	//checking if child exists
    	// if no child exists nothing to do
    	if(left(index) >= heap.size()) return;
    	else if(right(index) >= heap.size()) { //implies we have only one child.
    		if(heap.get(index).get(0).compareTo(heap.get(left(index)).get(0)) < 0) { //implies that there is a problem
    			/*
    			T temp = heap.get(index);
        		heap.add(index, heap.get(left(index)));
        		heap.add(left(index), temp); //swapped.*/
    			swap(index, left(index));
    			heapify(left(index));
    		}
    	}
    	else {
    		int nextIndex;
    		if(heap.get(right(index)).get(0).compareTo( heap.get(left(index)).get(0) ) > 0) nextIndex = right(index);
    		else nextIndex = left(index);
    		if(heap.get(index).get(0).compareTo(heap.get(nextIndex).get(0)) < 0){
    			swap(index, nextIndex);
    			heapify(nextIndex);
    		}
    	}
    }
    @Override
    public T extractMax() {
    	ArrayList<T> max = null;
    	T maxelement = null;
    	if(heap.size() > 0) {
    	max = heap.get(0);
    	}
    	if(max != null && max.size() > 0) {
    		maxelement = max.get(0);
    		max.remove(0);
    		//checking if there are still elements in the list
    		if(max.size() > 0) return maxelement;
    	}
    	if (maxelement == null) return null;
    	heap.set(0, heap.get(heap.size() - 1));
    	heap.remove(heap.size() - 1);
    	heapify2(0);
    	//System.out.println(heap);
        return maxelement;
    }
    
    public ArrayList<ArrayList<T>> heap() {
    	return heap;
    }

}