package pl.edu.pw;

import pl.edu.pw.models.User;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

public class ExpenseSplitter {
	User actor;

	void split(BigDecimal amount, User... users) {
		for (User user : users) {
			actor.requestObligationFrom(user, amount.divide(BigDecimal.valueOf(users.length), RoundingMode.HALF_EVEN));
		}
	}
	void split(BigDecimal amount, List<User> users) {

	}
	void split(Map<User, BigDecimal> users) {

	}

}
