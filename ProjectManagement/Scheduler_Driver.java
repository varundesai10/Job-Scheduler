package ProjectManagement;


import java.io.*;
import java.net.URL;
import java.util.ArrayList;
//import java.util.Collections;

import PriorityQueue.MaxHeap;
import RedBlack.RBTree;
import Trie.Trie;
import Trie.TrieNode;

public class Scheduler_Driver extends Thread implements SchedulerInterface {
	
	private static final long MEGABYTE = 1024L * 1024L;

    public static long bytesToMegabytes(long bytes) {
        return bytes / MEGABYTE;
    }
    
	static ArrayList<Job> searchJobs(ArrayList<ArrayList<Job>> heap, String projectName, int p, int i){
		ArrayList<Job> toBeReturned = null;
	//	boolean found = false;
	//	int i = 0;
	
		if(heap.get(i) != null && heap.get(i).size() > 0 && p > heap.get(i).get(0).priority) {
				return null;
		}
		else if(heap.get(i) != null && heap.get(i).size() > 0 && p < heap.get(i).get(0).priority) {
			toBeReturned = searchJobs(heap, projectName, p, MaxHeap.left(i));
			if(toBeReturned == null) toBeReturned = searchJobs(heap, projectName, p, MaxHeap.right(i));
		}
		else if(heap.get(i) != null && heap.get(i).size() > 0 && p == heap.get(i).get(0).priority) {
			toBeReturned = heap.get(i);
		}
		return toBeReturned;
	}

	static Trie<Project> projectTree = new Trie<Project>(); /* projectTree is actaully a Trie! The naming is a bit confusing */
	static MaxHeap<Job> jobHeap = new MaxHeap<Job>();
	static ArrayList<User> users = new ArrayList<User>();
	
	static ArrayList<Job> completedJobs = new ArrayList<Job>();
	static ArrayList<Job> incompleteJobs = new ArrayList<Job>();
	
	//static RBTree<String, Job> jobRBTree = new RBTree<String, Job>();
	//static RBTree<String, Job> completedJobRBTree = new RBTree<String, Job>();
	
	static int current_Jobs = 0;
	static int global_time = 0;
	
	static RBTree<String, Job> user_heap = new RBTree<String, Job>(); //heap just for new_user query, things are only inserted here.
	static RBTree<String, Job> project_heap = new RBTree<String, Job>(); //heap just for new_project query, things are only inserted here.
	
    public static void main(String[] args) throws IOException {
//
        Scheduler_Driver scheduler_driver = new Scheduler_Driver();
        File file;
        if (args.length == 0) {
            URL url = Scheduler_Driver.class.getResource("INP");
            file = new File(url.getPath());
        } else {
            file = new File(args[0]);
        }

        scheduler_driver.execute(file);
    }

    public void execute(File commandFile) throws IOException {


        BufferedReader br = null;
        
        
        try {
            br = new BufferedReader(new FileReader(commandFile));

            String st;
            while ((st = br.readLine()) != null) {
                String[] cmd = st.split(" ");
                if (cmd.length == 0) {
                    System.err.println("Error parsing: " + st);
                    return;
                }
                String project_name, user_name;
                Integer start_time, end_time;

                long qstart_time, qend_time;

               switch (cmd[0]) {
                    case "PROJECT":
                        handle_project(cmd);
                        break;
                    case "JOB":
                        handle_job(cmd);
                        break;
                    case "USER":
                        handle_user(cmd[1]);
                        break;
                    case "QUERY":
                        handle_query(cmd[1]);
                        break;
                    case "": // HANDLE EMPTY LINE
                    	System.out.println("Running code");
                    	System.out.println("Remaining jobs: " + current_Jobs);
                        handle_empty_line();
                        System.out.println("Execution cycle completed");
                        //System.out.println(jobHeap.job());
                        break;
                    case "ADD":
                        handle_add(cmd);
                        break;
                    //--------- New Queries
                    case "NEW_PROJECT":
                    case "NEW_USER":
                    case "NEW_PROJECTUSER":
                    case "NEW_PRIORITY":
                        timed_report(cmd);
                        break;
                    case "NEW_TOP":
                        qstart_time = System.nanoTime();
                        timed_top_consumer(Integer.parseInt(cmd[1]));
                        qend_time = System.nanoTime();
                        System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                        break;
                    case "NEW_FLUSH":
                        qstart_time = System.nanoTime();
                        timed_flush( Integer.parseInt(cmd[1]));
                        qend_time = System.nanoTime();
                        System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                        break;
                    default:
                        System.err.println("Unknown command: " + cmd[0]);
                }

            }


            run_to_completion();
            print_stats(); 

        } catch (FileNotFoundException e) {
            System.err.println("Input file Not found. " + commandFile.getAbsolutePath());
        } catch (NullPointerException ne) {
            ne.printStackTrace();

        }
      
    }

    @Override
    public ArrayList<JobReport_> timed_report(String[] cmd) {
        long qstart_time, qend_time;
        ArrayList<JobReport_> res = null;
        switch (cmd[0]) {
            case "NEW_PROJECT":
                qstart_time = System.nanoTime();
                res = handle_new_project(cmd);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                break;
            case "NEW_USER":
                qstart_time = System.nanoTime();
                res = handle_new_user(cmd);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));

                break;
            case "NEW_PROJECTUSER":
                qstart_time = System.nanoTime();
                res = handle_new_projectuser(cmd);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                break;
            case "NEW_PRIORITY":
                qstart_time = System.nanoTime();
                res = handle_new_priority(cmd[1]);
                qend_time = System.nanoTime();
                System.out.println("Time elapsed (ns): " + (qend_time - qstart_time));
                break;
        }

        return res;
    }
    
    public static void sortUser() {
    	//mergersort.
    	mergesort(users, 0, users.size() - 1);
    }
    static void mergesort(ArrayList<User> toBeSorted, int left, int right){
    	int mid = (left + right)/2;
    	if(left >= right ) return;// do nothing!
    	else {
    		mergesort(toBeSorted, left, mid);
    		mergesort(toBeSorted, mid+1, right);
    		merge(toBeSorted, left, right);
    	}
    	//return merge(left, right);
    }
    
    static void merge(ArrayList<User> toBeSorted, int left, int right){
    	ArrayList<User> toBeReturned = new ArrayList<User>();
    	int mid = (left + right)/2;
    	int i = left; int j = mid + 1;
    	while(i <= mid && j <= right) {
    		if( toBeSorted.get(i).compareTo(toBeSorted.get(j)) <= 0){
    			toBeReturned.add(toBeSorted.get(i));
    			i++;
    		}
    		else {
    			toBeReturned.add(toBeSorted.get(j));
    			j++;
    		}
    	}
    	while(i <= mid) {
    		toBeReturned.add(toBeSorted.get(i));
    		i++;
    	}
    	while(j <= right) {
    		toBeReturned.add(toBeSorted.get(j));
    		j++;
    	}
    	for(int p = left; p <=right; p++) {
    		toBeSorted.set(p, toBeReturned.get(p - left));
    	}
    }
    @Override
    public ArrayList<UserReport_> timed_top_consumer(int top) {
    	//System.out.println("called at: " + global_time);
    	sortUser();
    	ArrayList toBeReturned = new ArrayList();
    	for(int i = 0; i < top; i++) {
    		if(i < users.size()) toBeReturned.add(users.get(i));
    	}
    	//System.out.println(toBeReturned);
        return (ArrayList) toBeReturned;
    }



    @Override
    public void timed_flush(int waittime) {
    	//we have to increase the priority of jobs having waittime greater than <WaitTime>.
    	MaxHeap<Job> newHeap = new MaxHeap<Job>();
    	
    	Job currentJob;
    	int myTime = global_time; //initializing with globlal time at start;
    	ArrayList list1 = new ArrayList(); //list of pushed jobs. 
    	//ArrayList list2 = new ArrayList();
    	while(true) {
    		currentJob = jobHeap.extractMax();
    		if(currentJob == null) break; //end loop if heap gets empty
    		if(myTime - currentJob.arrivalTime >= waittime) {
    			//executing Job
    			//currentJob.priority = 9999;
    			//looking for project
    			Project currentProject = (Project) projectTree.search(currentJob.project).getValue();
    			if(currentProject.budget < currentJob.runtime) {
    				//do nothing special.
    				
    				newHeap.insert(currentJob); //job wasn't executed
    			}
    			else { //this means job can be executed.
    				global_time += currentJob.runtime;
    				currentProject.budget -= currentJob.runtime;
    				currentJob.status = "COMPLETED";
    				completedJobs.add(currentJob);
    				list1.add(currentJob);
    				current_Jobs--;
    				for(int i = 0; i<users.size(); i++) {
    					User currentUser = users.get(i);
    					if(currentUser.name.equalsIgnoreCase(currentJob.user)) {
    						currentUser.consumed += currentJob.runtime;
    						break;
    					}
    				}
    				currentJob.completed_time = global_time;
    			}
    			
    		}
    		else {
    			newHeap.insert(currentJob);
    		}
    	}
    	/*System.out.println("Total pushed: " + list1.size());
    	for(int i = 0; i < list1.size(); i++) {
    		currentJob = (Job) list1.get(i);
    		System.out.println(currentJob.toString());
    	}*/
    	jobHeap = newHeap;
    	//what to print??!!
    }
    
    ArrayList<Job> recpriority(ArrayList<ArrayList<Job>> heap, int p, int i){
    	ArrayList<Job> list = new ArrayList<Job>();
    	if( heap.size() >= i || heap.get(i).size() <= 0 ) {
    		return null;
    	}
    	else if(heap.get(i).get(0).priority < p) {
    		return null;
    	}
    	else {
    		list.addAll(heap.get(i));
    		list.addAll(recpriority(heap, p, MaxHeap.left(i)));
    		list.addAll(recpriority(heap, p, MaxHeap.right(i)));
    	}
    	//System.out.println(list);
    	return list;
    }
    private ArrayList<JobReport_> handle_new_priority(String s) {
    	int p = Integer.parseInt(s);
    	ArrayList toBeReturned = recpriority(jobHeap.heap(), p, 0);
    	if(toBeReturned == null) toBeReturned = new ArrayList();
    	for(int i = 0; i < incompleteJobs.size(); i++) {
    		Job currentJob = incompleteJobs.get(i);
    		if(currentJob != null && currentJob.priority >= p) toBeReturned.add(currentJob);
    	}
    	//System.out.println(toBeReturned);
        return toBeReturned;
    }

    private ArrayList<JobReport_> handle_new_projectuser(String[] cmd) {
    	String projectName = cmd[1];
    	String userName = cmd[2];
    	int t1 = Integer.parseInt(cmd[3]) - 1;
    	int t2 = Integer.parseInt(cmd[4]) + 1;
    	ArrayList toBeReturned = new ArrayList();
    	/*Project random = (Project)projectTree.search(projectName).getValue();
    	int p = random.priority;
    	//we will search in maxHeap, completedRBtree, incompleteRBtree.
    	ArrayList<Job> list = searchJobs(jobHeap.heap(), projectName, p, 0);
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.project_name().equalsIgnoreCase(projectName) && currentJob.user.equalsIgnoreCase(userName)) {
    			toBeReturned.add(currentJob);
    		}
    	}
    	//searching in completed RBtree
    	list = (ArrayList) completedJobRBTree.search(projectName).getValues();
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.project_name().equalsIgnoreCase(projectName)&& currentJob.user.equalsIgnoreCase(userName)) {
    			toBeReturned.add(currentJob);
    		}
    	}
    	list = (ArrayList) jobRBTree.search(projectName).getValues();
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.project_name().equalsIgnoreCase(projectName)&& currentJob.user.equalsIgnoreCase(userName)) {
    			toBeReturned.add(currentJob);
    		}
    	}*/
    	ArrayList<Job> list = (ArrayList) project_heap.search(projectName).getValues();
    	ArrayList<Job> list2 = new ArrayList<Job>();
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if(currentJob.arrival_time() > t2) break;
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.user.equalsIgnoreCase(userName)) {
    			if(currentJob.status.equalsIgnoreCase("COMPLETED")) {
    			toBeReturned.add(currentJob);
    			//System.out.println(currentJob);
    			}
    			else {
    				list2.add(currentJob);
    			}
    		}
    	}
    	for(int i = 0; i < list2.size(); i++) {
    		toBeReturned.add(list2.get(i));
    		//System.out.println(list2.get(i));
    	}
    	//System.out.println(toBeReturned);
        return toBeReturned;
        
    }

    private ArrayList<JobReport_> handle_new_user(String[] cmd) {
        String userName = cmd[1];
        int t1 = Integer.parseInt(cmd[2]) - 1;
        int t2 = Integer.parseInt(cmd[3]) + 1;
        
        ArrayList toBeReturned = new ArrayList();
        ArrayList<Job> list = (ArrayList) user_heap.search(userName).getValues();
        for(int i = 0; i < list.size(); i++) {
        	Job currentJob = list.get(i);
        	if(currentJob.arrival_time() > t2) break; //as the list is FIFO
        	if(currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2) {
        		toBeReturned.add(currentJob);
        		//System.out.println(currentJob);
        	}
        	
        }
        //System.out.println(toBeReturned);
    	return toBeReturned;
    }

    private ArrayList<JobReport_> handle_new_project(String[] cmd) {
    	String projectName = cmd[1];
    	int t1 = Integer.parseInt(cmd[2]) - 1;
    	int t2 = Integer.parseInt(cmd[3]) + 1;
    	ArrayList toBeReturned = new ArrayList();
    	//Project random = (Project)projectTree.search(projectName).getValue();
    	//int p = random.priority;
    	//we will search in maxHeap, completedRBtree, incompleteRBtree.
    	/*ArrayList<Job> list = searchJobs(jobHeap.heap(), projectName, p, 0);
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.project_name().equalsIgnoreCase(projectName)) {
    			toBeReturned.add(currentJob);
    		}
    	}
    	//searching in completed RBtree
    	list = (ArrayList) completedJobRBTree.search(projectName).getValues();
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.project_name().equalsIgnoreCase(projectName)) {
    			toBeReturned.add(currentJob);
    		}
    	}
    	list = (ArrayList) jobRBTree.search(projectName).getValues();
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2 && currentJob.project_name().equalsIgnoreCase(projectName)) {
    			toBeReturned.add(currentJob);
    		}
    	}*/
    	ArrayList<Job> list = (ArrayList) project_heap.search(projectName).getValues();
    	for(int i = 0; i < list.size(); i++) {
    		Job currentJob = list.get(i);
    		if(currentJob.arrival_time() > t2) break;
    		if (currentJob.arrival_time() > t1 && currentJob.arrival_time() < t2) {
    			toBeReturned.add(currentJob);
    			//System.out.println(currentJob);
    		}
    	}
    	//System.out.println(toBeReturned);
        return toBeReturned;
    }




    public void schedule() {
            execute_a_job();
    }

    public void run_to_completion() {
    	if (current_Jobs>0) {
    		System.out.println("Running code");
    		System.out.println("Remaining jobs: " + current_Jobs);
    		handle_empty_line();
    		System.out.println("System execution completed");
    	}
    	else return;
    	run_to_completion();
    }
    
    static int compareJobs(Job job1, Job job2) {
    	if(job2.priority != job1.priority) {
    		return job1.priority - job2.priority;
    	}
    	else {
    		Project project1 = (Project) projectTree.search(job1.project).getValue();
    		Project project2 = (Project) projectTree.search(job2.project).getValue();
    		if(project1.projectId != project2.projectId) return project2.projectId - project1.projectId;
    		else return job2.id - job1.id;
    	}
    }
    static void mergesort2(ArrayList<Job> toBeSorted, int left, int right){
    	int mid = (left + right)/2;
    	if(left >= right ) return;// do nothing!
    	else {
    		mergesort2(toBeSorted, left, mid);
    		mergesort2(toBeSorted, mid+1, right);
    		merge2(toBeSorted, left, right);
    	}
    	//return merge(left, right);
    }
    
    static void merge2(ArrayList<Job> toBeSorted, int left, int right){
    	ArrayList<Job> toBeReturned = new ArrayList<Job>();
    	int mid = (left + right)/2;
    	int i = left; int j = mid + 1;
    	while(i <= mid && j <= right) {
    		if( compareJobs(toBeSorted.get(i),toBeSorted.get(j)) > 0){
    			toBeReturned.add(toBeSorted.get(i));
    			i++;
    		}
    		else {
    			toBeReturned.add(toBeSorted.get(j));
    			j++;
    		}
    	}
    	while(i <= mid) {
    		toBeReturned.add(toBeSorted.get(i));
    		i++;
    	}
    	while(j <= right) {
    		toBeReturned.add(toBeSorted.get(j));
    		j++;
    	}
    	for(int p = left; p <=right; p++) {
    		toBeSorted.set(p, toBeReturned.get(p - left));
    	}
    }

    public void print_stats() {
    	System.out.println("--------------STATS---------------");
    	System.out.println("Total jobs done: " + completedJobs.size());
    	for(int i = 0; i < completedJobs.size(); i++) {
    		System.out.println(completedJobs.get(i));
    	}
    	System.out.println("------------------------");
    	System.out.println("Unfinished jobs: ");
    	mergesort2(incompleteJobs, 0, incompleteJobs.size() - 1);
    	for(int i = 0; i < incompleteJobs.size(); i++) {
    		System.out.println(incompleteJobs.get(i));
    	}
    	System.out.println("Total unfinished jobs: " + incompleteJobs.size());
    	System.out.println("--------------STATS DONE---------------");
    }

    public void handle_add(String[] cmd) {
    	TrieNode<Project> node = projectTree.search(cmd[1]);
    	if(node == null) {
    		System.out.println(cmd[1] + ": NO SUCH PROJECT");
    		return;
    	}
    	else {
    		node.getValue().budget += Integer.parseInt(cmd[2]);
    		//p.budget = p.budget + Integer.parseInt(cmd[2]);
    		//projectTree.insert(p.name, p);
    		System.out.println("ADDING Budget");
    		//modifying current heap!
    		MaxHeap<Job> newHeap = new MaxHeap<Job>();
    		for(int i = 0; i < incompleteJobs.size(); i++) {
    			Job tempJob = incompleteJobs.get(i);
    			if(tempJob.project.equalsIgnoreCase(cmd[1])){
    				newHeap.insert(tempJob);
    				current_Jobs++;
    				incompleteJobs.remove(i);
    				i = i - 1;
    			}
    		}
    		Job tempJob;
    		while(true) {
    			tempJob = jobHeap.extractMax();
    			if(tempJob == null) break;
    			newHeap.insert(tempJob);
    		}
    		jobHeap = newHeap;
    	}
    	//ArrayList<Job> hey = (ArrayList) jobRBTree.search(cmd[1]).getValues();
    	//while(hey.size() > 0) hey.remove(0);
    }

    public void handle_empty_line() {
       schedule();
    }


    public void handle_query(String key) {
    	System.out.println("Querying");
    	ArrayList<ArrayList<Job>> list2 = new ArrayList<ArrayList<Job>>(jobHeap.heap());
    	ArrayList<Job> list = new ArrayList<Job>();
    	for(int i = 0; i < list2.size(); i++) {
    		list.addAll(list2.get(i));
    	}
    	list.addAll(incompleteJobs);
    	list.addAll(completedJobs);
    	Job tempJob = null;
    	for(int i = 0; i < list.size(); i++) {
    		tempJob = (Job) list.get(i);
    		if(tempJob.name.equalsIgnoreCase(key)) break;
    	}
    	if(tempJob != null && tempJob.name.equalsIgnoreCase(key)) {
    		if(tempJob.status.equalsIgnoreCase("REQUESTED")) {
    		System.out.println(tempJob.name + ": NOT FINISHED");
    		}
    		else if(tempJob.status.equalsIgnoreCase("COMPLETED")) {
    			System.out.println(tempJob.name + ": COMPLETED");
    		}
    	}
    	else System.out.println("Doesnotexists: NO SUCH JOB");
    }

    public void handle_user(String name) {
    	System.out.println("Creating user");
    	User newUser = new User(name);
    	users.add(newUser);
    }
    
    boolean containsUser(ArrayList<User> list, String s) {
    	boolean contains = false;
    	for(int i = 0; i < list.size(); i++) {
    		User currentUser = list.get(i);
    		if(currentUser.name.equalsIgnoreCase(s)) {
    			contains = true;
    			break;
    		}
    	}
    	return contains;
    }
    
    public void handle_job(String[] cmd) {
    	System.out.println("Creating job");
    	int p;
    	TrieNode<Project> random = projectTree.search(cmd[2]);
    	if(random == null) {
    		System.out.println("No such project exists. " + cmd[2]);
    		return;
    	}
    	else {
    		Project random2 = (Project) random.getValue();
    		p = random2.priority;
    	}
    	if(!containsUser(users, cmd[3])) {
    		System.out.println("No such user exists: " + cmd[3]);
    		return;
    	}
    	Job newJob = new Job(cmd[1], cmd[2], cmd[3], Integer.parseInt(cmd[4]), p, global_time);
    	jobHeap.insert(newJob);
    	user_heap.insert(newJob.user, newJob);
    	project_heap.insert(newJob.project, newJob);
    	current_Jobs++;
    	//System.out.println(jobHeap.heap());
    }

    public void handle_project(String[] cmd) {
    	System.out.println("Creating project");
    	Project newProject = new Project(cmd[1], Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]));
    	projectTree.insert(newProject.name, newProject);
    	//created projected and inserted in my trie.
    }

    public void execute_a_job() {
    	if(current_Jobs <= 0) return;
    	Job currentJob = jobHeap.extractMax();
    	//System.out.println(jobHeap.heap());
    	current_Jobs--;
    	
    	System.out.println("Executing: " + currentJob.name + " from: " + currentJob.project);
    	
    	Project currentProject = (Project) projectTree.search(currentJob.project).getValue();
    	
    	if(currentJob.runtime<=currentProject.budget) {
    		currentProject.budget -= currentJob.runtime;
    		System.out.println("Project: " + currentProject.name + " budget remaining: " + currentProject.budget);
    		currentJob.status = "COMPLETED";
    		global_time += currentJob.runtime;
    		currentJob.completed_time = global_time;
    		completedJobs.add(currentJob);
    		
    		//TrieNode<User> random = userTrie.search(currentJob.user);
    		//random.getValue().consumed += currentJob.runtime;
    		
    		for(int i = 0; i < users.size(); i++) {
    			User random = users.get(i);
    			if (random.name.equalsIgnoreCase(currentJob.user)) {
    				random.consumed += currentJob.runtime;
    				random.latestJob = global_time;
    			}
    		}
    		
    		//completedJobRBTree.insert(currentJob.project, currentJob); THIS IS NOT NECESSARY ANYMORE!
    	}
    	else {
    		System.out.println("Un-sufficient budget.");
    		incompleteJobs.add(currentJob);
    		handle_empty_line();
    		//jobRBTree.insert(currentJob.project, currentJob); ALSO NOT NECESSARY ANYMORE!
    	}
    }
    
    public void timed_handle_user(String name){
    	//System.out.println("Creating user");
    	User newUser = new User(name);
    	users.add(newUser);
    }
    public void timed_handle_job(String[] cmd){
    	//System.out.println("Creating job");
    	int p;
    	TrieNode<Project> random = projectTree.search(cmd[2]);
    	if(random == null) {
    		//System.out.println("No such project exists. " + cmd[2]);
    		return;
    	}
    	else {
    		Project random2 = (Project) random.getValue();
    		p = random2.priority;
    	}
    	if(!containsUser(users, cmd[3])) {
    		//System.out.println("No such user exists: " + cmd[3]);
    		return;
    	}
    	Job newJob = new Job(cmd[1], cmd[2], cmd[3], Integer.parseInt(cmd[4]), p, global_time);
    	jobHeap.insert(newJob);
    	user_heap.insert(newJob.user, newJob);
    	project_heap.insert(newJob.project, newJob);
    	current_Jobs++;
    }
    public void timed_handle_project(String[] cmd){
    	//System.out.println("Creating project");
    	Project newProject = new Project(cmd[1], Integer.parseInt(cmd[2]), Integer.parseInt(cmd[3]));
    	projectTree.insert(newProject.name, newProject);
    }
    public void timed_run_to_completion(){
    	if (current_Jobs>0) {
    		//System.out.println("Running code");
    		//System.out.println("Remaining jobs: " + current_Jobs);
    		timed_handle_empty_line();
    		//System.out.println("System execution completed");
    	}
    	else return;
    	timed_run_to_completion();
    }
    
    public void timed_handle_empty_line() {
    	if(current_Jobs <= 0) return;
    	Job currentJob = jobHeap.extractMax();
    	//System.out.println(jobHeap.heap());
    	current_Jobs--;
    	
    	//System.out.println("Executing: " + currentJob.name + " from: " + currentJob.project);
    	
    	Project currentProject = (Project) projectTree.search(currentJob.project).getValue();
    	
    	if(currentJob.runtime<=currentProject.budget) {
    		currentProject.budget -= currentJob.runtime;
    		//System.out.println("Project: " + currentProject.name + " budget remaining: " + currentProject.budget);
    		currentJob.status = "COMPLETED";
    		global_time += currentJob.runtime;
    		currentJob.completed_time = global_time;
    		completedJobs.add(currentJob);
    		
    		//TrieNode<User> random = userTrie.search(currentJob.user);
    		//random.getValue().consumed += currentJob.runtime;
    		
    		for(int i = 0; i < users.size(); i++) {
    			User random = users.get(i);
    			if (random.name.equalsIgnoreCase(currentJob.user)) {
    				random.consumed += currentJob.runtime;
    				random.latestJob = global_time;
    			}
    		}
    		
    		//completedJobRBTree.insert(currentJob.project, currentJob); THIS IS NOT NECESSARY ANYMORE!
    	}
    	else {
    		//System.out.println("Un-sufficient budget.");
    		incompleteJobs.add(currentJob);
    		timed_handle_empty_line();
    		//jobRBTree.insert(currentJob.project, currentJob); ALSO NOT NECESSARY ANYMORE!
    	}
    }
}
