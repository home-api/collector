package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import util.SumFormatter;
import repository.OrderRepository;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Singleton
public class SumCommand implements Command {

    private OrderRepository orderRepository;

    @Inject
    public SumCommand(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        Map<String, List<Map<String, BigDecimal>>> orders = orderRepository.getAllOrders();
        return getSendMessage(SumFormatter.format(orders), message);
    }
}
