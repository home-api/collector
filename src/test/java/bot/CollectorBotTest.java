package bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import command.Command;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import service.OrderService;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CollectorBotTest {

    @InjectMocks
    private CollectorBot bot = new CollectorBot("test_token");

    @Mock
    private OrderService orderServiceMock;

    @Mock
    private Command menuCommandMock;

    @Mock
    private Map<String, Command> commandsMock;

    @Mock
    private Command commandMock;

    @Test
    public void handleOnlyPrivateMessages() throws Exception {
        // Given
        String command = new String(new char[]{'\uD83D', '\uDDB0'});

        when(commandsMock.get(eq(command))).thenReturn(commandMock);
        when(commandMock.execute(eq(command), any(Message.class))).thenReturn(new SendMessage());

        String request = "{" +
                "   \"update_id\":888457484," +
                "   \"message\":{" +
                "      \"message_id\":11," +
                "      \"from\":{" +
                "         \"id\":193289395," +
                "         \"first_name\":\"Yegor\"," +
                "         \"language_code\":\"en-BY\"" +
                "      }," +
                "      \"chat\":{" +
                "         \"id\":193289395," +
                "         \"first_name\":\"Yegor\"," +
                "         \"type\":\"private\"" +
                "      }," +
                "      \"date\":1496845315," +
                "      \"text\":\"" + command + "\"" +
                "   }" +
                "}";
        Update update = new ObjectMapper().readValue(request, Update.class);

        // When, Then
        bot.onUpdateReceived(update);
    }

    @Test
    public void ignoreMessagesOnGroupChats() {
        // Given
        String request = "{" +
                "   \"ok\":true," +
                "   \"result\":[" +
                "      {  \n" +
                "         \"update_id\":888457493," +
                "         \"message\":{" +
                "            \"message_id\":25," +
                "            \"from\":{" +
                "               \"id\":193289395," +
                "               \"first_name\":\"Yegor\"," +
                "               \"language_code\":\"en-BY\"" +
                "            }," +
                "            \"chat\":{" +
                "               \"id\":-145104124," +
                "               \"title\":\"\\u0422\\u0435\\u0441\\u0442\"," +
                "               \"type\":\"group\"," +
                "               \"all_members_are_administrators\":true" +
                "            }," +
                "            \"date\":1497258697," +
                "            \"reply_to_message\":{" +
                "               \"message_id\":24," +
                "               \"from\":{" +
                "                  \"id\":394497421," +
                "                  \"first_name\":\"SushiiTest\"," +
                "                  \"username\":\"SushiiTestBot\"" +
                "               }," +
                "               \"chat\":{" +
                "                  \"id\":-145104124," +
                "                  \"title\":\"\\u0422\\u0435\\u0441\\u0442\"," +
                "                  \"type\":\"group\"," +
                "                  \"all_members_are_administrators\":true" +
                "               }," +
                "               \"date\":1497258670," +
                "               \"text\":\"\"" +
                "            }," +
                "            \"text\":\"\\ud83d\\udcb0\"" +
                "         }" +
                "      }" +
                "   ]" +
                "}";

        // When, Then
        verifyZeroInteractions(orderServiceMock, menuCommandMock, commandsMock);
    }

}
