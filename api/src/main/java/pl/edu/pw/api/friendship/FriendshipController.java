package pl.edu.pw.api.friendship;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.friendship.dto.FriendshipDTO;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.api.users.dto.UserDTO;
import pl.edu.pw.models.User;

import java.util.List;

@RestController("/friends")
public class FriendshipController {
	@Autowired
	private JwtService jwtService;
	/**
	 * Sends a friendship request to the user with the given id (or accepts the friendship if the other side requested it)
	 * @param id id of the second user
	 */
	@PostMapping("/user/{id}")
	public void requestOrAcceptFriendship(@PathVariable Long id) {

	}

	/**
	 * Rejects the friendship request from the user with the given id (or cancels the friendship if it was already accepted)
	 * @param id id of the second user
	 */
	@DeleteMapping("/user/{id}")
	public void rejectFriendship(@PathVariable Long id) {

	}

	/**
	 * Makes it so that the current user will automatically accept obligations from the user with the given id
	 * @param id id of the second user
	 */
	@PostMapping("/user/{id}/auto")
	public void markAsAutoAccept(@PathVariable Long id) {

	}

	/**
	 * Returns a list of all users that are friends with the current user
	 * @return list of friends
	 */
	@GetMapping
	public List<UserDTO> getFriends() {
		return null;
	}

	/**
	 * Returns a list of all users that have sent a friendship request to the current user
	 */
	@GetMapping("/requests")
	public List<FriendshipDTO> getFriendshipRequests() {
		return null;
	}
}
