package md.cassa.be2048.exceptions;

public class WrongCredentialsException extends RuntimeException {
    public WrongCredentialsException() {
        super("Username or password is incorrect");
    }
}
