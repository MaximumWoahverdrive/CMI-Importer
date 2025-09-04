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

import static com.earth2me.essentials.I18n.tlLiteral;

public class MigrationMail extends AbstractMigration {

    public MigrationMail(CMIImporter importer, Essentials essentials) {
        super(importer, essentials, "Mail", "Imports user mail data.", true);
    }

    @Override
    public void run() {
        try {
            List<DbRow> results = DB.getResults("SELECT player_uuid, Mail FROM " + table("users") + " WHERE player_uuid IS NOT NULL AND Mail IS NOT NULL");
            for (DbRow row : results) {
                UUID uuid = UUID.fromString(row.get("player_uuid"));
                User user = essentials.getUser(uuid);
                if (user == null) {
                    logWarning(String.format("Unable to migrate mail data for UUID %s!", uuid));
                    continue;
                }
                List<List<String>> mails = Util.parseLists(row.getString("Mail"), ";", ":");
                for (List<String> mail : mails) {
                    String sender = mail.get(0);
                    // CMI replaces ";" with "T7C" and ":" with "T8C" when storing message contents
                    String content = mail.get(2).replace("T7C", ";").replace("T8C", ":");
                    String mailFormat = tlLiteral("mailFormat", sender, content);
                    user.addMail(tlLiteral("mailMessage", mailFormat));
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
