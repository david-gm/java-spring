package academy.learnprogramming.console;

import academy.learnprogramming.config.GameConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Main {

    public static void main(String[] args) {
        log.info("Guess The Number Game");

        // create the context (container)
        // NOTE: using an interface (left) and a class to the right
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(GameConfig.class);
        // close context (container) - doing that to prevent any memory resource leaks
        context.close();
    }
}
