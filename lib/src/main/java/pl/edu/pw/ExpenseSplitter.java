package pl.edu.pw;

import pl.edu.pw.models.Obligation;
import pl.edu.pw.models.User;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ExpenseSplitter {
	private User actor;
	private DBConnector dbc;
	private GraphLogic gl;

	public ExpenseSplitter(User actor, DBConnector dbc) {
		this.actor = actor;
		this.dbc = dbc;
		this.gl = new GraphLogic(dbc);
	}

	public User getActor() {
		return actor;
	}

	/**
	 * Splits the expense equally between users.
	 * @param amount
	 * @param users
	 */

	public void split(Double amount, User... users) {
		users = (User[]) Arrays.stream(users).filter( user -> actor.isFriend(user)).toArray();
		Double splittedAmount;
		splittedAmount = amount/(users.length+1);
		for (User user: users) {
			if(actor.isSuperFriend(user)) {
				Obligation obl = new Obligation(actor, user, splittedAmount, Obligation.Status.ACCEPTED);
				dbc.addObligation(obl);
				gl.debtTransfer(obl);
			}
			else dbc.addObligation(new Obligation(actor, user, splittedAmount, Obligation.Status.PENDING));
		}
	}

	/**
	 * Splits the expense equally between users.
	 * @param amount
	 * @param users
	 */
	public void split(Double amount, List<User> users) {
		Iterator<User> i = users.iterator();
			while(i.hasNext()){
				User u = i.next();
				if (!actor.isFriend(u)) i.remove();
			}
		Double splittedAmount;
		splittedAmount = amount/(users.size()+1);
		for (User user: users) {
			if(actor.isSuperFriend(user)) {
				Obligation obl = new Obligation(actor, user, splittedAmount, Obligation.Status.ACCEPTED);
				dbc.addObligation(obl);
				gl.debtTransfer(obl);
			}
			else dbc.addObligation(new Obligation(actor, user, splittedAmount, Obligation.Status.PENDING));
		}
	}

	/**
	 * Splits the expense by the given amounts.
	 * @param users a map with users as keys and the amounts they should pay as values
	 */
	public void split(Map<User, Double> users) {
			for (Map.Entry<User, Double> entry : users.entrySet()) {
				if (!actor.isFriend(entry.getKey())) users.remove(entry.getKey());
		}
		for (Map.Entry<User, Double> entry : users.entrySet()) {
			if(actor.isSuperFriend(entry.getKey())){
				Obligation obl = new Obligation(actor, entry.getKey(), entry.getValue(), Obligation.Status.ACCEPTED);
				dbc.addObligation(obl);
				gl.debtTransfer(obl);
			}
			else{
				dbc.addObligation(new Obligation(actor, entry.getKey(), entry.getValue(), Obligation.Status.PENDING));
			}
		}
	}
}
