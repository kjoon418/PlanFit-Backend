package success.planfit.global.exception;

/**
 * 이 클래스 대신 jakarta.persistence.EntityNotFoundException 클래스를 사용할 것
 */
@Deprecated
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Exception e) {
        super(e);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

}
