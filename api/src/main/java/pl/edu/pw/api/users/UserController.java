package pl.edu.pw.api.users;

import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.users.dto.UserDTO;
import pl.edu.pw.models.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
	@GetMapping
	public List<UserDTO> getUsers() {
		return null;
	}

	@GetMapping("/{id}")
	public UserDTO getUser(@PathVariable Long id) {
		DBConnector dbc = new DBConnector();
		User user = dbc.findUserById(id);
		UserDTO u = new UserDTO();
		u.setName(user.getName());
		u.setId(user.getId());
		return u;
	}

	@GetMapping("/{id}/total")
	public String getTotalObligationsToTo(@PathVariable Long id) {
		return null;
	}

	/**
	 * Search users by name (ideally full text search, or at least prefix match)
	 * @param name - name of user
	 * @return list of users that match the search term
	 */
	@GetMapping("/find")
	public List<UserDTO> findUsers(@RequestParam String name) {
		DBConnector dbc = new DBConnector();
		List<User> users = dbc.findUsersByPrefix(name);
		return users.stream()
				.map(user -> {
					UserDTO userDTO = new UserDTO();
					userDTO.setId(user.getId());
					userDTO.setName(user.getName());
					return userDTO;
				})
				.collect(Collectors.toList());
	}

	@GetMapping("/findtest")
	public UserDTO findUsersTest(@RequestParam String name) {
		System.out.println(name);
		DBConnector dbc = new DBConnector();
		User user = dbc.findUserByName(name);
		UserDTO u = new UserDTO();
		u.setName(user.getName());
		u.setId(790L);
		return null;
	}
}
