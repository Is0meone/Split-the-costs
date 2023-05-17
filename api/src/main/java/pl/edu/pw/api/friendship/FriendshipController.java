package pl.edu.pw.api.friendship;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.auth.dto.LoginDTO;
import pl.edu.pw.api.friendship.dto.FriendsDTO;
import pl.edu.pw.api.friendship.dto.FriendshipDTO;
import pl.edu.pw.api.friendship.dto.FriendshipRequestDTO;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.api.users.dto.UserDTO;
import pl.edu.pw.models.Friendship;
import pl.edu.pw.models.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/friends")
public class FriendshipController {
	@Autowired
	private JwtService jwtService;
	private DBConnector dbc = new DBConnector();

	/**
	 * Sends a friendship request to the user with the given id (or accepts the friendship if the other side requested it)
	 * @param id id of the second user
	 */
	@GetMapping("/user/{id}/requestoracceptfriendship/{withid}")
	public void requestOrAcceptFriendship(@PathVariable("id") Long id, HttpServletRequest request,@PathVariable("withid") Long withId) {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.sendOrAcceptFriendship(dbc.findUserById(withId));
			dbc.updateUser(user);
		}
	}


	/**
	 * Rejects the friendship request from the user with the given id (or cancels the friendship if it was already accepted)
	 * @param id id of the second user
	 */
	@GetMapping("/user/{id}/rejectfriendship/{withid}")
	public void rejectFriendship(@PathVariable("id") Long id, HttpServletRequest request,@PathVariable("withid") Long withId) {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.rejectFriendship(dbc.findUserById(withId));
			dbc.updateUser(user);
		}
	}


	/**
	 * Makes it so that the current user will automatically accept obligations from the user with the given id
	 * @param id id of the second user
	 */
	@GetMapping("/user/{id}/auto/{withid}")
	public void markAsAutoAccept(@PathVariable("id") Long id, HttpServletRequest request, @PathVariable("withid") Long withId) {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			user.markAsAutoAccept(dbc.findUserById(withId));
			dbc.updateUser(user);
		}
	}


	/**
	 * Returns a list of all users that are friends with the current user
	 * @return list of friends
	 */
	@GetMapping("/user/{id}/friends")
	public List<FriendsDTO> getFriends(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			List<Friendship> friends = user.getFriendsWith();
			return friends.stream()
					.map(friend -> {
						FriendsDTO friendsDTO = new FriendsDTO();
						friendsDTO.setId(user.getId());
						friendsDTO.setUsername(user.getName());
						return friendsDTO;
					})
					.collect(Collectors.toList());
		} else{
			response.setStatus(401);
		}
		return null;
	}


	/**
	 * Returns a list of all users that have sent a friendship request to the current user
	 */
	@GetMapping("/user/{id}/requests")
	public List<FriendshipRequestDTO> getFriendshipRequests(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) {
		if(jwtService.checkUserToken(id, request)) {
			User user = dbc.findUserById(id);
			List<Friendship> friendshipRequests = user.getAllFriendshipRequests();
			return friendshipRequests.stream()
					.map(friend -> {
						FriendshipRequestDTO friendsDTO = new FriendshipRequestDTO();
						friendsDTO.setId(user.getId());
						friendsDTO.setUsername(user.getName());
						return friendsDTO;
					})
					.collect(Collectors.toList());
		}else {
			response.setStatus(401);
		}
		return null;
	}

}
