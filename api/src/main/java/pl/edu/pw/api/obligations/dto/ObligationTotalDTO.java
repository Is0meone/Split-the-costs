package pl.edu.pw.api.obligations.dto;

import pl.edu.pw.models.User;

public class ObligationTotalDTO {
	private User user;
	private Double totalAmount;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Double totalAmount) {
		this.totalAmount = totalAmount;
	}
}
