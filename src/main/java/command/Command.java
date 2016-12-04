package command;

import org.telegram.telegrambots.api.objects.Message;

public interface Command {
    String execute(String commandText, Message message);
}
