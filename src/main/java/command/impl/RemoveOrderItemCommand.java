package command.impl;

import com.google.inject.Inject;
import command.Command;
import repository.OrderRepository;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public class RemoveOrderItemCommand implements Command {

    private OrderRepository orderRepository;

    @Inject
    private RemoveOrderItemMenuCommand removeOrderItemMenuCommand;

    @Inject
    public RemoveOrderItemCommand(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SendMessage execute(String orderItem, Message message) throws Exception {
        boolean hasBeenRemoved = orderRepository.removeCustomerOrderItem(message.getChat().getFirstName().trim(), orderItem);
        String responseText = hasBeenRemoved ? orderItem + " был удален" : orderItem + " не был удален";
        return removeOrderItemMenuCommand.execute(null, message);
    }
}
