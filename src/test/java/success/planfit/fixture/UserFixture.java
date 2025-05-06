package success.planfit.fixture;

import success.planfit.entity.user.IdentityType;
import success.planfit.entity.user.PlanfitUser;
import success.planfit.entity.user.User;

import java.time.LocalDate;

public enum UserFixture {

    USER_A(
            "userA_ID",
            "userA_PASSWORD",
            "userA_USER_NAME",
            "01012345678",
            LocalDate.of(2001, 4, 18),
            IdentityType.STUDENT,
            "userA@email.com"
    ),
    USER_B(
            "userB_ID",
            "userB_PASSWORD",
            "userB_USER_NAME",
            "01098765432",
            LocalDate.of(2011, 11, 22),
            IdentityType.STUDENT,
            "userB@email.com"
    ),
    USER_C(
            "userC_ID",
            "userC_PASSWORD",
            "userC_USER_NAME",
            "01011112222",
            LocalDate.of(2015, 11, 22),
            IdentityType.STUDENT,
            "userC@email.com"
    );

    private final String loginId;
    private final String password;
    private final String name;
    private final String phoneNumber;
    private final LocalDate birthOfDate;
    private final IdentityType identity;
    private final String email;

    UserFixture(
            String loginId,
            String password,
            String name,
            String phoneNumber,
            LocalDate birthOfDate,
            IdentityType identity,
            String email
    ) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.birthOfDate = birthOfDate;
        this.identity = identity;
        this.email = email;
    }

    public PlanfitUser createInstance() {
        return PlanfitUser.builder()
                .loginId(loginId)
                .password(password)
                .name(name)
                .phoneNumber(phoneNumber)
                .birthOfDate(birthOfDate)
                .identity(identity)
                .email(email)
                .build();
    }

}
