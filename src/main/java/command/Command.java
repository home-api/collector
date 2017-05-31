package command;

import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;

public interface Command {

    SendMessage execute(String commandText, Message message) throws Exception;

    default SendMessage createSendMessage(String text, Message message) throws Exception {
        SendMessage response = new SendMessage();
        response.setText(text);
        response.setChatId(message.getChatId());
        return response;
    }
}
