package academy.learnprogramming.console;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ConsoleNumberGuess {

    // == constants ==
    private static final Logger log = LoggerFactory.getLogger(ConsoleNumberGuess.class);

    @EventListener(ContextRefreshedEvent.class)
    public void onStart() {
        // Context RefreshedEvent is published when the ApplicationContext is initialized or refreshed
        // --> all beans are loaded, singletons are pre-instantiated, ...

        log.info("onStart(): Container ready to use.");
    }
}
