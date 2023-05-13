package pl.edu.pw.models;


import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@NodeEntity
public class User{
	@Id
	@GeneratedValue
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

	public User(String name, String password) {
		this.name = name;
		Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32,64,1,15*1024,2);
		this.passwordHash = encoder.encode(password); // Password Hashing
		this.owes = new ArrayList<>();
		this.isOwed = new ArrayList<>();
		this.friendsWith = new ArrayList<>();
	}

	public boolean passwordCompare(String pass1, String pass2) {
		Argon2PasswordEncoder encoder = new Argon2PasswordEncoder(32,64,1,15*1024,2);
		return encoder.matches(pass1, pass2);
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
		return this.friendsWith.stream()
				.filter(friendship -> (friendship.getStatus() == Friendship.Status.ACCEPTED || friendship.getStatus() == Friendship.Status.AUTO_APPROVE))
				.collect(Collectors.toList());
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
				", friendsWith=" + friendsWith +
				'}';
	}

	/**
	 * request another user to pay you back specified amount
	 * @param user user to request obligation from
	 * @param amount amount to request
	 */

	public Optional<Obligation> requestObligationFrom(User user, Double amount) {
		Optional<Friendship> f = this.friendsWith.stream()
				.filter(friendship -> friendship.getSender().equals(user))
				.filter(friendship -> friendship.getReceiver().equals(user))
				.findFirst();
		try {
			if (f.isPresent()) {
				switch (f.get().getStatus()) {
					case ACCEPTED:
						return Optional.of(new Obligation(this, user, amount, Obligation.Status.PENDING));
					case AUTO_APPROVE:
						return Optional.of(new Obligation(this, user, amount, Obligation.Status.ACCEPTED));
					case PENDING:
					case DECLINED: {
						throw new IllegalArgumentException();        //NotInAFriendshipException
					}
				}
			} else throw new IllegalArgumentException();
		} catch (IllegalArgumentException e) {
			System.out.println("Not in a friendship with " + user + " !");
		}
		return Optional.empty();
	}

	/**
	 * accept obligation from another user
	 * @param user user who sent the obligation request
	 * @param id id of the obligation
	 */
	public void acceptObligationTo(User user, Long id) {
		for (Obligation obligation: this.owes
		) {
			if(obligation.getCreditor().equals(user) && obligation.getId()==id) obligation.accept();
		}
	}

	/**
	 *
	 * @param user
	 */
	public void payObligationTo(User user) {
		for (Obligation obligation: this.owes
		) {
			if(obligation.getCreditor().equals(user)) obligation.pay();
		}
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

	public void addOwed(Obligation obligation){
		this.isOwed.add(obligation);
	}

	public void addOwes(Obligation obligation){
		this.owes.add(obligation);
	}

	public Optional<Friendship> sendOrAcceptFriendship(User user){
		try {
			Optional<Friendship> f = this.friendsWith.stream()
					.filter(friendship -> friendship.getSender().equals(user))
					.findFirst();
			if (f.isPresent()) {
				switch (f.get().getStatus()) {
					case ACCEPTED:
					case AUTO_APPROVE:
						throw new IllegalStateException();        //AlreadyAcceptedException
					case PENDING:
						f.get().setStatus(Friendship.Status.ACCEPTED);
					case DECLINED:
						throw new IllegalAccessException();        //DeclinedByReceiverException
				}
			} else {
				Optional<Friendship> onlF = this.friendsWith.stream()
						.filter(friendship -> friendship.getSender().equals(this))
						.filter(friendship -> friendship.getReceiver().equals(user))
						.findFirst();
				if(onlF.isEmpty()) return Optional.of(new Friendship(this, user, Friendship.Status.PENDING));
				else throw new IllegalCallerException();        //RequestAlreadySentException
			}
		} catch(IllegalStateException e) {
			System.out.println("Already friends with " + user + "!");
		} catch(IllegalAccessException e) {
			System.out.println("Request has been already declined by " + user + "!");
		} catch(IllegalCallerException e) {
			System.out.println("You already sent the request to " + user + "!");
		}
		return Optional.empty();
	}

	public void declineFriendship(User user){
		try {
			Optional<Friendship> f = this.friendsWith.stream()
					.filter(friendship -> friendship.getSender().equals(user))
					.findFirst();
			if (f.isPresent()) f.get().setStatus(Friendship.Status.DECLINED);
			else throw new NoSuchElementException();
		} catch(Exception e){
			System.out.println("Friendship request from " + user + "does not exists!");
		}
	}

	public List<Friendship> getAllFriendshipRequests(){
		return this.friendsWith.stream()
				.filter(friendship -> friendship.getStatus() == Friendship.Status.PENDING)
				.collect(Collectors.toList());
	}

	public void rejectFriendship(User user){
		try{
			Optional<Friendship>  friend = this.friendsWith.stream()
					.filter(friendship -> friendship.getStatus() == Friendship.Status.PENDING)
					.filter(friendship -> friendship.getSender().equals(user))
					.findFirst();
			if(friend.isPresent()){
				friend.get().setStatus(Friendship.Status.DECLINED);
			} else throw new NoSuchElementException();        //NoPendingInvitationException
		} catch (NoSuchElementException e){
			System.out.println("No pending friendship request from "+ user);
		}
	}

	public void markAsAutoAccept(User user){
		try {
			for (Friendship friend : this.friendsWith) {
				if (!friend.getStatus().equals(Friendship.Status.DECLINED) && friend.getSender().equals(user)) {
					friend.setStatus(Friendship.Status.AUTO_APPROVE);
				} else if (friend.getSender().equals(user) || friend.getReceiver().equals(user)) {
					throw new Exception();
				}
			}
		}catch (Exception e){
			System.out.println("You cannot mark " + user + "as a friend!");
		}
	}

	public List<Obligation> getPendingObligations(){
		List<Obligation> pendingOwes = this.owes.stream()
				.filter(obligation -> obligation.getStatus().equals(Friendship.Status.PENDING))
				.collect(Collectors.toList());
		List<Obligation> pendingOwed = this.isOwed.stream()
				.filter(obligation -> obligation.getStatus().equals(Friendship.Status.PENDING))
				.collect(Collectors.toList());
		pendingOwes.addAll(pendingOwed);
		return pendingOwes;
	}

	public boolean isFriend(User user){
		for (Friendship f : this.friendsWith) {
			if((f.getSender().equals(user) || f.getReceiver().equals(user)) &&
					(f.getStatus().equals(Friendship.Status.ACCEPTED) || (f.getStatus().equals(Friendship.Status.AUTO_APPROVE)))) return true;
		}
		return false;
	}

	public boolean isSuperFriend(User user){
		for (Friendship f : this.friendsWith) {
			if((f.getSender().equals(user) || f.getReceiver().equals(user)) && f.getStatus().equals(Friendship.Status.ACCEPTED)) return true;
		}
		return false;
	}
}
