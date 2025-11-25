package HCMUT.TutorSytem.exception;
//Thank for chatgpt i love you guy 

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}

