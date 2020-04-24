package ProjectManagement;


public class Project{
	static int id = 0;
	String name;
	int priority;
	int budget;
	int projectId;
	public Project(String name, int priority, int budget) {
		this.name = name;
		this.priority = priority;
		this.budget = budget;
		projectId = id++;
	}
	public int compareTo(Project otherProject) {
		if(this.priority != otherProject.priority) return this.priority - otherProject.priority;
		else return this.projectId - otherProject.projectId;
	}
}
