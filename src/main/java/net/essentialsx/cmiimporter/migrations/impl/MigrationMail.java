package net.essentialsx.cmiimporter.migrations.impl;

import co.aikar.idb.DB;
import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.ess3.api.IUser;
import net.essentialsx.api.v2.services.mail.MailMessage;
import net.essentialsx.api.v2.services.mail.MailService;
import net.essentialsx.cmiimporter.CMIImporter;
import net.essentialsx.cmiimporter.migrations.AbstractMigration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class MigrationMail extends AbstractMigration {

    private static final Gson GSON = new Gson();

    private final MailService mailService = Bukkit.getServer().getServicesManager().getRegistrations(MailService.class).stream()
        .findFirst()
        .map(RegisteredServiceProvider::getProvider)
        .orElse(null);

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
                String mailData = row.getString("Mail");
                List<List<String>> mails = GSON.fromJson(mailData, new TypeToken<List<List<String>>>() {
                }.getType());
                if (mails == null) {
                    continue;
                }
                for (List<String> mail : mails) {
                    if (mail.size() < 3) {
                        logWarning(String.format("Skipping malformed mail entry for UUID %s: %s", uuid, mail));
                        continue;
                    }
                    String sender = mail.get(0);
                    long timestamp = Long.parseLong(mail.get(1));
                    String content = mail.get(2);

                    // Check if mail has expired (optional 4th parameter is expiration time in seconds)
                    if (mail.size() >= 4) {
                        long expirationSeconds = Long.parseLong(mail.get(3)); // yes really the expiration time is in seconds while timestamps are in millisecond unix epoch
                        long expirationTime = timestamp + (expirationSeconds * 1000);
                        if (System.currentTimeMillis() > expirationTime) {
                            // Mail has expired, skip it
                            continue;
                        }
                        sendMailWrapper(user, new MailMessage(false, false, sender, null, timestamp, expirationTime, content));
                        continue;
                    }

                    sendMailWrapper(user, new MailMessage(false, false, sender, null, timestamp, 0L, content));
                }
            }
        } catch (SQLException | InvocationTargetException | NoSuchMethodException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    // hack access to private method because none of the public ones allow specifying sent timestamp
    // private void sendMail(IUser recipient, MailMessage message) {
    private void sendMailWrapper(User recipient, MailMessage message) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        var m = mailService.getClass().getDeclaredMethod("sendMail", IUser.class, MailMessage.class);
        m.setAccessible(true);
        m.invoke(mailService, recipient, message);
    }
}
