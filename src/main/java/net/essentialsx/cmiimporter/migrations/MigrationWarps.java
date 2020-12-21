package net.essentialsx.cmiimporter.migrations;

import com.earth2me.essentials.Essentials;
import net.essentialsx.cmiimporter.CMIImporter;
import net.essentialsx.cmiimporter.Util;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MigrationWarps extends Migration {

    public MigrationWarps(CMIImporter importer, Essentials essentials, boolean requiresUsers) {
        super(importer, essentials, requiresUsers);
    }

    @Override
    public void run() {
        final String warpLocSeparator = ";";
        try {
            File warpsFile = new File(essentials.getDataFolder(), "../CMI/warps.yml");
            YamlConfiguration warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
            for (String key : warpsConfig.getKeys(false)) {
                String locString = warpsConfig.getString(key + ".Location");
                if (locString != null) {
                    Location loc = Util.parseLocation(locString, warpLocSeparator, false);
                    try {
                        essentials.getWarps().setWarp(null, key, loc);
                    } catch (Exception ex) {
                        logWarning(String.format("Unable to migrate warp data for warp \"%s\"!", key));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
