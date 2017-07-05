import bot.CollectorBot;
import com.google.inject.Guice;
import com.google.inject.Injector;
import configuration.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

public class Main {

    private static final int ARGUMENTS_REQUIRED_COUNT = 1;

    public static void main(String[] args) throws Exception {
        if (args.length != ARGUMENTS_REQUIRED_COUNT) {
            throw new IllegalAccessException("You must pass token!");
        }

        String token = args[0];
        Injector injector = Guice.createInjector(new Configuration(token));

        ApiContextInitializer.init();
        TelegramBotsApi api = new TelegramBotsApi();
        CollectorBot instance = injector.getInstance(CollectorBot.class);
        api.registerBot(instance);
    }

}
