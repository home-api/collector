package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Singleton
public class OrderCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommand.class);
    private static final java.lang.String ERROR_MESSAGE_PROPERTY_KEY = "errorMessage";

    private OrderRepository orderRepository;

    @Inject
    public OrderCommand(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        LOGGER.info("Order command - " + commandText);
        String customer = message.getFrom().getFirstName().trim();
        String order = commandText.split("\\(")[0].trim();
        boolean isOrderAdded = orderRepository.addOrder(customer, order);
        String responseText = isOrderAdded ? "Порция " + order + " была добавлена" : Property.get(ERROR_MESSAGE_PROPERTY_KEY);
        return getSendMessage(responseText, message);
    }
}
