package pl.edu.pw.models;


import jdk.jfr.Name;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.pw.DBConnector;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@NodeEntity
public class User{
	@Id	@GeneratedValue
	private Long id;

	private String name;
	private String passwordHash;
	@Relationship(type = "OWES", direction = Relationship.Direction.OUTGOING)
	private List<Obligation> owes;
	@Relationship(type = "OWES", direction = Relationship.Direction.INCOMING)
	private List<Obligation> isOwed;
	@Relationship(type = "FRIENDS_WITH", direction = Relationship.Direction.UNDIRECTED)
	private List<Friendship> friendsWith;

	public User() {
	}

	public User(String name, String passwordHash) {
		this.name = name;
		this.passwordHash = passwordHash;
		DBConnector dbc = new DBConnector();
		dbc.addUser(this);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public List<Obligation> getOwes() {
		return owes;
	}

	public void setOwes(List<Obligation> owes) {
		this.owes = owes;
	}

	public List<Obligation> getIsOwed() {
		return isOwed;
	}

	public void setIsOwed(List<Obligation> isOwed) {
		this.isOwed = isOwed;
	}

	public List<Friendship> getFriendsWith() {
		return friendsWith;
	}

	public void setFriendsWith(List<Friendship> friendsWith) {
		this.friendsWith = friendsWith;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof User user)) return false;
		return Objects.equals(id, user.id) && Objects.equals(name, user.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name);
	}

	@Override
	public String toString() {
		return "User{" +
				"id=" + id +
				", name='" + name + '\'' +
				", passwordHash='" + passwordHash + '\'' +
				", owes=" + owes +
				", isOwed=" + isOwed +
				", friendsWith=" + friendsWith +
				'}';
	}

	/**
	 * request another user to pay you back specified amount
	 * @param user user to request obligation from
	 * @param amount amount to request
	 * @param description short note about the obligation
	 * @param timestamp when the obligation was requested
	 */

	public void requestObligationFrom(User user, Double amount, String description, LocalDateTime timestamp) {
		new Obligation(this, user, amount, Obligation.Status.PENDING, description, timestamp);
	}

	/**
	 * accept obligation from another user
	 * @param user user who sent the obligation request
	 * @param id id of the obligation
	 */
	public void acceptObligationTo(User user, Long id) {

	}

	/**
	 *
	 * @param user
	 * @param id
	 */
	public void payObligationTo(User user, Long id) {

	}
	/**
	 * Follow the graph to find the end users who owe you money
	 * (that is, following who owes money to people who owe you money)
	 * @return a map with users as keys and the amounts they need to pay you as values
	 */
	public Map<User,Double> findFinalDebtors() {
		return null;
	}

	/**
	 * Follow the graph to find the end users who you owe money
	 * (that is, following who people you owe money to owe money to)
	 * @return a map with users as keys and the amounts you need to pay them as values
	 */
	public Map<User,Double> findFinalCreditors() {
		return null;
	}
}
