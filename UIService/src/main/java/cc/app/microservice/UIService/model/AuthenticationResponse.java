package cc.app.microservice.UIService.model;

public class AuthenticationResponse {

    private String jwt;

    public AuthenticationResponse() { }

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
