package success.planfit.exception;

/**
 * 프론트 측의 요청에 문제가 있을 경우 사용
 */
public class IllegalRequestException extends IllegalArgumentException {

    public IllegalRequestException(Exception e) {
        super(e);
    }

    public IllegalRequestException(String message) {
        super(message);
    }

}
