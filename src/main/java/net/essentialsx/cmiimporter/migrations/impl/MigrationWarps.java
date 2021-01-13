package net.essentialsx.cmiimporter.migrations.impl;

import com.earth2me.essentials.Essentials;
import net.essentialsx.cmiimporter.CMIImporter;
import net.essentialsx.cmiimporter.Util;
import net.essentialsx.cmiimporter.migrations.AbstractMigration;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MigrationWarps extends AbstractMigration {

    private static final String WARP_LOC_SEPARATOR = ":";

    public MigrationWarps(CMIImporter importer, Essentials essentials) {
        super(importer, essentials, "Warps", "Imports warp data.", false);
    }

    @Override
    public void run() {
        try {
            File warpsFile = new File(essentials.getDataFolder(), "../CMI/warps.yml");
            YamlConfiguration warpsConfig = YamlConfiguration.loadConfiguration(warpsFile);
            for (String key : warpsConfig.getKeys(false)) {
                String locString = warpsConfig.getString(key + ".Location");
                if (locString != null) {
                    Location loc = Util.parseLocation(locString, WARP_LOC_SEPARATOR, false);
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
