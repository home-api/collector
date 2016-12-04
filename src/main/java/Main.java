import com.google.inject.Guice;
import com.google.inject.Injector;
import bot.CollectorBot;
import configuration.Configuration;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;

public class Main {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
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
