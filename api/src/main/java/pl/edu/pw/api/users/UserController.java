package pl.edu.pw.api.users;

import org.springframework.web.bind.annotation.*;
import pl.edu.pw.api.users.dto.UserDTO;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
	@GetMapping
	public List<UserDTO> getUsers() {
		return null;
	}
	@GetMapping("/{id}")
	public UserDTO getUser(@PathVariable Long id) {
		return null;
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
		return null;
	}
}
