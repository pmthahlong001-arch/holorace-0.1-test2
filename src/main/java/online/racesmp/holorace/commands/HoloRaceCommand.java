package online.racesmp.holorace.commands;

import online.racesmp.holorace.HoloRace;
import online.racesmp.holorace.models.PlayerData;
import online.racesmp.holorace.models.Race;
import online.racesmp.holorace.utils.ItemUtil;
import online.racesmp.holorace.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class HoloRaceCommand implements CommandExecutor {

    private final HoloRace plugin;

    public HoloRaceCommand(HoloRace plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "check" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MessageUtil.get(plugin, "only-player"));
                    return true;
                }
                PlayerData data = plugin.getPlayerDataManager().getData(player.getUniqueId());
                if (data == null || data.getCurrentRace() == null) {
                    player.sendMessage(MessageUtil.get(plugin, "no-race"));
                    return true;
                }
                Race race = plugin.getRaceManager().getRace(data.getCurrentRace());
                player.sendMessage(MessageUtil.format(plugin, "check-info",
                        "%race%", race != null ? race.getDisplayName() : data.getCurrentRace(),
                        "%history%", data.getHistory().isEmpty() ? "Chưa có" : String.join(", ", data.getHistory()),
                        "%remaining%", String.valueOf(data.getRemainingRandoms())
                ));
            }

            case "give" -> {
                if (!sender.hasPermission("holorace.give")) {
                    sender.sendMessage(MessageUtil.get(plugin, "no-permission"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(MessageUtil.get(plugin, "usage-give"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(MessageUtil.get(plugin, "player-not-found"));
                    return true;
                }
                int amount;
                try {
                    amount = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(MessageUtil.get(plugin, "invalid-number"));
                    return true;
                }
                for (int i = 0; i < amount; i++) {
                    target.getInventory().addItem(ItemUtil.buildRandomRaceItem(plugin));
                }
                sender.sendMessage(MessageUtil.format(plugin, "give-success",
                        "%player%", target.getName(), "%amount%", String.valueOf(amount)));
                target.sendMessage(MessageUtil.format(plugin, "received-item",
                        "%amount%", String.valueOf(amount)));
            }

            case "resetrace" -> {
                if (!sender.hasPermission("holorace.resetrace")) {
                    sender.sendMessage(MessageUtil.get(plugin, "no-permission"));
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage(MessageUtil.get(plugin, "usage-resetrace"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(MessageUtil.get(plugin, "player-not-found"));
                    return true;
                }
                plugin.getPlayerDataManager().resetRace(target.getUniqueId());
                plugin.getRaceManager().removeEffects(target);
                sender.sendMessage(MessageUtil.format(plugin, "reset-success",
                        "%player%", target.getName()));
                target.sendMessage(MessageUtil.get(plugin, "your-race-reset"));
            }

            case "chonrace" -> {
                if (!sender.hasPermission("holorace.chonrace")) {
                    sender.sendMessage(MessageUtil.get(plugin, "no-permission"));
                    return true;
                }
                if (!(sender instanceof Player player)) {
                    sender.sendMessage(MessageUtil.get(plugin, "only-player"));
                    return true;
                }
                plugin.getGUIManager().openRaceMenu(player);
            }

            case "help" -> sendHelp(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(MessageUtil.get(plugin, "help-header"));
        sender.sendMessage(MessageUtil.get(plugin, "help-check"));
        sender.sendMessage(MessageUtil.get(plugin, "help-give"));
        sender.sendMessage(MessageUtil.get(plugin, "help-resetrace"));
        sender.sendMessage(MessageUtil.get(plugin, "help-chonrace"));
        sender.sendMessage(MessageUtil.get(plugin, "help-footer"));
    }
}
