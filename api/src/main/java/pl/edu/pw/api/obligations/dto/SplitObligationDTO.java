package pl.edu.pw.api.obligations.dto;

import java.util.List;

public class SplitObligationDTO {
	private String description;
	private String timestamp;
	private List<Long> users;
	private Double amount;

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

	public List<Long> getUsers() {
		return users;
	}

	public void setUsers(List<Long> users) {
		this.users = users;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}
}
