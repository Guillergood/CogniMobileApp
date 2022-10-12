package ugr.gbv.cognimobile.payload.response;


import java.util.List;

public class JwtResponse {
  private String token;
  private String type = "Bearer";
  private String username;
  private List<String> roles;
  private String refreshToken;

  public JwtResponse() {
  }

  public JwtResponse(String accessToken, String username, List<String> roles, String refreshToken) {
    this.token = accessToken;
    this.username = username;
    this.roles = roles;
    this.refreshToken = refreshToken;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public String getRefreshToken() {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }
}
