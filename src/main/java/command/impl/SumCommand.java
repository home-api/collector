package command.impl;

import com.google.inject.Inject;
import command.Command;
import dao.OrderDAO;
import org.telegram.telegrambots.api.objects.Message;

public class SumCommand implements Command {

    @Inject
    private OrderDAO orderDAO;

    @Override
    public String execute(String commandText, Message message) {
        return orderDAO.getOrder();
    }
}
