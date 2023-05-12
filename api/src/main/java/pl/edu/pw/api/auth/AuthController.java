package pl.edu.pw.api.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.DBConnector;
import pl.edu.pw.api.auth.dto.LoginDTO;
import pl.edu.pw.api.auth.dto.RegisterDTO;
import pl.edu.pw.api.auth.dto.UserTokenDTO;
import pl.edu.pw.api.security.JwtService;
import pl.edu.pw.models.User;


@RestController("/auth")
public class AuthController {
	@Autowired
	private JwtService jwtService;

	@PostMapping("/register")
	public UserTokenDTO register(@RequestBody @Validated RegisterDTO registerDTO) {
		DBConnector dbc = new DBConnector();
		dbc.addUser(new User(registerDTO.getName(), registerDTO.getPassword()));
		return null;
	}

	@PostMapping("/login")
	public String authenticateAndGetToken(@RequestBody LoginDTO loginDTO) {
		//TODO: Check username and password in database,
		// return true and generate token
		return jwtService.generateToken(loginDTO.getUsername());
	}

	@GetMapping("/logout")
	public void logout() {
	}

	@GetMapping("/test")
	public LoginDTO test1() {
		LoginDTO login = new LoginDTO();
		login.setUsername("jan");
		login.setPassword("PassWord");
		return login;
	}
	@GetMapping("/test/{id}")
	public @ResponseBody String generateReport(@PathVariable("id") Long id, HttpServletRequest request) {

		if(jwtService.checkUserToken(id, request)){
			return "correct " + jwtService.getUsernameFromToken(id,request);
		}
		return "wrong";
	}

}
