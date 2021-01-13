package net.essentialsx.cmiimporter.migrations;

import co.aikar.idb.DbRow;
import com.earth2me.essentials.Essentials;
import net.essentialsx.cmiimporter.CMIImporter;

import java.util.logging.Logger;

public abstract class AbstractMigration implements Migration {

    private static final Logger logger = Logger.getLogger("EssentialsX-CMI-Importer");

    protected final CMIImporter importer;
    protected final Essentials essentials;

    private final String name;
    private final String description;
    private final boolean userDependent;

    public AbstractMigration(CMIImporter importer, Essentials essentials, String name, String description, boolean userDependent) {
        this.importer = importer;
        this.essentials = essentials;
        this.name = name;
        this.description = description;
        this.userDependent = userDependent;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isUserDependent() {
        return userDependent;
    }

    // utility methods

    protected String table(String table) {
        return importer.getDbConfig().getTablePrefix() + table;
    }

    protected boolean getBooleanFromNumeric(DbRow row, String column) {
        if (importer.getDbConfig().getBackend().equalsIgnoreCase("sqlite")) {
            return row.getInt(column) == 1;
        }
        return row.get(column);
    }

    protected void logWarning(String message) {
        logger.warning(message);
    }

}
