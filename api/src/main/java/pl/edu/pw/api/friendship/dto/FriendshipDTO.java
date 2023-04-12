package pl.edu.pw.api.friendship.dto;

import pl.edu.pw.models.Friendship;
import pl.edu.pw.models.User;

public class FriendshipDTO {
	private User from;
	private User to;
	private Friendship.Status status;
}
