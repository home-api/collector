package command.impl;

import com.google.inject.Inject;
import command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import service.OrderService;
import util.Emoji;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RemoveOrderItemMenuCommand implements Command {

    @Inject
    private OrderService orderService;

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        ArrayList<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        replyMarkup.setKeyboard(keyboard);

        SendMessage response = new SendMessage();
        response.setReplyMarkup(replyMarkup);

        String userName = message.getChat().getFirstName().trim();
        List<Map<String, Double>> userOrders = orderService.getCustomerOrder(userName);

        for (Map<String, Double> customerOrder : userOrders) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            String text = Emoji.ORDER_ITEM_REMOVE + customerOrder.keySet().stream().findFirst().get();
            button.setText(text);
            button.setCallbackData(text);
            keyboard.add(Collections.singletonList(button));
        }

        response.setText("Ваш заказ: " + (userOrders.isEmpty() ? "<пусто>" : ""));
        response.setChatId(message.getChatId());
        return response;
    }
}
