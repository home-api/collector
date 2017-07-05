package configuration;

import bot.CollectorBot;
import com.google.inject.AbstractModule;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.name.Names;
import com.mongodb.MongoClient;
import command.Command;
import command.impl.ClearCommand;
import command.impl.DeleteOrderItemCommand;
import command.impl.MenuCommand;
import command.impl.OrderCommand;
import command.impl.RemoveOrderItemMenuCommand;
import command.impl.RepeatOrderCommand;
import command.impl.SumCommand;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import repository.OrderDAO;
import repository.impl.OrderDAOImpl;
import service.OrderService;
import service.impl.OrderServiceImpl;
import util.Emoji;
import util.Menu;

public class Configuration extends AbstractModule {

    private String token;

    public Configuration(String token) {
        this.token = token;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("token")).to(token);

        MongoClient mongoClient = new MongoClient("localhost", 27017);

        Morphia morphia = new Morphia();
        morphia.mapPackage("model");
        Datastore datastore = morphia.createDatastore(mongoClient, "orders");

        //repositories
        bind(OrderDAO.class).toInstance(new OrderDAOImpl(datastore));

        //services
        bind(OrderService.class).toInstance(new OrderServiceImpl());

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
        commandsBinder.addBinding(Emoji.ORDER_ITEM_REMOVE.toString()).to(DeleteOrderItemCommand.class);
        commandsBinder.addBinding(Emoji.REPEAT_ORDER.toString()).to(RepeatOrderCommand.class);

        //controllers
        bind(CollectorBot.class).annotatedWith(Names.named("bot")).to(CollectorBot.class);

        //others
        bind(Menu.class).asEagerSingleton();
    }
}
