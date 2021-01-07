package academy.learnprogramming;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    private static final String CONFIG_LOCATION = "beans.xml";

    public static void main(String[] args) {
        log.info("Guess The Number Game");

        // create the context (container)

        // NOTE: using an interface (left) and a class to the right
        ConfigurableApplicationContext context = new ClassPathXmlApplicationContext(CONFIG_LOCATION);

        // get number generator bean from context (container); name has to match the Id of the bean.xml file
        NumberGenerator numberGenerator = context.getBean("numberGenerator", NumberGenerator.class);

        // call methods next() to get a random number
        int number = numberGenerator.next();
        log.info("number = {}", number);

        // get game bean from context (container)
        Game game = context.getBean(Game.class);

        // close context (container) - doing that to prevent any memory resource leaks
        context.close();
    }
}
