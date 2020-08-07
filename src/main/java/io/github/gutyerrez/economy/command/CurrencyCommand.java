package io.github.gutyerrez.economy.command;

import io.github.gutyerrez.core.shared.CoreProvider;
import io.github.gutyerrez.core.shared.commands.CommandRestriction;
import io.github.gutyerrez.core.shared.user.User;
import io.github.gutyerrez.core.spigot.commands.CustomCommand;
import io.github.gutyerrez.economy.Currency;
import io.github.gutyerrez.economy.EconomyProvider;
import io.github.gutyerrez.economy.command.impl.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author SrGutyerrez
 */
public class CurrencyCommand extends CustomCommand {

    private final Currency currency;

    public CurrencyCommand(Currency currency) {
        super(
                currency.getCommandName(),
                CommandRestriction.IN_GAME,
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
    public void onCommand(CommandSender sender, String[] args) {
        if (args.length == 1) {
            String targetName = args[0];
            User targetUser = CoreProvider.Cache.Local.USERS.provide().get(targetName);

            if (targetUser == null) {
                sender.sendMessage("§cEste usuário não existe.");
                return;
            }

            Double coins = EconomyProvider.Repositories.ECONOMY.provide().get(targetUser, this.currency);

            sender.sendMessage(String.format(
                    "§aSaldo de §f%s§a: §f%s",
                    targetUser.getName(),
                    this.currency.format(coins)
            ));
            return;
        }

        Player player = (Player) sender;
        User user = CoreProvider.Cache.Local.USERS.provide().get(player.getName());

        Double coins = EconomyProvider.Repositories.ECONOMY.provide().get(user, this.currency);

        sender.sendMessage("§aSaldo: §f" + this.currency.format(coins));
    }
}