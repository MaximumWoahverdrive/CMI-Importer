package net.essentialsx.cmiimporter.migrations;

import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import net.essentialsx.cmiimporter.CMIImporter;

import java.util.logging.Logger;

public abstract class Migration {

    private static final Logger logger = Logger.getLogger("EssentialsX-CMI-Importer");

    protected final CMIImporter importer;
    protected final Essentials essentials;
    protected final boolean requiresUsers;

    public Migration(CMIImporter importer, Essentials essentials, boolean requiresUsers) {
        this.importer = importer;
        this.essentials = essentials;
        this.requiresUsers = requiresUsers;
    }

    public boolean requiresUsers() {
        return requiresUsers;
    }

    public abstract void run();

    protected String table(String table) {
        return importer.getDbConfig().getTablePrefix() + table;
    }

    protected boolean getBooleanFromNumeric(DbRow row, String column) {
        if (importer.getDbConfig().getBackend().equalsIgnoreCase("sqlite")) {
            return row.getInt(column) == 1;
        }
        return row.get(column);
    }

    protected static void logWarning(String message) {
        logger.warning(message);
    }

}
