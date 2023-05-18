package pl.edu.pw.api.obligations.dto;

import pl.edu.pw.models.Obligation;

public class ObligationDTO {
	private String description;
	private String timestamp;
	private Obligation.Status status;
	private Double amount;
	private Long debtorId;
	private Long creditorId;

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

	public Obligation.Status getStatus() {
		return status;
	}

	public void setStatus(Obligation.Status status) {
		this.status = status;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Long getDebtorId() {
		return debtorId;
	}

	public void setDebtorId(Long debtorId) {
		this.debtorId = debtorId;
	}

	public Long getCreditorId() {
		return creditorId;
	}

	public void setCreditorId(Long creditorId) {
		this.creditorId = creditorId;
	}
}
