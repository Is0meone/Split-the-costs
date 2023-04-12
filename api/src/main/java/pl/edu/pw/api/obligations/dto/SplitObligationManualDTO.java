package pl.edu.pw.api.obligations.dto;

import java.util.List;
import java.util.Map;

public class SplitObligationManualDTO {
	private String description;
	private String timestamp;
	private Map<Long, Double> users;

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

	public Map<Long, Double> getUsers() {
		return users;
	}

	public void setUsers(Map<Long, Double> users) {
		this.users = users;
	}
}
