package pl.edu.pw.models;

/**
 * this is magic
 */
public class Friendship {
	private final User user1;
	private final User user2;
	private boolean autoAccept;

	public Friendship(User user1, User user2, boolean autoAccept) {
		this.user1 = user1;
		this.user2 = user2;
		this.autoAccept = autoAccept;
	}

}
