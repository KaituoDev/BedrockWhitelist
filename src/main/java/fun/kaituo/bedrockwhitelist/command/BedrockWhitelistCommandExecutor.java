package fun.kaituo.bedrockwhitelist.command;

import fun.kaituo.bedrockwhitelist.BedrockWhitelist;
import fun.kaituo.bedrockwhitelist.utils.GeyserApiRequester;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class BedrockWhitelistCommandExecutor implements CommandExecutor {
    private static final int UUID_LENGTH = 32;
    private final BedrockWhitelist plugin;

    public BedrockWhitelistCommandExecutor(BedrockWhitelist plugin) {
         this.plugin = plugin;
    }

    private String padLeftZeros(String inputString) {
        if (inputString.length() >= BedrockWhitelistCommandExecutor.UUID_LENGTH) {
            return inputString;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < BedrockWhitelistCommandExecutor.UUID_LENGTH - inputString.length()) {
            sb.append('0');
        }
        sb.append(inputString);

        return sb.toString();
    }

    private UUID getUuidFromXuid(long Xuid) {
        String hexString = Long.toHexString(Xuid);
        String uuidString = padLeftZeros(hexString);
        String sb = uuidString.substring(0, 8) +
                "-" +
                uuidString.substring(8, 12) +
                "-" +
                uuidString.substring(12, 16) +
                "-" +
                uuidString.substring(16, 20) +
                "-" +
                uuidString.substring(20, 32);
        return UUID.fromString(sb);
    }
    private void addBedrockWhitelist(CommandSender commandSender, UUID uuid) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (player.isWhitelisted()) {
                commandSender.sendMessage("§c该玩家已经在白名单列表");
            } else {
                player.setWhitelisted(true);
                commandSender.sendMessage("成功添加该玩家至白名单列表");
            }
        } catch (Exception e) {
            commandSender.sendMessage("§c发生内部错误，请联系管理员处理");
            e.printStackTrace();
        }
    }

    private void removeBedrockWhitelist(CommandSender commandSender, UUID uuid) {
        try {
            OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
            if (!player.isWhitelisted()) {
                commandSender.sendMessage("§c该玩家不在白名单列表");
            } else {
                player.setWhitelisted(true);
                commandSender.sendMessage("成功从白名单列表中移除该玩家");
            }
        } catch (Exception e) {
            commandSender.sendMessage("§c发生内部错误，请联系管理员处理");
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("whitelistb")) {
            return false;
        }
        if (args.length != 2) {
            commandSender.sendMessage("§c指令参数错误！用法: /whitelistb add/remove <GamerTag>");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            long xuid;
            try {
                xuid = GeyserApiRequester.getXuid(args[1]);
            } catch (Exception e) {
                commandSender.sendMessage("§c发生内部错误，请联系管理员处理");
                e.printStackTrace();
                return;
            }
            UUID uuid;
            try {
                uuid = getUuidFromXuid(xuid);
            } catch (Exception e) {
                commandSender.sendMessage("§c发生内部错误，请联系管理员处理");
                e.printStackTrace();
                return;
            }
            switch (args[0]) {
                case "add" -> Bukkit.getScheduler().runTask(plugin, () -> addBedrockWhitelist(commandSender, uuid));
                case "remove" -> Bukkit.getScheduler().runTask(plugin, () -> removeBedrockWhitelist(commandSender, uuid));
                default -> commandSender.sendMessage("§c指令参数错误!用法: /whitelistb add/remove <GamerTag>");
            }
        });
        return true;
    }
}
