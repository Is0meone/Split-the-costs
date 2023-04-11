package pl.edu.pw.models;

import java.math.BigDecimal;

public class User {
	private final String id;
	private String name;
	private String passwordHash;

	public User(String id, String name, String passwordHash) {
		this.id = id;
		this.name = name;
		this.passwordHash = passwordHash;
	}
	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}
	public User(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public void createObligationFor(User user, BigDecimal amount) {
	}

	public void requestObligationFrom(User user, BigDecimal amount) {
	}
}
