package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import dao.OrderDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Singleton
public class SumCommand implements Command {

    private OrderDAO orderDAO;

    @Inject
    public SumCommand(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        return getSendMessage(orderDAO.getOrder(), message);
    }
}
