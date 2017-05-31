package command.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import command.Command;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import service.OrderService;
import util.SumFormatter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Singleton
public class SumCommand implements Command {

    @Inject
    private OrderService orderService;

    @Override
    public SendMessage execute(String commandText, Message message) throws Exception {
        Map<String, List<Map<String, BigDecimal>>> orders = orderService.getAllOrders();
        return createSendMessage(SumFormatter.format(orders), message);
    }
}
