package pl.edu.pw.models;

import ch.qos.logback.classic.joran.sanity.IfNestedWithinSecondPhaseElementSC;
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
	private User sender;
	@EndNode
	private User receiver;
	private Status status;

	public Friendship(User sender, User receiver, Status status){
		this.sender=sender;
		this.receiver=receiver;
		this.status=status;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getSender() {
		return sender;
	}

	public void setSender(User sender) {
		this.sender = sender;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setUser2(User receiver) {
		this.receiver = Friendship.this.receiver;
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
				sender, that.sender) && Objects.equals(receiver, that.receiver) && status == that.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, sender, receiver, status);
	}

	@Override
	public String toString() {
		return "Friendship{" +
				"id=" + id +
			//	", sender=" + sender +
			//	", receiver=" + receiver +
				", status=" + status +
				'}';
	}
}
