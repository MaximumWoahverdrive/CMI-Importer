package net.essentialsx.cmiimporter;

import net.essentialsx.cmiimporter.migrations.Migration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ImportCommand implements CommandExecutor {

    private final CMIImporter plugin;

    public ImportCommand(CMIImporter plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // should this only be run from console?
        // we should validate arguments here using Migrations#getAvailableMigrations

        List<Migration> migrations = plugin.getMigrations().getApplicableMigrations(args);
        migrations.forEach(Migration::run);

        return true;
    }

}
