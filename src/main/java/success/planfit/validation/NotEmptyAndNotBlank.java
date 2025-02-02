package success.planfit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NotEmptyAndNotBlankValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NotEmptyAndNotBlank {

    String value();

    boolean allowWhiteSpace() default true;

    String message() default "잘못된 요청입니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
