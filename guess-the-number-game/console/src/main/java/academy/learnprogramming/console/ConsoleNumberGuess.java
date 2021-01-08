package academy.learnprogramming.console;

import academy.learnprogramming.Game;
import academy.learnprogramming.MessageGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Scanner;

@Component
public class ConsoleNumberGuess {

    // == constants ==
    private static final Logger log = LoggerFactory.getLogger(ConsoleNumberGuess.class);

    // == fields ==
    @Autowired
    private Game game;

    @Autowired
    private MessageGenerator messageGenerator;

    // == events ==
    @EventListener(ContextRefreshedEvent.class)
    public void onStart() throws FileNotFoundException {
        // Context RefreshedEvent is published when the ApplicationContext is initialized or refreshed
        // --> all beans are loaded, singletons are pre-instantiated, ...

        //exampleReadFromFileWithScanner();

        Scanner scanner = new Scanner(System.in); // A simple text scanner which can parse primitive types and strings using regular expressions.

        while(true) {
            System.out.println(messageGenerator.getMainMessage());
            System.out.println(messageGenerator.getResultMessage());

            int guess = scanner.nextInt();
            scanner.nextLine();
            game.setGuess(guess);
            game.check();

            if(game.isGameWon() || game.isGameLost()) {
                System.out.println(messageGenerator.getResultMessage());
                System.out.println("Play again y/n?");

                String playAgainString = scanner.nextLine().trim();
                if(!playAgainString.equalsIgnoreCase("y")) {
                    break;
                }

                game.reset();
            }
        }


    }

    private void exampleReadFromFileWithScanner() {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("myNumbers.txt");
        if (inputStream == null)
            return;

        // A simple text scanner which can parse primitive types and strings using regular expressions.
        // A Scanner breaks its input into tokens using a delimiter pattern, which by default matches whitespace.
        // The resulting tokens may then be converted into values of different types using the various next methods.
        // For example, this code allows a user to read a number from System.in:
        //
        //     Scanner sc = new Scanner(System.in);
        //     int i = sc.nextInt();
        //
        // As another example, this code allows long types to be assigned from entries in a file myNumbers:
        //
        //      Scanner sc = new Scanner(new File("myNumbers"));
        //      while (sc.hasNextLong()) {
        //          long aLong = sc.nextLong();
        //      }
        //
        Scanner scanner = new Scanner(inputStream);
        scanner.useLocale(Locale.ENGLISH);
        while (scanner.hasNextDouble()) {
            double num = scanner.nextDouble();
            log.info("number: {}", num);
        }
    }
}
