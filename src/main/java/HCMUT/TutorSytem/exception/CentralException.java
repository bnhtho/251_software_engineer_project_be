package HCMUT.TutorSytem.exception;


import HCMUT.TutorSytem.payload.response.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CentralException {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleException(Exception e) {
        BaseResponse BaseResponse = new BaseResponse();
        BaseResponse.setStatusCode(500);
        BaseResponse.setMessage(e.getMessage());
        BaseResponse.setData(null);

        return ResponseEntity.ok(BaseResponse);
    }

    @ExceptionHandler(DataNotFoundExceptions.class)
    public ResponseEntity<?> handleDataNotFoundExceptions(Exception e) {
        BaseResponse BaseResponse = new BaseResponse();
        BaseResponse.setStatusCode(404);
        BaseResponse.setMessage(e.getMessage());
        BaseResponse.setData(null);

        return ResponseEntity.ok(BaseResponse);
    }

    @ExceptionHandler(MethodNotAllowExceptions.class)
    public ResponseEntity<?> handleMethodNotAllowExceptions(Exception e) {
        BaseResponse BaseResponse = new BaseResponse();
        BaseResponse.setStatusCode(405);
        BaseResponse.setMessage(e.getMessage());
        BaseResponse.setData(null);

        return ResponseEntity.ok(BaseResponse);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(Exception e) {
        BaseResponse BaseResponse = new BaseResponse();
        BaseResponse.setStatusCode(400);
        BaseResponse.setMessage(e.getMessage());
        BaseResponse.setData(null);

        return ResponseEntity.ok(BaseResponse);
    }
}
