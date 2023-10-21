package fun.kaituo.bedrockwhitelist;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BedrockWhitelistTabCompleter implements TabCompleter {
    private final List<String> subCommands = Arrays.asList("add", "remove");
    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender commandSender, @NotNull Command command,
            @NotNull String alias, @NotNull String[] args) {
        if (!command.getName().equalsIgnoreCase("whitelistb")) {
            return null;
        }
        if (args.length != 1) {
            return null;
        }
        return subCommands.stream().filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
    }
}
