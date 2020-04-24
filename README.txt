ReadMe File
Varun Desai. 2018EE10511.

**TRIES**
I will only explain the main ‘Trie’ class here because I feel that all other files are trivial to explain, they
do what the problem statement specifies directly.

Int height: stores the height of the trie.

Mapascii() is the function that I have used to map each ascii value specified in the range to a particular
array index in the children array.

Root: it is the root of the trie.

Delete(String key): searches for the key and deletes it. It actually calls an internal delete function which
finds the key recursively, and then deletes it. So for how it finds the key, refer to the explaination of the

Search(String Key) function. So once it searches the key, the problem that we have is that removing
the ‘end of leaf’ from the node doesen’t help much, because then things get messed up in the
printLevel() function. So we actually have to delete it. So it sets it that particular child to null. But this
particular word(that just got deleted) maybe the prefix of some other word. So if that is the case, we
simply removing the thing which says this is ‘a leaf’. If it is not a prefix, but a suffix, we delete all the
elements till we reach the suffix part, recursively. I sort of got this idea when reading about tries on
Geeksforgeeks.org, but the implementation is completely my own work after understanding the
algorithm.

Search(String Key): Search is similar to a BST. It keeps searching for next ascii character with help of
mapAscii() function until it reaches a null child or the word gets completed. False and True are the
search results respectively. It returns the particular TrieNode.
startsWith(String prefix): Basically searches for the prefix and returns that TrieNode.
printTrie(TrieNode trienNode): Prints all possible word combinations for that Node. Basically does
Depth First traversal on the TrieNode, which happens to be the lexicographical traversal order.
Insert(String Key): Finds the point where it should be inserted(analogous to search function) and then
inserts it. If is null then makes new child and marks It as leaf, if child is already there then simply marks
it as end of leaf.printLevel(): Prints a particular level of a trie. Basically it is sort of a breadth first traversal.

Print(): calls printLevel again and again.

RED BLACK TREES

Search(): Search is same as in any normal BST. No explanation needed

Insert(): I have implemented an easier version of RedBlackTrees, called Left-Leaning Red Black Trees,
about which read about on GeeksForGeeks.org. The reading was just for understanding of the
algorithm, implementation of it is completely my own job.
We insert the Node by default as a Red Node. And after each insertion we color the root node Black.
(just to avoid some unwanted errors).
So when we insert, there may be two cases, either we have a red parent or a black parent.
	If there is a black parent, then the tree still follows all properties. So we need to adjust nothing after
	insertion.
	If the parent is red, then we do need to adjust the tree again by performing some recolorings and some
	rotations.

We recursively call the function AdjustAfterInsertion again and again to check for peoblems until we
reach the root node. We may arrive at 3 cases:
	If a node has a right red child and left black child, this is not allowed in LLRB tree. So we left rotate the
node, and swap colors of node and its left child.
	If a node has left red child and left red grand child, obv we have a double red problem. To fix this, we
right rotate the node and swap colors of node and its right child.
	If a node has left red child and right red grandchild, then we invert colors of all nodes.
This is how we insert in a LLRB Tree.

PRIORITY QUEUE



	2. jobHeap: The priority queue MAXHEAP for jobs.
I have implemented MaxHeap with help of arrayLists.

Insert() inserts elemets at leaf and the calls heapify() (equivalent to bubble())

What heapify() does is basically compares priorities of child and parent and if rule is broken swaps
them. And then recurs it further up.

Delete removes element from root and then replaces it from leaf element, deletes leaf element and
callus bubble down on this. (Which recurs further down).

I feel that rest of the explanations are trivial.


SCHEDULE DRIVER

I have the following data structures here:
	1.ProjectTree: it is a *Trie*, which stores the name, and all other data for project. It is keyed on project name.

	2.jobHeap: the priority queue(MAXHEAP) or READY_QUEUE
	3. completedJobs: an Arraylist of all completed jobs, stores them in order of their completion time.
	4. incompleteJobs: an ArrayList of all incomplete jobs(jobs which were not completed due to budget insufficiency) or basically the NOT_READY QUEUE.
	5. users: an arraylist of all Users.

static int current_jobs: keeps track of number of jobs in the maxheap.
static int global_time: keeps track of global time.

RBTree user_heap: stores all jobs, keyed on userName.
RBTree project_heap: sotres all jobs, keyed on projectName.


>> timed_top_consumer: first of all it sorts all users(on the basis of what is given in the specs), by merge sort algorithms, with help of mergesort() and merge() functions. And then return a sublist of the arraylist( 0:<top>-1 ).

Its time complexity is O(nlog(n)), where n = total number of users. 

>> timed_flush: it creates a new maxheap, say temp. jobHeap.extractMax() is called repeatedly, untill it becomes empty. Checks if job's waitTime >= <WaitTime>. if budget is sufficient it is executed right away if not then it is added to temp. If waittime is less it is also added to temp. 
	in the end we set jobHeap = temp.

Time Complexity: Basically it calls jobHeap.extractMax() which is O(logn) and then compares, exectes, etc.. and then maybe adds is to temp, which is also O(log n). When computed it should turn out to be O(n log n).

>>handle_new_priority(): We have to return all incomplete jobs. So it first tries to find them in MAXHEAP (READY_QUEUE) with the help of the function recpriority(). And then traverses the arrayList of all incomplete jobs and checks if their priority is higher or not.

Time Complexity: 1. Traversing MAXHEAP: O(log n)
		 2. Traversing ArrayList: O(n).
so net is O(n).

>> handle_new_projectuser(): It search is project_heap, with the input project name, which return an ArrayList. Then the condition, >=t1, <= t2 is checked and job.username = input username. The correct arrayList is returned(WHICH IS ALSO SORTED IN THE CORRECT ORDER). 

Time complexity: 1.searching for project: O(1) {assuming the number of projects are considerably less than the number of jobs}.

2. traversing list: O(number of jobs of that project)
so its basically O(number of jobs of project)

>>handle_new_user() and handle_new_project():
	searches in project_heap/user_heap on the correct key(project user) traverses list and returns.
	Time Complexity: O(number of jobs of that user/project)

>>run to completion(): calls handle_empty_line until jobs exist in the READY_QUEUE.

>>print_stats(): 
	print stats of all jobs. First finished, in order of completion and then unfinished in the order of priority. I have used mergesort to sort the unfinished jobs and print them in correct order.

>>handle_job(): 
	checks if project and user exists and if true then creates job and inserts it into MAXHEAP. It is O(logn), n = number of jobs already existing.

>>handle_add():
	increases budget of given project, and transfers all the jobs in incompleteJobs list(NOT_READY QUEUE) to MAXHEAP(the READY_QUEUE). I think that the time complexity of this function is not easy to express. It depends on number of incomplete jobs of that project, the number of jobs already existing... but roughly maybe you can say that the T(N) = O(N log(N) ) where N = total number of jobs. But this is a very rough estimate.

>>handle_query():
	checks in MAXHEAP, incompleteJobs list and completed jobs list.

>>handle_user(): 
	creates user, ands it to the users arraylist.
	Time Complexity: O(1).

>>handle_project():
	creates project, adds to projectTree
	Time Complexity: O(log N), N = number of projects.

>>execute_a_job():
	if number of jobs in READY_QUEUE>0 executes job. calls jobHeap.extractMax(). If budget sufficient then execute if not insert into NOT_READY queue again and call execute_a_job() again.

All the timed function do basically the same thing, but without print statements.

	

    
