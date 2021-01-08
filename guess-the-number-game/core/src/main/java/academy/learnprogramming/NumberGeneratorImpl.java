package academy.learnprogramming;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class NumberGeneratorImpl implements NumberGenerator {

    // == fields ==
    private final Random random = new Random();

    @Autowired
    @MaxNumber // custom qualifier: makes sure, that the correct bean type is autowired, even if the names do not match
    private int maxNumber;

    @Autowired
    @MinNumber // custom qualifier: makes sure, that the correct bean type is autowired, even if the names do not match
    private int minNumber;

    // == public methods ==
    @Override
    public int next() {
        // example: min = 5 max = 20 -> max-min = 15 -> range 0-15 + min -> 5-20
        return random.nextInt(maxNumber - minNumber) + minNumber;
    }

    @Override
    public int getMaxNumber() {
        return maxNumber;
    }

    @Override
    public int getMinNumber() {
        return minNumber;
    }
}
