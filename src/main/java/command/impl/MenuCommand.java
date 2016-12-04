package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import dao.OrderDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import util.Constants;
import util.Emoji;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Singleton
public class MenuCommand implements Command {

    private OrderDAO orderDAO;

    private Map<String, ReplyKeyboardMarkup> subMenus;

    private ReplyKeyboardMarkup mainKeyboardMarkup;

    @Inject
    public MenuCommand(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;

        ArrayList<KeyboardRow> mainKeyboard = new ArrayList<>();
        subMenus = new HashMap<>();
        Map<String, Map<String, BigDecimal>> allFood = orderDAO.getAllFood();
        for (Map.Entry<String, Map<String, BigDecimal>> group : allFood.entrySet()) {
            String menu = group.getKey();
            KeyboardRow row = new KeyboardRow();
            row.add(Emoji.FILE_FOLDER + menu);
            mainKeyboard.add(row);
            subMenus.put(menu, getKeyboard(group.getValue()));
        }

        KeyboardRow row = new KeyboardRow();
        row.add(Emoji.CASH + Constants.SUM);
        row.add(Emoji.TOILET + Constants.CLEAN);
        mainKeyboard.add(row);

        mainKeyboardMarkup = new ReplyKeyboardMarkup();
        mainKeyboardMarkup.setKeyboard(mainKeyboard);
    }

    private static String getBackCommand() {
        return Emoji.BACK_WITH_LEFTWARDS_ARROW_ABOVE + Constants.BACK;
    }

    private ReplyKeyboardMarkup getKeyboard(Map<String, BigDecimal> food) {
        List<KeyboardRow> keyboard = new ArrayList<>();
        food.forEach((sushi, price) -> {
            KeyboardRow row = new KeyboardRow();
            row.add(Emoji.SUSHI + sushi + " (" + price + ")");
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

    @Override
    public SendMessage execute(String selectedMenu, Message message) throws Exception {
        if (selectedMenu.equals(Constants.BACK)) {
            return onBackMenu(message);
        } else {
            return onSelectMenu(selectedMenu, message);

        }
    }

    private SendMessage onBackMenu(Message message) throws Exception {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setReplyMarkup(mainKeyboardMarkup);
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setChatId(message.getChatId());
        sendMessage.setText("Главное меню");
        return sendMessage;
    }

    private SendMessage onSelectMenu(String selectedMenu, Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setReplyMarkup(subMenus.get(selectedMenu));
        sendMessage.setText("Сделайте выбор");
        return sendMessage;
    }

    public ReplyKeyboardMarkup getMainKeyboardMarkup() {
        return mainKeyboardMarkup;
    }
}
