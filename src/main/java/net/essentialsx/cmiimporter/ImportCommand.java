package net.essentialsx.cmiimporter;

import net.essentialsx.cmiimporter.migrations.Migration;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;
import java.util.Set;

public class ImportCommand implements CommandExecutor {

    private final CMIImporter plugin;
    private final String availableMessage;

    public ImportCommand(CMIImporter plugin) {
        this.plugin = plugin;
        this.availableMessage = String.format("&7Available migrations: %s", String.join(", ", plugin.getMigrations().getAvailable()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // should this only be run from console?

        if (args.length == 0) {
            reply(sender, availableMessage);
            return true;
        }

        // maybe we can also have an "all" argument that runs every migration

        Set<String> available = plugin.getMigrations().getAvailable();
        for (String arg : args) {
            if (!available.contains(arg.toLowerCase())) {
                reply(sender, String.format("&cInvalid migration: \"%s\"", arg));
                reply(sender, availableMessage);
                return true;
            }
        }

        reply(sender, "&7Migrating data, please wait...");

        List<Migration> migrations = plugin.getMigrations().getApplicable(args);
        migrations.forEach(Migration::run);

        reply(sender, "&aMigration complete.");

        return true;
    }

    private void reply(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
