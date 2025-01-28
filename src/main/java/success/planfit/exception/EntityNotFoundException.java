package success.planfit.exception;

/**
 * 엔티티 조회 실패시 사용
 */
public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Exception e) {
        super(e);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

}
