package pl.edu.pw.models;

import org.neo4j.ogm.annotation.*;

import java.util.Objects;

/**
 * this is magic
 */
@RelationshipEntity(type = "FRIENDS_WITH")
public class Friendship {
	public enum Status {
		AUTO_APPROVE, // automatically approve obligations between two users
		ACCEPTED,
		DECLINED,
		PENDING
	}
	@Id @GeneratedValue
	private Long id;
	@StartNode
	private User user1;
	@EndNode
	private User user2;
	private Status status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser1() {
		return user1;
	}

	public void setUser1(User user1) {
		this.user1 = user1;
	}

	public User getUser2() {
		return user2;
	}

	public void setUser2(User user2) {
		this.user2 = user2;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Friendship that)) return false;
		return Objects.equals(id, that.id) && Objects.equals(
				user1, that.user1) && Objects.equals(user2, that.user2) && status == that.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, user1, user2, status);
	}

	@Override
	public String toString() {
		return "Friendship{" +
				"id=" + id +
				", user1=" + user1 +
				", user2=" + user2 +
				", status=" + status +
				'}';
	}
}
