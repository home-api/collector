package bot;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import command.Command;
import command.impl.MenuCommand;
import dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    @Named("doCleaning")
    private Boolean doCleaning;

    @Inject
    private OrderDAO orderDAO;

    @Inject
    @Named("menu")
    private Command menuCommand;

    @Override
    public void onUpdateReceived(Update update) {
        cleanOutdatedOrders();

        try {
            if (update.hasInlineQuery()) {
                handleInlineQuery(update.getInlineQuery());
                return;
            }

            if (update.hasCallbackQuery()) {
                handleCallbackQuery(update);
                return;
            }

            if (!update.hasMessage()) {
                return;
            }

            Message message = update.getMessage();
            handleMessage(message);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

    }

    private void cleanOutdatedOrders() {
        LocalDateTime currentDate = LocalDateTime.now();
        if (doCleaning && currentDate.getDayOfYear() != date.getDayOfYear()) {
            LOGGER.info("Removing all orders..");
            orderDAO.removeAllOrders();
            date = currentDate;
        }
    }

    private void handleInlineQuery(InlineQuery inlineQuery) throws Exception {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setResults(convertInlineResults());
        answerInlineQuery(answerInlineQuery);
    }

    private List<InlineQueryResult> convertInlineResults() {
        // only one supported result - order sum
        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.disableWebPagePreview();
        messageContent.enableMarkdown(true);
        messageContent.setMessageText(orderDAO.getOrder());

        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setInputMessageContent(messageContent);
        article.setId("1");
        article.setTitle("Заказ");
        article.setDescription("Получить весь заказ");
        article.setThumbUrl("http://pngimg.com/upload/money_PNG3545.png");

        return Collections.singletonList(article);
    }

    private boolean handleCallbackQuery(Update update) throws Exception {
        String callbackData = update.getCallbackQuery().getData();
        String commandPrefix = getCommandPrefix(callbackData);
        String commandText = getCommandText(callbackData);

        Command command = commands.get(commandPrefix);

        Message message = update.getCallbackQuery().getMessage();
        if (command == null) {
            sendHelpMessage("Не понял, сэр", message);
            LOGGER.info(message.getChat().getFirstName().trim() + " has sent incorrect command: " + commandPrefix);
            return true;
        }

        SendMessage response = command.execute(commandText, message);
        sendMessage(response);
        return false;
    }

    private void handleMessage(Message message) throws Exception {
        if (!message.hasText()) {
            sendHelpMessage("Что пожелаете, сэр?", message);
        }

        String messageText = message.getText();
        LOGGER.info(message.getFrom().getFirstName() + " has sent " + messageText);

        String commandPrefix = getCommandPrefix(messageText);
        String commandText = getCommandText(messageText);

        Command command = commands.get(commandPrefix);

        if (command == null) {
            sendHelpMessage("Не понял, сэр", message);
            LOGGER.info(message.getFrom().getFirstName() + " has sent incorrect command: " + commandPrefix);
            return;
        }

        SendMessage response = command.execute(commandText, message);
        sendMessage(response);
    }

    private String getCommandText(String messageText) {
        return messageText.length() > 3 ? messageText.substring(2) : null;
    }

    private String getCommandPrefix(String messageText) {
        return messageText.length() >= 2 ? messageText.substring(0, 2) : messageText.substring(0, 1) + ' ';
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
