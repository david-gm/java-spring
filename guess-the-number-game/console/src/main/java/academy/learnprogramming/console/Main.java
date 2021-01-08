package academy.learnprogramming.console;

import academy.learnprogramming.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        log.info("Guess The Number Game");

        // create the context (container)
        // NOTE: using an interface (left) and a class to the right
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        // close context (container) - doing that to prevent any memory resource leaks
        context.close();
    }
}
