package io.github.gutyerrez.economy.command;

import io.github.gutyerrez.core.shared.CoreProvider;
import io.github.gutyerrez.core.shared.commands.CommandRestriction;
import io.github.gutyerrez.core.shared.misc.utils.ChatColor;
import io.github.gutyerrez.core.shared.user.User;
import io.github.gutyerrez.core.spigot.commands.CustomCommand;
import io.github.gutyerrez.economy.Currency;
import io.github.gutyerrez.economy.EconomyAPI;
import io.github.gutyerrez.economy.EconomyProvider;
import io.github.gutyerrez.economy.command.impl.*;
import net.luckperms.api.node.ChatMetaType;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * @author SrGutyerrez
 */
public class CurrencyCommand extends CustomCommand
{

    private final Currency currency;

    public CurrencyCommand(Currency currency)
    {
        super(
                currency.getCommandName(),
                CommandRestriction.CONSOLE_AND_IN_GAME,
                currency.getCommandAliases()
        );

        this.currency = currency;

        this.registerSubCommand(new CurrencySetSubCommand(this.currency));
        this.registerSubCommand(new CurrencyTopSubCommand(this.currency));
        this.registerSubCommand(new CurrencyAddSubCommand(this.currency));
        this.registerSubCommand(new CurrencySendSubCommand(this.currency));
        this.registerSubCommand(new CurrencyRemoveSubCommand(this.currency));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        if (sender instanceof ConsoleCommandSender) {
            return;
        }

        if (args.length == 1) {
            String targetName = args[0];
            User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetName);

            if (targetUser == null) {
                sender.sendMessage("§cEste usuário não existe.");
                return;
            }

            BigDecimal coins = EconomyAPI.get(targetUser, this.currency);

            if (coins == null) {
                sender.sendMessage("§cEste usuário não existe");
                return;
            }

            sender.sendMessage(String.format(
                    "§aCoins de §f%s: §7%s.",
                    ChatColor.translateAlternateColorCodes(
                            '&',
                            EconomyProvider.Hooks.CHAT.getChatMeta(
                                    targetName,
                                    ChatMetaType.PREFIX
                            )
                    ) + targetName,
                    this.currency.format(coins)
            ));
            return;
        }

        Player player = (Player) sender;
        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        BigDecimal coins = EconomyAPI.get(user, this.currency);

        sender.sendMessage("§aSeus coins §7" + this.currency.format(coins) + ".");
    }

}
