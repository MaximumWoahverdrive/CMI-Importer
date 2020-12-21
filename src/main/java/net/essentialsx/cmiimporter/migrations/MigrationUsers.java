package net.essentialsx.cmiimporter.migrations;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import net.essentialsx.cmiimporter.CMIImporter;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MigrationUsers extends Migration {

    public MigrationUsers(CMIImporter importer, Essentials essentials, boolean requiresUsers) {
        super(importer, essentials, requiresUsers);
    }

    @Override
    public void run() {
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, username, FakeAccount FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND username IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.getString("player_uuid"));
                String username = row.getString("username");
                boolean isNpc = getBooleanFromNumeric(row, "FakeAccount");
                if (!essentials.getUserMap().userExists(uuid)) {
                    OfflinePlayer player = new OfflinePlayer(uuid, Bukkit.getServer());
                    player.setName(username);

                    User user = new User(player, essentials);
                    user.setLastAccountName(username);
                    if (isNpc) {
                        user.setNPC(true);
                    }
                    user.save();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
