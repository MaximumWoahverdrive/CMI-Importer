package net.essentialsx.cmiimporter.migrations.impl;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.OfflinePlayer;
import com.earth2me.essentials.User;
import net.essentialsx.cmiimporter.CMIImporter;
import net.essentialsx.cmiimporter.migrations.AbstractMigration;
import org.bukkit.Bukkit;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MigrationUsers extends AbstractMigration {

    public MigrationUsers(CMIImporter importer, Essentials essentials) {
        super(importer, essentials, "Users", "Imports basic user data.", false);
    }

    @Override
    public void run() {
        // import all valid CMI userdata
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, username, FakeAccount FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND username IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.getString("player_uuid"));
                String username = row.getString("username");
                boolean isNpc = getBooleanFromNumeric(row, "FakeAccount");
                constructUserFile(uuid, username, isNpc);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // try and construct user files for remaining OfflinePlayers
        // necessary to prevent UserDoesNotExistException thrown by Vault provider
        for (org.bukkit.OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            constructUserFile(offlinePlayer.getUniqueId(), offlinePlayer.getName(), null);
        }
    }

    private void constructUserFile(UUID uuid, String name, Boolean npc) {
        if (!essentials.getUserMap().userExists(uuid)) {
            OfflinePlayer player = new OfflinePlayer(uuid, Bukkit.getServer());
            // this depends on com.earth2me.essentials.OfflinePlayer#setName being made public
            player.setName(name);

            User user = new User(player, essentials);
            user.setLastAccountName(name);
            // this player might be an NPC, so we'll flag them
            // if this player ever reconnects, their player file will be updated automatically
            if ((npc != null && npc) || (npc == null && uuid.version() == 4)) {
                user.setNPC(true);
            }
            user.save();
        }
    }

}
