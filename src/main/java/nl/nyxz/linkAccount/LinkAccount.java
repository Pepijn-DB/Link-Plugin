package nl.nyxz.linkAccount;

import com.google.inject.Inject;
import com.velocitypowered.api.command.BrigadierCommand;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import nl.nyxz.linkAccount.commands.LinkCommand;
import org.slf4j.Logger;

@Plugin(id = "linkaccount", name = "LinkAccount", version = "1.0-SNAPSHOT")
public class LinkAccount {

    @Inject
    private Logger logger;

    public static LuckPerms luckperms;

    private final ProxyServer proxy;

    @Inject
    public LinkAccount(ProxyServer proxy) {
        this.proxy = proxy;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        registerCommands();
        try {
            LinkAccount.luckperms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            System.err.println("Failed to get LuckPerms instance!");
        }
    }

    public void registerCommands() {
        CommandManager commandManager = proxy.getCommandManager();
        CommandMeta commandMeta = commandManager.metaBuilder("link")
                .plugin(this)
                .build();

        commandManager.register(commandMeta, new LinkCommand());
    }
}
