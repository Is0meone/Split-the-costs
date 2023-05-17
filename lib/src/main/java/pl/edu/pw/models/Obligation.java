package pl.edu.pw.models;

import org.neo4j.ogm.annotation.*;

import java.util.Objects;

@RelationshipEntity(type="OWES")
public class Obligation {
	public enum Status {
		PAID,
		ACCEPTED,
		DECLINED,
		PENDING,
		AUTOGEN,
		AUTOPAID
	}
	@Id
	@GeneratedValue
	private Long id;
	@EndNode
	private User creditor;
	@StartNode
	private User debtor;
	private Double amount;
	private Double coreAmount; //moze finall

	private Status status;

	private String description;
	private String timestamp;

	public Obligation() {
	}

	public Obligation(
			User creditor, User debtor, Double amount, Status status, String description, String timestamp
	) {
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
		this.coreAmount = amount;
		this.status = status;
		this.description = description;
		this.timestamp = timestamp;
	}

	public Obligation(User creditor, User debtor, Double amount, Status status) {
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
		this.coreAmount = amount;
		this.status = status;
	}

	public Obligation(User creditor, User debtor, Double amount) {
		this.creditor = creditor;
		this.debtor = debtor;
		this.coreAmount = amount;
		this.amount = amount;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getCreditor() {
		return creditor;
	}

	public void setCreditor(User creditor) {
		this.creditor = creditor;
	}

	public User getDebtor() {
		return debtor;
	}
	public Double getCoreAmount(){
		return coreAmount;
	}
	public void setDebtor(User debtor) {
		this.debtor = debtor;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Obligation that)) return false;
		return Objects.equals(id, that.id) && Objects.equals(
				creditor, that.creditor) && Objects.equals(debtor, that.debtor) && Objects.equals(
				amount, that.amount) && status == that.status;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, creditor, debtor, amount, status);
	}

	@Override
	public String toString() {
		return "Obligation{" +
				"id=" + id +
				", creditor=" + creditor +
				", debtor=" + debtor +
				", amount=" + amount +
				", status=" + status +
				'}';
	}

	/**
	 * Accept the obligation as valid
	 */
	public void accept() {
		this.status = Status.ACCEPTED;
	}

	/**
	 * Decline the obligation request
	 */
	public void decline() {
		this.status = Status.DECLINED;
	}

	/**
	 * Mark the obligation as paid
	 */
	public void pay() {
		this.status = Status.PAID;
	}
	public void autopay(){
		this.status = Status.AUTOPAID;
	}
}
