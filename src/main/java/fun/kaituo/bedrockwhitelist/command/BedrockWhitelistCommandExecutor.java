package fun.kaituo.bedrockwhitelist.command;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import fun.kaituo.bedrockwhitelist.BedrockWhitelist;
import fun.kaituo.bedrockwhitelist.utils.GeyserApiRequester;
import fun.kaituo.bedrockwhitelist.utils.WhitelistEntry;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BedrockWhitelistCommandExecutor implements CommandExecutor {
    private static final String WHITELIST_FILE_PATH = "./whitelist.json";
    private static final int UUID_LENGTH = 32;
    private final BedrockWhitelist plugin;
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

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

    private List<WhitelistEntry> getWhitelist() throws Exception {
        File file = new File(WHITELIST_FILE_PATH);
        if (!file.exists()) {
            throw new IllegalStateException("The whitelist file does not exist!");
        }
        String whitelistString = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
        JsonArray array = JsonParser.parseString(whitelistString).getAsJsonArray();
        List<WhitelistEntry> entries = new ArrayList<>();
        for (JsonElement e : array) {
            entries.add(GSON.fromJson(e, WhitelistEntry.class));
        }
        return entries;
    }

    private void saveWhitelist(List<WhitelistEntry> whitelist) throws Exception {
        File file = new File(WHITELIST_FILE_PATH);
        if (!file.exists()) {
            throw new IllegalStateException("The whitelist file does not exist!");
        }
        JsonArray array = new JsonArray();
        for (WhitelistEntry e : whitelist) {
            array.add(GSON.toJsonTree(e, WhitelistEntry.class));
        }
        FileUtils.writeStringToFile(file, GSON.toJson(array), StandardCharsets.UTF_8);
    }

    private void addBedrockWhitelist(CommandSender commandSender, UUID uuid, String name) {
        try {
            List<WhitelistEntry> whitelist = getWhitelist();
            for (WhitelistEntry e : whitelist) {
                if (UUID.fromString(e.getUuid()).equals(uuid)) {
                    commandSender.sendMessage("§c该玩家已经在白名单列表");
                    return;
                }
            }
            whitelist.add(new WhitelistEntry(uuid.toString(), name));
            saveWhitelist(whitelist);
            Bukkit.reloadWhitelist();
            commandSender.sendMessage("成功添加该玩家至白名单列表");
        } catch (Exception e) {
            commandSender.sendMessage("§c发生内部错误，请联系管理员处理");
            e.printStackTrace();
        }
    }

    private void removeBedrockWhitelist(CommandSender commandSender, UUID uuid, String name) {
        try {
            List<Integer> indicesToBeRemoved = new ArrayList<>();
            List<WhitelistEntry> whitelist = getWhitelist();

            // Iterate from the back so that the indices are not messed up during removal
            for (int i = whitelist.size() - 1; i >= 0; i -= 1) {
                if (UUID.fromString(whitelist.get(i).getUuid()).equals(uuid)) {
                    indicesToBeRemoved.add(i);
                }
            }
            if (indicesToBeRemoved.isEmpty()) {
                commandSender.sendMessage("§c该玩家不在白名单列表");
                return;
            }
            for (int i : indicesToBeRemoved) {
                whitelist.remove(i);
            }
            saveWhitelist(whitelist);
            Bukkit.reloadWhitelist();
            commandSender.sendMessage("成功从白名单列表中移除该玩家");
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
        if (args.length < 2) {
            commandSender.sendMessage("§c指令参数错误！用法: /whitelistb add/remove <GamerTag>");
            return true;
        }
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            long xuid;
            StringBuilder gamerTag = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                gamerTag.append(args[i]);
                if (i < args.length - 1) {
                    gamerTag.append(" ");
                }
            }
            try {
                xuid = GeyserApiRequester.getXuid(gamerTag.toString().replaceAll(" ", "%20"));
            } catch (Exception e) {
                commandSender.sendMessage(
                        """
                                §c发生内部错误，请联系管理员处理
                                §f如果该玩家从未使用基岩版登录过，请让该玩家尝试进入服务器至少一次
                                §f玩家尝试进入后，等待约半小时后再次尝试添加即可
                                """);
                e.printStackTrace();
                return;
            }
            UUID uuid;
            try {
                uuid = getUuidFromXuid(xuid);
            } catch (Exception e) {
                commandSender.sendMessage(
                        """
                                §c发生内部错误，请联系管理员处理
                                §f如果该玩家从未使用基岩版登录过，请让该玩家尝试进入服务器至少一次
                                §f玩家尝试进入后，等待约半小时后再次尝试添加即可
                                """);
                e.printStackTrace();
                return;
            }
            switch (args[0]) {
                case "add" -> Bukkit.getScheduler().runTask(plugin, () -> addBedrockWhitelist(commandSender, uuid, plugin.getBedrockNamePrefix() + gamerTag.toString().replaceAll(" ", "_")));
                case "remove" -> Bukkit.getScheduler().runTask(plugin, () -> removeBedrockWhitelist(commandSender, uuid, plugin.getBedrockNamePrefix() + gamerTag.toString().replaceAll(" ", "_")));
                default -> commandSender.sendMessage("§c指令参数错误!用法: /whitelistb add/remove <GamerTag>");
            }
        });
        return true;
    }
}
