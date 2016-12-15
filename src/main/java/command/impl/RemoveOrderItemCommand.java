package command.impl;

import com.google.inject.Inject;
import command.Command;
import dao.OrderDAO;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class RemoveOrderItemCommand implements Command {

    private OrderDAO orderDAO;

    @Inject
    private RemoveOrderItemMenuCommand removeOrderItemMenuCommand;

    @Inject
    public RemoveOrderItemCommand(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public SendMessage execute(String orderItem, Message message) throws Exception {
        boolean hasBeenRemoved = orderDAO.removeOrderItem(message.getChat().getFirstName().trim(), orderItem);
        String responseText = hasBeenRemoved ? orderItem + " был удален" : orderItem + " не был удален";
        return removeOrderItemMenuCommand.execute(null, message);
    }
}
