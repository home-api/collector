package configuration;

import bot.CollectorBot;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import command.Command;
import command.impl.ClearCommand;
import command.impl.MenuCommand;
import command.impl.OrderCommand;
import command.impl.RemoveOrderItemCommand;
import command.impl.RemoveOrderItemMenuCommand;
import command.impl.SumCommand;
import repository.MenuRepository;
import repository.OrderRepository;
import repository.impl.InMemoryOrderRepository;
import repository.impl.PropertiesMenuRepository;
import util.Emoji;

public class Configuration extends AbstractModule {

    private String token;
    private Boolean doCleaning;

    public Configuration(String token, Boolean doCleaning) {
        this.token = token;
        this.doCleaning = doCleaning;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("token")).to(token);
        bindConstant().annotatedWith(Names.named("doCleaning")).to(doCleaning);

        //repositories
        bind(OrderRepository.class).to(InMemoryOrderRepository.class);
        bind(MenuRepository.class).to(PropertiesMenuRepository.class);

        //commands
        bind(Command.class).annotatedWith(Names.named("clear")).to(ClearCommand.class);
        bind(Command.class).annotatedWith(Names.named("order")).to(OrderCommand.class);
        bind(Command.class).annotatedWith(Names.named("sum")).to(SumCommand.class);
        bind(Command.class).annotatedWith(Names.named("menu")).to(MenuCommand.class);

        MapBinder<String, Command> commandsBinder = MapBinder.newMapBinder(binder(), String.class, Command.class);
        commandsBinder.addBinding(Emoji.ORDER_POSITION.toString()).to(OrderCommand.class);
        commandsBinder.addBinding(Emoji.SUBMENU_ITEM.toString()).to(MenuCommand.class);
        commandsBinder.addBinding(Emoji.MENU_BACK.toString()).to(MenuCommand.class);
        commandsBinder.addBinding(Emoji.ORDER_SUM.toString()).to(SumCommand.class);
        commandsBinder.addBinding(Emoji.ORDER_RESET.toString()).to(ClearCommand.class);
        commandsBinder.addBinding(Emoji.ORDER_EDIT.toString()).to(RemoveOrderItemMenuCommand.class);
        commandsBinder.addBinding(Emoji.ORDER_ITEM_REMOVE.toString()).to(RemoveOrderItemCommand.class);

        //controllers
        bind(CollectorBot.class).annotatedWith(Names.named("bot")).to(CollectorBot.class);
    }
}
