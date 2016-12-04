package command.impl;

import com.google.inject.Inject;
import command.Command;
import dao.OrderDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.api.objects.Message;

public class OrderCommand implements Command {

    @Inject
    private OrderDAO orderDAO;
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderCommand.class);

    @Override
    public String execute(String commandText, Message message) {
        LOGGER.info("Order command - " + commandText);
        String customer = message.getFrom().getFirstName().trim();
        String order = commandText.split("\\(")[0].trim();
        boolean isOrderAdded = orderDAO.addOrder(customer, order);
        return isOrderAdded ? "Порция " + order + " была добавлена" : "Не понял, сэр";
    }
}
