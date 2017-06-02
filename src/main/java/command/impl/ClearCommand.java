package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import service.OrderService;

@Singleton
public class ClearCommand implements Command {

    @Inject
    private OrderService orderService;

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        String responseText = orderService.deleteOrder(message.getFrom().getFirstName().trim())
                ? "Ваши заказы были удалены"
                : "Сначала закажЫ!11адын";
        return createSendMessage(responseText, message);
    }
}
