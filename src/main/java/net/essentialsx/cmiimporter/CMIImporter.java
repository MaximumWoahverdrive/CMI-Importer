/*
 * Imports data from a CMI SQLite database into EssentialsX.
 * Copyright (C) 2020 md678685
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.essentialsx.cmiimporter;

import co.aikar.idb.*;
import com.earth2me.essentials.Essentials;
import net.essentialsx.cmiimporter.config.DatabaseConfig;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class CMIImporter extends JavaPlugin {

    private DatabaseConfig dbConfig;
    private Migrations migrations;

    @Override
    public void onEnable() {
        this.dbConfig = new DatabaseConfig(this);
        DatabaseOptions options = dbConfig.getDbOptions();
        HikariPooledDatabase db = PooledDatabaseOptions.builder()
                .options(options)
                .createHikariDatabase();
        DB.setGlobalDatabase(db);

        Plugin plugin = this.getServer().getPluginManager().getPlugin("Essentials");
        if (!(plugin instanceof Essentials)) {
            throw new IllegalArgumentException("The currently installed \"Essentials\" plugin isn't actually EssentialsX!");
        }
        this.migrations = new Migrations(this, (Essentials) plugin);

        this.getServer().getPluginCommand("cmi-import").setExecutor(new ImportCommand(this));
    }

    @Override
    public void onDisable() {
        DB.close();
    }

    public DatabaseConfig getDbConfig() {
        return dbConfig;
    }

    public Migrations getMigrations() {
        return migrations;
    }

}
