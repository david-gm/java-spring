package academy.learnprogramming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloMaven {

    private final static Logger log = LoggerFactory.getLogger(HelloMaven.class);

    public static void main(String[] args) {
        System.out.println("Hello Maven");
        Test t = new Test(5);
        t.print(); //

        log.info("Hello Info");
        log.debug("Hello Debug");
    }
}
