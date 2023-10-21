package fun.kaituo.bedrockwhitelist;

import fun.kaituo.bedrockwhitelist.command.BedrockWhitelistCommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

public class BedrockWhitelist extends JavaPlugin {
    BedrockWhitelistTabCompleter tabCompleter;
    BedrockWhitelistCommandExecutor executor;

    public void onEnable() {
        executor = new BedrockWhitelistCommandExecutor(this);
        tabCompleter = new BedrockWhitelistTabCompleter();
        getCommand("whitelistb").setExecutor(executor);
        getCommand("whitelistb").setTabCompleter(tabCompleter);
    }
}
