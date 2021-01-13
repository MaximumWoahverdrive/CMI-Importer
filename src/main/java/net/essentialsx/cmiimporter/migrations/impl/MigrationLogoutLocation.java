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
import java.util.UUID;

public class MigrationLogoutLocation extends AbstractMigration {

    private static final String LOGOUT_LOC_SEPARATOR = ":";

    public MigrationLogoutLocation(CMIImporter importer, Essentials essentials) {
        super(importer, essentials, "Logout Location", "Imports user logout location data.", true);
    }

    @Override
    public void run() {
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, LogOutLocation FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND LogOutLocation IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.getString("player_uuid"));
                User user = essentials.getUser(uuid);
                if (user == null) {
                    logWarning(String.format("Unable to migrate logout location data for UUID %s!", uuid));
                    continue;
                }
                try {
                    String loc = row.getString("LogOutLocation");
                    user.setLogoutLocation(Util.parseLocation(loc, LOGOUT_LOC_SEPARATOR, true));
                } catch (Exception ex) {
                    logWarning(String.format("Unable to migrate logout location data for %s!", user.getName()));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

}
