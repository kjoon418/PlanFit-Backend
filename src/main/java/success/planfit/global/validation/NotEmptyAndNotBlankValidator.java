package success.planfit.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NotEmptyAndNotBlankValidator implements ConstraintValidator<NotEmptyAndNotBlank, Object> {

    private String fieldName;
    private boolean allowWhiteSpace;

    @Override
    public void initialize(NotEmptyAndNotBlank constraintAnnotation) {
        this.fieldName = constraintAnnotation.value(); // value 필드명을 받아옴
        this.allowWhiteSpace = constraintAnnotation.allowWhiteSpace(); // 공백 허용 여부를 받아옴
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            buildContext(context, "은(는) 필수 파라미터입니다.");
            return false;
        }

        // 문자열일 경우의 검증
        if (value instanceof String str) {
            if (str.isEmpty()) {
                buildContext(context, "은(는) 비어있을 수 없습니다.");
                return false;
            }

            if (str.trim().isEmpty()) {
                buildContext(context, "은(는) 공백일 수 없습니다.");
                return false;
            }

            if (!allowWhiteSpace && str.contains(" ")) {
                buildContext(context, "에는 공백이 있을 수 없습니다.");
                return false;
            }
        }

        return true;
    }

    private void buildContext(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(fieldName + message)
                .addConstraintViolation();
    }

}
