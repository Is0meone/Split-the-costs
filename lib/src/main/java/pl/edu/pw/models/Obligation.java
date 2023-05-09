package pl.edu.pw.models;

import org.neo4j.ogm.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@RelationshipEntity(type="OWES")
public class Obligation {
	public enum Status {
		PAID,
		ACCEPTED,
		DECLINED,
		PENDING
	}
	@Id
	@GeneratedValue
	private Long id;
	@EndNode
	private User creditor;
	@StartNode
	private User debtor;
	private Double amount;

	private Status status;

	private String description;
	private LocalDateTime timestamp;

	public Obligation() {
	}

	public Obligation(
			User creditor, User debtor, Double amount, Status status, String description, LocalDateTime timestamp
	) {
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
		this.status = status;
		this.description = description;
		this.timestamp = timestamp;
		creditor.addOwed(this);
		debtor.addOwes(this);
	}

	public Obligation(User creditor, User debtor, Double amount, Status status) {
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
		this.status = status;
		creditor.addOwed(this);
		debtor.addOwes(this);
	}

	public Obligation(User creditor, User debtor, Double amount) {
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
		creditor.addOwed(this);
		debtor.addOwes(this);
	}

	public Obligation(Long id) {
		this.id = id;
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

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
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
}
