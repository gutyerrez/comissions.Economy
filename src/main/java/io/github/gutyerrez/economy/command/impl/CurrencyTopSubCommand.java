package io.github.gutyerrez.economy.command.impl;

import com.google.common.collect.Maps;
import io.github.gutyerrez.core.shared.commands.CommandRestriction;
import io.github.gutyerrez.core.shared.misc.utils.ChatColor;
import io.github.gutyerrez.core.shared.misc.utils.NumberUtils;
import io.github.gutyerrez.economy.Currency;
import io.github.gutyerrez.economy.EconomyPlugin;
import io.github.gutyerrez.economy.EconomyProvider;
import io.github.gutyerrez.economy.command.CurrencySubCommand;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author SrGutyerrez
 */
public class CurrencyTopSubCommand extends CurrencySubCommand {

    private final Currency currency;
    private final LinkedHashMap<String, Double> top = Maps.newLinkedHashMap();

    private Long cooldown;

    public CurrencyTopSubCommand(Currency currency) {
        super("top", CommandRestriction.CONSOLE_AND_IN_GAME);

        this.currency = currency;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(this.cooldown == null || this.cooldown < System.currentTimeMillis()){
            this.top.clear();;
            this.top.putAll(EconomyProvider.Repositories.ECONOMY.provide().fetchTop(this.currency));
            this.cooldown = System.currentTimeMillis() + 5000L;
        }

        StringBuilder message = new StringBuilder();


        Bukkit.getScheduler().runTaskAsynchronously(
                EconomyPlugin.getInstance(),
                () -> {
                    message.append("\n")
                            .append("§2Top 10 jogadores mais ricos §7(Atualizado a cada 5 minutos)")
                            .append("\n \n");

                    AtomicInteger count = new AtomicInteger(1);

                    this.top.forEach((username, value) -> {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
                        String coins = NumberUtils.format(value);

                        message.append(String.format(
                                "  §f%sº §7%s §7($ %s)\n",
                                count.getAndIncrement(),
                                ChatColor.translateAlternateColorCodes(
                                        '&',
                                        EconomyProvider.Hooks.CHAT.get().getPlayerPrefix("world", offlinePlayer.getName())
                                ) + offlinePlayer.getName(),
                                coins
                        ));
                    });

                    message.append("\n ");

                    sender.sendMessage(message.toString());
                }
        );
    }

}