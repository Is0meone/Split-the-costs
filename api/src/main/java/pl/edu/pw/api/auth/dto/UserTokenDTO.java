package pl.edu.pw.api.auth.dto;

public class UserTokenDTO {
	private String token;
	private Long userId;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long id) {
		this.userId = id;
	}
}
