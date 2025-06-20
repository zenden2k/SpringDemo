package dev.svistunov.springdemo.exception;

//@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends BaseException {
    public UserNotFoundException(Long id) {
        super("User not found with id: " + id);
    }
}