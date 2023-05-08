package pl.edu.pw.api.auth;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.auth.dto.LoginDTO;
import pl.edu.pw.api.auth.dto.RegisterDTO;
import pl.edu.pw.api.auth.dto.UserTokenDTO;
import pl.edu.pw.models.User;


@RestController("/auth")
public class AuthController {
	@PostMapping("/register")
	public UserTokenDTO register(@RequestBody @Validated RegisterDTO registerDTO) {
		DBConnector dbc = new DBConnector();
		dbc.addUser(new User(registerDTO.getName(), registerDTO.getPassword()));
		return null;
	}

	@PostMapping("/login")
	public UserTokenDTO login(@RequestBody @Validated LoginDTO loginDTO) {
		UserTokenDTO u = new UserTokenDTO();
		u.setId(123333334L);
		u.setToken(loginDTO.getName());
		return u;
	}

	@GetMapping("/logout")
	public void logout() {
	}

	@GetMapping("/test1")
	public LoginDTO test1() {
		LoginDTO login = new LoginDTO();
		login.setName("jan");
		login.setPassword("PassWord");
		return login;
	}

	@PostMapping("/test2")
	public void test2(@RequestBody LoginDTO loginDTO) {
		System.out.println(loginDTO);
	}
}
