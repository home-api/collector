package command.impl;

import com.google.inject.Inject;
import command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import service.OrderService;

public class DeleteOrderItemCommand implements Command {

    @Inject
    private OrderService orderService;

    @Inject
    private RemoveOrderItemMenuCommand removeOrderItemMenuCommand;

    @Override
    public SendMessage execute(String orderItem, Message message) throws Exception {
        boolean hasBeenRemoved = orderService.deleteOrderItem(message.getChat().getFirstName().trim(), orderItem);
        String responseText = hasBeenRemoved ? orderItem + " был удален" : orderItem + " не был удален";
        return removeOrderItemMenuCommand.execute(null, message);
    }
}
