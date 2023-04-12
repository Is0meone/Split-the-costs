package pl.edu.pw;

import pl.edu.pw.models.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class ExpenseSplitter {
	private User actor;

	public ExpenseSplitter(User actor) {
		this.actor = actor;
	}

	public User getActor() {
		return actor;
	}

	/**
	 * Splits the expense equally between users.
	 * @param amount
	 * @param users
	 */

	void split(BigDecimal amount, User... users) {

	}

	/**
	 * Splits the expense equally between users.
	 * @param amount
	 * @param users
	 */
	void split(BigDecimal amount, List<User> users) {

	}

	/**
	 * Splits the expense by the given amounts.
	 * @param users a map with users as keys and the amounts they should pay as values
	 */
	void split(Map<User, BigDecimal> users) {

	}

}
