package cc.app.microservice.VideoUploadService.model;

public class AuthenticationResponse {

    private final String jwt;

    public AuthenticationResponse(){this.jwt = null;}

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public String getJwt() {
        return jwt;
    }
}
