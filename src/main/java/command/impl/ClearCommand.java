package command.impl;

import com.google.inject.Inject;
import command.Command;
import dao.OrderDAO;
import org.telegram.telegrambots.api.objects.Message;

public class ClearCommand implements Command {

    @Inject
    private OrderDAO orderDAO;

    @Override
    public String execute(String commandText, Message message) {
        return orderDAO.removeOrder(message.getFrom().getFirstName().trim())
                ? "Ваши заказы были удалены"
                : "Хм, что-то не так..";
    }
}
