package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import repository.OrderRepository;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Singleton
public class ClearCommand implements Command {

    private OrderRepository orderRepository;

    @Inject
    public ClearCommand(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        String responseText = orderRepository.removeCustomerOrder(message.getFrom().getFirstName().trim())
                ? "Ваши заказы были удалены"
                : "Хм, что-то не так..";
        return getSendMessage(responseText, message);
    }
}
