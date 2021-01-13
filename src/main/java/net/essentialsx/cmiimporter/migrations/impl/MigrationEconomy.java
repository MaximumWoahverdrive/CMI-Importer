package net.essentialsx.cmiimporter.migrations.impl;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import net.ess3.api.MaxMoneyException;
import net.essentialsx.cmiimporter.CMIImporter;
import net.essentialsx.cmiimporter.migrations.AbstractMigration;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MigrationEconomy extends AbstractMigration {

    public MigrationEconomy(CMIImporter importer, Essentials essentials) {
        super(importer, essentials, "Economy", "Imports user economy data.", true);
    }

    @Override
    public void run() {
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, Balance FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND Balance IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.getString("player_uuid"));
                User user = essentials.getUser(uuid);
                if (user == null) {
                    logWarning(String.format("Unable to migrate economy data for UUID %s!", uuid));
                    continue;
                }
                // Not using valueOf() because it returns scientific notation and Glare isn't advanced enough to find a way to fix this
                String value = row.getDbl("Balance", 0.0).toString();
                BigDecimal bal = new BigDecimal(value);
                user.setMoney(bal);
            }
        } catch (SQLException | MaxMoneyException ex) {
            ex.printStackTrace();
        }
    }

}
