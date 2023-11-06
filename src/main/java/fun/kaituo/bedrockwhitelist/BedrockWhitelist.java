package fun.kaituo.bedrockwhitelist;

import fun.kaituo.bedrockwhitelist.command.BedrockWhitelistCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class BedrockWhitelist extends JavaPlugin {
    private String bedrockNamePrefix;

    public String getBedrockNamePrefix() {
        return bedrockNamePrefix;
    }

    public void onEnable() {
        saveDefaultConfig();
        bedrockNamePrefix = getConfig().getString("bedrock-name-prefix");

        BedrockWhitelistCommandExecutor executor = new BedrockWhitelistCommandExecutor(this);
        BedrockWhitelistTabCompleter tabCompleter = new BedrockWhitelistTabCompleter();
        getCommand("whitelistb").setExecutor(executor);
        getCommand("whitelistb").setTabCompleter(tabCompleter);
    }
}
