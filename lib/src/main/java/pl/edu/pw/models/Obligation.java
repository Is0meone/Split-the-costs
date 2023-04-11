package pl.edu.pw.models;

import java.math.BigDecimal;

public class Obligation {
	public enum Status {
		ACCEPTED,
		DECLINED,
		PENDING
	}
	private final String id;
	private final User creditor;
	private final User debtor;
	private final BigDecimal amount;

	private Status status;
	private boolean paid;

	public Obligation(String id, User creditor, User debtor, BigDecimal amount) {
		this.id = id;
		this.creditor = creditor;
		this.debtor = debtor;
		this.amount = amount;
	}

	public String getId() {
		return id;
	}

	public User getCreditor() {
		return creditor;
	}

	public User getDebtor() {
		return debtor;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public boolean isPaid() {
		return paid;
	}
}
