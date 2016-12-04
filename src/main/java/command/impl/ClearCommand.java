package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import dao.OrderDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Singleton
public class ClearCommand implements Command {

    private OrderDAO orderDAO;

    @Inject
    public ClearCommand(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        String responseText = orderDAO.removeOrder(message.getFrom().getFirstName().trim())
                ? "Ваши заказы были удалены"
                : "Хм, что-то не так..";
        return getSendMessage(responseText, message);
    }
}
