package ProjectManagement;

public class User implements Comparable<User>, UserReport_ {

	String name;
	public int consumed;
	public int latestJob;
	public User(String name) {
		this.name = name;
	}
    @Override
    public int compareTo(User user) {
    	if(this.consumed != user.consumed)
    		return -1*(this.consumed - user.consumed);
    	else return -1*(this.latestJob - user.latestJob);
    }
    
    public int consumed() {
    	return consumed;
    }
    public String toString() {
    	return name + " " + consumed + " " + latestJob;
    }
}
