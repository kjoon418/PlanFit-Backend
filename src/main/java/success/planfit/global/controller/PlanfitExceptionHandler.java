package success.planfit.global.controller;

import io.jsonwebtoken.MalformedJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.sql.SQLIntegrityConstraintViolationException;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@Component
public class PlanfitExceptionHandler {

    public ResponseEntity<String> handle(Exception e) {
        if (e instanceof MethodArgumentNotValidException methodArgumentNotValidException) {
            FieldError fieldError = methodArgumentNotValidException
                    .getBindingResult()
                    .getFieldErrors()
                    .getFirst();
            return ResponseEntity.status(BAD_REQUEST).body(fieldError.getDefaultMessage());
        }
        if (e instanceof EntityNotFoundException ||
                e instanceof IllegalArgumentException) {
            return ResponseEntity.status(BAD_REQUEST).body(e.getMessage());
        }
        if (e instanceof SQLIntegrityConstraintViolationException ||
                e instanceof DataIntegrityViolationException ||
                e instanceof ConstraintViolationException) {
            return ResponseEntity.status(BAD_REQUEST).body("다른 사용자가 이미 사용하고 있는 값이거나, 이미 처리된 요청입니다.");
        }
        if (e instanceof MalformedJwtException) {
            return ResponseEntity.status(UNAUTHORIZED).body("부적절한 JWT 토큰입니다.");
        }
        if (e instanceof HttpMessageNotReadableException) {
            return ResponseEntity.status(BAD_REQUEST).body("JSON 파싱에 실패했습니다.");
        }

        log.error("Unhandled error occurred", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body("예상하지 못한 예외가 발생했습니다: " + e.getMessage());
    }
}
