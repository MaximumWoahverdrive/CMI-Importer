package net.essentialsx.cmiimporter.migrations.impl;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.essentialsx.cmiimporter.CMIImporter;
import net.essentialsx.cmiimporter.Util;
import net.essentialsx.cmiimporter.migrations.AbstractMigration;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MigrationHomes extends AbstractMigration {

    public MigrationHomes(CMIImporter importer, Essentials essentials) {
        super(importer, essentials, "Homes", "Imports user home data.", true);
    }

    @Override
    public void run() {
        final String homeLocSeparator = ":";
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, Homes FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND Homes IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.getString("player_uuid"));
                User user = essentials.getUser(uuid);
                if (user == null) {
                    logWarning(String.format("Unable to migrate homes data for UUID %s!", uuid));
                    continue;
                }
                for (Map.Entry<String, String> entry : Util.parseMap(row.getString("Homes")).entrySet()) {
                    String name = entry.getKey();
                    String loc = entry.getValue();
                    try {
                        user.setHome(name, Util.parseLocation(loc, homeLocSeparator, true));
                    } catch (Exception ex) {
                        logWarning(String.format("Unable to migrate home \"%s\" for %s!", name, user.getName()));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
