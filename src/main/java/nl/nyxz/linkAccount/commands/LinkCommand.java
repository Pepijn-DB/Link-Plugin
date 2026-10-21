package nl.nyxz.linkAccount.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.MetaNode;
import nl.nyxz.linkAccount.LinkAccount;

import java.security.SecureRandom;
import java.util.HexFormat;

public class LinkCommand implements SimpleCommand {

    @Override
    public void execute(final Invocation invocation) {
        CommandSource source = invocation.source();

        if(!(source instanceof Player)) {
            source.sendMessage(Component.text("This command can only be used by players", NamedTextColor.RED));
            return;
        }

        User user = LinkAccount.luckperms.getPlayerAdapter(Player.class).getUser ((Player) source);

        if (user.getNodes().stream().anyMatch(node -> (node.getType().equals(NodeType.META) && node.getKey().contains("discord_account")))) {
            source.sendMessage(Component.text("You have already linked your account!", NamedTextColor.RED));
            return;
        }

        user.getNodes().stream().filter(node -> (node.getType().equals(NodeType.META) && node.getKey().contains("discord_verification_code"))).forEach(node -> user.data().remove(node));   


        String linkCode = randomHex(6);
        MetaNode node = MetaNode.builder("discord_verification_code", linkCode).build();

        DataMutateResult result = user.data().add(node);

        if (result.wasSuccessful()) {
            source.sendMessage(
                    Component.text("Your link code is: ", NamedTextColor.GREEN)
                            .append(
                                    Component.text(linkCode, NamedTextColor.YELLOW)
                                            .clickEvent(ClickEvent.copyToClipboard(linkCode))
                                            .hoverEvent(HoverEvent.showText(
                                                    Component.text("Click to copy!")
                                            ))
                            )
            );
        } else {
            source.sendMessage(Component.text("Failed to generate your link code.", NamedTextColor.RED));
            return;
        }

        LinkAccount.luckperms.getUserManager().saveUser(user);
    }

//    @Override
//    public boolean hasPermission(final Invocation invocation) {
//        return invocation.source().hasPermission("command.test");
//    }


    public static String randomHex(int bytes) {
        SecureRandom RANDOM = new SecureRandom();

        byte[] data = new byte[bytes];
        RANDOM.nextBytes(data);
        return HexFormat.of().formatHex(data);
    }

}
