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

import com.earth2me.essentials.Essentials;
import net.essentialsx.cmiimporter.migrations.Migration;
import net.essentialsx.cmiimporter.migrations.MigrationEconomy;
import net.essentialsx.cmiimporter.migrations.MigrationHomes;
import net.essentialsx.cmiimporter.migrations.MigrationLastLogin;
import net.essentialsx.cmiimporter.migrations.MigrationLastLogout;
import net.essentialsx.cmiimporter.migrations.MigrationMail;
import net.essentialsx.cmiimporter.migrations.MigrationNicknames;
import net.essentialsx.cmiimporter.migrations.MigrationUsers;
import net.essentialsx.cmiimporter.migrations.MigrationWarps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Migrations {

    private final Map<String, Migration> migrationMap;

    public Migrations(CMIImporter importer, Essentials essentials) {
        this.migrationMap = new HashMap<>();
        populateMap(importer, essentials);
    }

    private void populateMap(CMIImporter importer, Essentials essentials) {
        migrationMap.put("users", new MigrationUsers(importer, essentials, false));

        migrationMap.put("economy", new MigrationEconomy(importer, essentials, true));
        migrationMap.put("homes", new MigrationHomes(importer, essentials, true));
        migrationMap.put("lastlogin", new MigrationLastLogin(importer, essentials, true));
        migrationMap.put("lastlogout", new MigrationLastLogout(importer, essentials, true));
        migrationMap.put("mail", new MigrationMail(importer, essentials, true));
        migrationMap.put("nicknames", new MigrationNicknames(importer, essentials, true));

        migrationMap.put("warps", new MigrationWarps(importer, essentials, false));
    }

    public List<Migration> getApplicableMigrations(String[] args) {
        boolean includesUsers = false;
        List<Migration> migrations = new ArrayList<>();

        for (String arg : args) {
            if (arg.equalsIgnoreCase("users")) {
                includesUsers = true;
            }
            migrations.add(migrationMap.get(arg.toLowerCase()));
        }

        // ensure that any user-based migrations first migrate user data if required
        if (!includesUsers && migrations.stream().anyMatch(Migration::requiresUsers)) {
            migrations.set(0, migrationMap.get("users"));
        }

        // there should probably also be checks here for previously migrated data (need log file)

        return migrations;
    }

    public Set<String> getAvailableMigrations() {
        return migrationMap.keySet();
    }

}
