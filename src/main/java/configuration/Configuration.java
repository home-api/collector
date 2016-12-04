package configuration;

import bot.CollectorBot;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import command.Command;
import command.impl.ClearCommand;
import command.impl.MenuCommand;
import command.impl.OrderCommand;
import command.impl.SumCommand;
import dao.OrderDAO;
import dao.impl.FileOrderDAOImpl;
import util.Emoji;

public class Configuration extends AbstractModule {

    private String token;

    public Configuration(String token) {
        this.token = token;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("token")).to(token);

        //dao
        bind(OrderDAO.class).to(FileOrderDAOImpl.class);

        //commands
        bind(Command.class).annotatedWith(Names.named("clear")).to(ClearCommand.class);
        bind(Command.class).annotatedWith(Names.named("order")).to(OrderCommand.class);
        bind(Command.class).annotatedWith(Names.named("sum")).to(SumCommand.class);
        bind(Command.class).annotatedWith(Names.named("menu")).to(MenuCommand.class);

        MapBinder<String, Command> commandsBinder = MapBinder.newMapBinder(binder(), String.class, Command.class);
        commandsBinder.addBinding(Emoji.SUSHI.toString()).to(OrderCommand.class);
        commandsBinder.addBinding(Emoji.CASH.toString()).to(SumCommand.class);
        commandsBinder.addBinding(Emoji.TOILET.toString()).to(ClearCommand.class);
        commandsBinder.addBinding(Emoji.FILE_FOLDER.toString()).to(MenuCommand.class);
        commandsBinder.addBinding(Emoji.BACK_WITH_LEFTWARDS_ARROW_ABOVE.toString()).to(MenuCommand.class);

        //controllers
        bind(CollectorBot.class).annotatedWith(Names.named("bot")).to(CollectorBot.class);
    }
}
