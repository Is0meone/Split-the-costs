package pl.edu.pw.api.auth;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.api.auth.dto.LoginDTO;
import pl.edu.pw.api.auth.dto.RegisterDTO;
import pl.edu.pw.api.auth.dto.UserTokenDTO;

@RestController("/auth")
public class AuthController {
	@PostMapping("/register")
	public UserTokenDTO register(@RequestBody @Validated RegisterDTO registerDTO) {
		return null;
	}
	@PostMapping("/login")
	public UserTokenDTO login(@RequestBody @Validated LoginDTO loginDTO) {
		return null;
	}

	@GetMapping("/logout")
	public void logout() {
	}

}
