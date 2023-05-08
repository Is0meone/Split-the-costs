package pl.edu.pw;

import pl.edu.pw.models.Obligation;
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

	void split(Double amount, User... users) {
		Double splittedAmount;
		splittedAmount = amount/users.length;
		for (User user: users) {
			new Obligation(actor, user, splittedAmount, Obligation.Status.PENDING);
		}
	}

	/**
	 * Splits the expense equally between users.
	 * @param amount
	 * @param users
	 */
	void split(Double amount, List<User> users) {
		Double splittedAmount;
		splittedAmount = amount/users.size();
		for (User user: users) {
			new Obligation(actor, user, splittedAmount, Obligation.Status.PENDING);
		}
	}

	/**
	 * Splits the expense by the given amounts.
	 * @param users a map with users as keys and the amounts they should pay as values
	 */
	void split(Map<User, Double> users) {
		for (Map.Entry<User, Double> entry : users.entrySet()) {
			new Obligation(actor, entry.getKey(), entry.getValue(), Obligation.Status.PENDING);
		}
	}

}
