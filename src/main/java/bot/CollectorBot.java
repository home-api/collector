package bot;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import command.Command;
import command.impl.MenuCommand;
import dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * This class is used only for command parse.;
 */
public class CollectorBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(CollectorBot.class);

    private LocalDateTime date = LocalDateTime.now();

    @Inject
    private Map<String, Command> commands;

    @Inject
    @Named("token")
    private String token;

    @Inject
    private OrderDAO orderDAO;

    @Inject
    @Named("menu")
    private Command menuCommand;

    @Override
    public void onUpdateReceived(Update update) {
        cleanOutdatedOrders();

        if (!update.hasMessage()) {
            return;
        }

        Message message = update.getMessage();

        try {
            handleMessage(message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private void cleanOutdatedOrders() {
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.getDayOfYear() != date.getDayOfYear()) {
            LOGGER.info("Removing all orders..");
            orderDAO.removeAllOrders();
            date = currentDate;
        }
    }

    private void handleMessage(Message message) throws Exception {
        if (!message.hasText()) {
            sendHelpMessage("Что пожелаете, сэр?", message);
        }

        String messageText = message.getText();
        LOGGER.info(message.getFrom().getFirstName() + " has sent " + messageText);

        if (messageText.length() < 4) {
            sendHelpMessage("Не понял, сэр", message);
            LOGGER.info(message.getFrom().getFirstName() + " has sent " + messageText);
            return;
        }

        String commandPrefix = messageText.substring(0, 2);
        String commandText = messageText.substring(2);

        Command command = commands.get(commandPrefix);

        if (command == null) {
            sendHelpMessage("Не понял, сэр", message);
            LOGGER.info(message.getFrom().getFirstName() + " has sent incorrect command: " + commandPrefix);
            return;
        }

        SendMessage response = command.execute(commandText, message);
        sendMessage(response);
    }

    private void sendHelpMessage(String greeting, Message message) throws Exception {
        SendMessage response = new SendMessage();
        response.enableMarkdown(true);
        response.setText(greeting);
        response.setChatId(message.getChatId());
        response.setReplyMarkup(((MenuCommand) menuCommand).getMainKeyboardMarkup());
        sendMessage(response);
    }

    public String getBotUsername() {
        return "";
    }

    public String getBotToken() {
        return token;
    }
}
