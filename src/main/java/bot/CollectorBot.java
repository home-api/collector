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
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

        if (update.hasInlineQuery()) {
            handleInlineQuery(update.getInlineQuery());
            return;
        }

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
        if (doCleaning && currentDate.getDayOfYear() != date.getDayOfYear()) {
            LOGGER.info("Removing all orders..");
            orderDAO.removeAllOrders();
            date = currentDate;
        }
    }

    private void handleInlineQuery(InlineQuery inlineQuery) {
        String query = inlineQuery.getQuery();
        if (!query.isEmpty()) {
            try {
                answerInlineQuery(convertResultsToResponse(inlineQuery));
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private static AnswerInlineQuery convertResultsToResponse(InlineQuery inlineQuery) {
        AnswerInlineQuery answerInlineQuery = new AnswerInlineQuery();
        answerInlineQuery.setInlineQueryId(inlineQuery.getId());
        answerInlineQuery.setResults(convertInlineResults(inlineQuery.getQuery()));
        return answerInlineQuery;
    }

    private static List<InlineQueryResult> convertInlineResults(String query) {
        List<InlineQueryResult> results = new ArrayList<>();

        InputTextMessageContent messageContent = new InputTextMessageContent();
        messageContent.disableWebPagePreview();
        messageContent.enableMarkdown(true);
        messageContent.setMessageText("inline query - " + query);
        InlineQueryResultArticle article = new InlineQueryResultArticle();
        article.setInputMessageContent(messageContent);
        article.setId("test id");
        article.setTitle("test title");
        article.setDescription("test description");
        article.setThumbUrl("test thumb url");
        results.add(article);

        return results;
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
