package pl.edu.pw.api.users;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.api.users.dto.UserDTO;
import pl.edu.pw.models.User;

import java.util.List;
import java.util.stream.Collectors;

@RestController("/users")
public class UserController {
	@Autowired
	private JwtService jwtService;
	private DBConnector dbc = new DBConnector();
	@GetMapping("/allusers")
	public List<UserDTO> getUsers() {
		List<User> users = dbc.getAllUsers();
		return users.stream()
				.map(user -> {
					UserDTO userDTO = new UserDTO();
					userDTO.setId(user.getId());
					userDTO.setName(user.getName());
					return userDTO;
				})
				.collect(Collectors.toList());
	}

	@GetMapping("/findid/{id}")
	public UserDTO getUser(@PathVariable Long id) {
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
	@GetMapping("/findname/{name}")
	public List<UserDTO> findUsers(@PathVariable String name) {
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
}
