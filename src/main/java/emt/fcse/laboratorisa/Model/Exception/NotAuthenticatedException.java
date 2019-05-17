package emt.fcse.laboratorisa.Model.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class NotAuthenticatedException extends RuntimeException {
    public NotAuthenticatedException() {
        super("Not Authenticated Exception");
    }
}
