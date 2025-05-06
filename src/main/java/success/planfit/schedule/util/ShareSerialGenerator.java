package success.planfit.schedule.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Set;

@Component
public class ShareSerialGenerator {

    private static final int MINIMUM_SERIAL_LENGTH = 50;
    private static final int MAXIMUM_SERIAL_LENGTH = 100;
    private static final boolean USE_LETTERS = true;
    private static final boolean USE_NUMBERS = true;

    private static final Random random = new Random();

    public String generateUniqueSerial(Set<String> existsSerials) {
        String randomSerial = generateRandomSerial();

        while (existsSerials.contains(randomSerial)) {
            randomSerial = generateRandomSerial();
        }

        return randomSerial;
    }

    private String generateRandomSerial() {
        int length = getRandomSerialLength();
        RandomStringUtils secureRandom = RandomStringUtils.secure();

        return secureRandom.next(length, USE_LETTERS, USE_NUMBERS);
    }

    private int getRandomSerialLength() {
        return random.nextInt(MINIMUM_SERIAL_LENGTH, MAXIMUM_SERIAL_LENGTH);
    }

}
