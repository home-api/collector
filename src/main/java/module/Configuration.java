package module;

import bot.SushiBot;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import command.impl.ClearCommand;
import command.impl.OrderCommand;
import command.impl.SumCommand;
import dao.OrderDAO;
import dao.impl.FileOrderDAOImpl;

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
        bind(ClearCommand.class).annotatedWith(Names.named("clear")).to(ClearCommand.class);
        bind(OrderCommand.class).annotatedWith(Names.named("order")).to(OrderCommand.class);
        bind(SumCommand.class).annotatedWith(Names.named("sum")).to(SumCommand.class);

        //controllers
        bind(SushiBot.class).annotatedWith(Names.named("bot")).to(SushiBot.class);
    }
}
