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
        this.availableMessage = String.format("&7Available migrations: %s", String.join(", ", plugin.getMigrations().getKeys()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("cmiimporter.use")) {
            reply(sender, "&cYou don't have permission!");
            return true;
        }

        if (args.length == 0) {
            reply(sender, availableMessage);
            return true;
        }

        List<Migration> migrations;
        if (args.length == 1 && args[0].equalsIgnoreCase("all")) {
            migrations = plugin.getMigrations().getAllMigrations();
        } else {
            Set<String> available = plugin.getMigrations().getKeys();
            for (String arg : args) {
                if (!available.contains(arg.toLowerCase())) {
                    reply(sender, String.format("&cInvalid migration: \"%s\"", arg));
                    reply(sender, availableMessage);
                    return true;
                }
            }
            migrations = plugin.getMigrations().getApplicableMigrations(args);
        }

        reply(sender, "&7Migrating data, please wait...");
        migrations.forEach(Migration::run);
        reply(sender, "&aMigration complete.");

        return true;
    }

    private void reply(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

}
