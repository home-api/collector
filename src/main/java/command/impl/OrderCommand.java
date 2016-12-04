package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

@Singleton
public class OrderCommand implements Command {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommand.class);

    private OrderDAO orderDAO;

    @Inject
    public OrderCommand(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        LOGGER.info("Order command - " + commandText);
        String customer = message.getFrom().getFirstName().trim();
        String order = commandText.split("\\(")[0].trim();
        boolean isOrderAdded = orderDAO.addOrder(customer, order);
        String responseText = isOrderAdded ? "Порция " + order + " была добавлена" : "Не понял, сэр";
        return getSendMessage(responseText, message);
    }
}
