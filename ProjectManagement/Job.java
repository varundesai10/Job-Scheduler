package ProjectManagement;

public class Job implements Comparable<Job>, JobReport_ {
	static int current_id = 0;
	String name;
	String project;
	String user;
	int runtime;
	int priority;
	int arrivalTime;
    String status;
    Integer completed_time = null;
    int id;
    
    public Job(String name, String project, String user, int runtime, int priority) {
    	this.name = name;
    	this.project = project;
    	this.user = user;
    	this.runtime = runtime;
    	this.priority = priority;
      	status = "REQUESTED";
    }
    public Job(String name, String project, String user, int runtime, int priority, int arrivalTime) {
    	this.name = name;
    	this.project = project;
    	this.user = user;
    	this.runtime = runtime;
    	this.priority = priority;
    	this.arrivalTime = arrivalTime;
      	status = "REQUESTED";
      	id = current_id;
      	current_id++;
    }
    @Override
    
    public int compareTo(Job job) {
        return this.priority - job.priority;
    }
    
    public String toString(){
    	//Job{id=23, user='Harry', project='IITD.CS.OS.ASPLOS', jobstatus=COMPLETED, execution_time=10, end_time=150, priority=9, name='Pipeline5'}
    	return "Job{user='" + user + "', project='" + project + "', jobstatus=" + status + ", execution_time=" + runtime + ", end_time=" + completed_time + ", name='" + name + "'}";
    	//return "Job{id="+id+", user='" + user + "', project='" + project + "', jobstatus=" + status + ", execution_time=" + runtime + ", end_time=" + completed_time +", priority="+ priority+ ", name='" + name + "'}";                          
    }
    
    public String user() {
    	return user;
    }
    
    public String project_name() {
    	return project;
    }
    public int budget(){
    	return runtime;
    }
    public int arrival_time() {
    	return arrivalTime;
    }
    public int completion_time() {
    	return completed_time;
    }
}