package io.github.gutyerrez.economy.command.impl;

import io.github.gutyerrez.core.shared.CoreProvider;
import io.github.gutyerrez.core.shared.commands.Argument;
import io.github.gutyerrez.core.shared.commands.CommandRestriction;
import io.github.gutyerrez.core.shared.misc.utils.ChatColor;
import io.github.gutyerrez.core.shared.user.User;
import io.github.gutyerrez.core.spigot.commands.CustomCommand;
import io.github.gutyerrez.economy.Currency;
import io.github.gutyerrez.economy.EconomyAPI;
import io.github.gutyerrez.economy.EconomyProvider;
import io.github.gutyerrez.economy.misc.utils.EconomyExecuteAction;
import net.luckperms.api.node.ChatMetaType;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

/**
 * @author SrGutyerrez
 */
public class CurrencySendSubCommand extends CustomCommand
{

    private final Currency currency;

    public CurrencySendSubCommand(Currency currency)
    {
        super(
                "pay",
                CommandRestriction.IN_GAME,
                "enviar"
        );

        this.currency = currency;

        this.registerArgument(new Argument("nick", "Nome do jogador."));
        this.registerArgument(new Argument("valor", "Quantidade de " + currency.getSingular() + "."));
    }

    @Override
    public void onCommand(CommandSender sender, String[] args)
    {
        Player player = (Player) sender;
        String targetName = args[0];

        if (player.getName().equalsIgnoreCase(targetName)) {
            sender.sendMessage("§cVocê não pode enviar coins para si mesmo.");
            player.playSound(player.getLocation(), Sound.VILLAGER_NO, 1, 1);
            return;
        }

        new EconomyExecuteAction(sender, this.currency, args)
        {
            @Override
            public void execute(User targetUser, Currency currency, BigDecimal amount)
            {
                User user = CoreProvider.Cache.Local.USERS.provide().get(sender.getName());

                BigDecimal coins = EconomyAPI.get(user, currency);

                if (coins.compareTo(amount) < 0) {
                    sender.sendMessage("§cVocê não possui coins suficientes.");
                    return;
                }

                EconomyAPI.remove(user, currency, amount);
                EconomyAPI.add(targetUser, currency, amount);

                sender.sendMessage(String.format(
                        "§eVocê enviou §f%s §epara §f%s§e.",
                        currency.format(amount),
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                EconomyProvider.Hooks.CHAT.getChatMeta(
                                        targetName,
                                        ChatMetaType.PREFIX
                                )
                        ) + targetName
                ));

                Player targetPlayer = Bukkit.getPlayerExact(targetName);

                if (targetPlayer != null) {
                    targetPlayer.sendMessage(String.format(
                            "§eVocê recebeu §f%s §ede §f%s§e.",
                            currency.format(amount),
                            ChatColor.translateAlternateColorCodes(
                                    '&',
                                    EconomyProvider.Hooks.CHAT.getChatMeta(
                                            sender.getName(),
                                            ChatMetaType.PREFIX
                                    )
                            ) + sender.getName()
                    ));
                }
            }
        };
    }
}
