package bot;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import command.Command;
import command.impl.ClearCommand;
import command.impl.OrderCommand;
import command.impl.SumCommand;
import dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import util.Emoji;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class is used only for initialization and comand parse.;
 */
public class SushiBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(SushiBot.class);

    private static final String SUSHI_PREFIX = "\uD83C\uDF63";
    private static final String ORDER_PREFIX = "\uD83D\uDCB0";

    private static Map<String, Command> COMMANDS;

    @Inject
    @Named("token")
    private String token;
    private LocalDateTime date = LocalDateTime.now();

    @Inject
    private OrderDAO orderDAO;

    @Inject
    @Named("clear")
    private ClearCommand clearCommand;
    @Inject
    @Named("order")
    private OrderCommand orderCommand;
    @Inject
    @Named("sum")
    private SumCommand sumCommand;

    private ReplyKeyboardMarkup garnirKeyboard;
    private ReplyKeyboardMarkup gunkanKeyboard;
    private ReplyKeyboardMarkup hotMakiKeyboard;
    private ReplyKeyboardMarkup miniRollsKeyboard;
    private ReplyKeyboardMarkup nigiriKeyboard;
    private ReplyKeyboardMarkup noriMakiKeyboard;
    private ReplyKeyboardMarkup soupsKeyboard;
    private ReplyKeyboardMarkup uraMakiKeyboard;

    private ReplyKeyboardMarkup mainKeyboardMarkup;

    public void initialize() throws Exception {
        initializeKeyBoards();
        initializeCommands();
    }

    private void initializeKeyBoards() {
        garnirKeyboard = getKeyboard(orderDAO.getGarnir());
        gunkanKeyboard = getKeyboard(orderDAO.getGunkan());
        hotMakiKeyboard = getKeyboard(orderDAO.getHotMaki());
        miniRollsKeyboard = getKeyboard(orderDAO.getMiniRolls());
        nigiriKeyboard = getKeyboard(orderDAO.getNigiri());
        noriMakiKeyboard = getKeyboard(orderDAO.getNoriMaki());
        soupsKeyboard = getKeyboard(orderDAO.getSoups());
        uraMakiKeyboard = getKeyboard(orderDAO.getUraMaki());

        ArrayList<KeyboardRow> mainKeyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Ура маки");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Нори маки");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Горячие маки");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Мини роллы");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Супы");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Нигири");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Гунканы");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(Emoji.FILE_FOLDER + "Гарниры");
        mainKeyboard.add(row);

        row = new KeyboardRow();
        row.add(ORDER_PREFIX + "Итого");
        row.add(Emoji.TOILET + "Очистить");
        mainKeyboard.add(row);

        mainKeyboardMarkup = new ReplyKeyboardMarkup();
        mainKeyboardMarkup.setKeyboard(mainKeyboard);
    }

    private ReplyKeyboardMarkup getKeyboard(Map<String, BigDecimal> food) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        food.forEach((k, v) -> {
            KeyboardRow row = new KeyboardRow();
            row.add(SUSHI_PREFIX + k + " (" + v + ")");
            keyboard.add(row);
        });

        KeyboardRow row = new KeyboardRow();
        row.add(getBackCommand());
        keyboard.add(row);

        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboad(false);
        replyKeyboardMarkup.setKeyboard(keyboard);

        return replyKeyboardMarkup;
    }


    private static String getBackCommand() {
        return Emoji.BACK_WITH_LEFTWARDS_ARROW_ABOVE + "Назад";
    }

    private void initializeCommands() {
        COMMANDS = new HashMap<>();
        COMMANDS.put(SUSHI_PREFIX, orderCommand);
        COMMANDS.put(ORDER_PREFIX, sumCommand);
        COMMANDS.put(Emoji.TOILET.toString(), clearCommand);
    }

    @Override
    public void onUpdateReceived(Update update) {
        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.getDayOfYear() != date.getDayOfYear()) {
            LOGGER.info("Removing all orders..");
            orderDAO.removeAllOrders();
            date = currentDate;
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

        if (commandPrefix.equals(Emoji.FILE_FOLDER.toString())) {
            sendChooseOptionMessage(message, commandText);
            return;
        } else if (commandPrefix.equals(Emoji.BACK_WITH_LEFTWARDS_ARROW_ABOVE.toString())) {
            onBackAlertCommand(message);
            return;
        }

        Command command = COMMANDS.get(commandPrefix);

        if (command == null) {
            sendHelpMessage("Не понял, сэр", message);
            LOGGER.info(message.getFrom().getFirstName() + " has sent incorrect command: " + commandPrefix);
            return;
        }

        String response = command.execute(commandText, message);
        sendMessage(response, message);
    }

    private void sendChooseOptionMessage(Message message, String replyKeyboard) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyToMessageId(message.getMessageId());

        switch (replyKeyboard) {
            case "Ура маки":
                sendMessage.setReplyMarkup(uraMakiKeyboard);
                break;
            case "Нори маки":
                sendMessage.setReplyMarkup(noriMakiKeyboard);
                break;
            case "Горячие маки":
                sendMessage.setReplyMarkup(hotMakiKeyboard);
                break;
            case "Мини роллы":
                sendMessage.setReplyMarkup(miniRollsKeyboard);
                break;
            case "Супы":
                sendMessage.setReplyMarkup(soupsKeyboard);
                break;
            case "Нигири":
                sendMessage.setReplyMarkup(nigiriKeyboard);
                break;
            case "Гунканы":
                sendMessage.setReplyMarkup(gunkanKeyboard);
                break;
            case "Гарниры":
                sendMessage.setReplyMarkup(garnirKeyboard);
                break;
        }

        sendMessage.setText("Сделайте выбор");
        sendMessage(sendMessage);
    }

    private void onBackAlertCommand(Message message) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(mainKeyboardMarkup);
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Главное меню");
        sendMessage(sendMessage);
    }

    private void sendHelpMessage(String greeting, Message message) throws Exception {
        SendMessage response = new SendMessage();
        response.enableMarkdown(true);
        response.setText(greeting);
        response.setChatId(message.getChatId());
        response.setReplyMarkup(mainKeyboardMarkup);
        sendMessage(response);
    }

    private void sendMessage(String text, Message message) throws Exception {
        SendMessage response = new SendMessage();
        response.setText(text);
        response.setChatId(message.getChatId());
        sendMessage(response);
    }

    public String getBotUsername() {
        return "";
    }

    public String getBotToken() {
        return token;
    }
}
