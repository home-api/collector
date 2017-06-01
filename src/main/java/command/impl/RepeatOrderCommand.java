package command.impl;

import com.google.inject.Inject;
import command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import service.OrderService;

public class RepeatOrderCommand implements Command {

    @Inject
    private OrderService orderService;

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        String customer = message.getFrom().getFirstName().trim();
        boolean wasRepeated = orderService.repeatOrder(customer);
        String responseText = wasRepeated ? "Предыдущий заказ был добавлен" : "Мы не нашли вышего предыдущего заказа";
        return createSendMessage(responseText, message);
    }
}
