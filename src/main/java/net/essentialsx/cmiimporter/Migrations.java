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
import com.google.common.collect.ImmutableMap;
import net.essentialsx.cmiimporter.migrations.Migration;
import net.essentialsx.cmiimporter.migrations.impl.MigrationEconomy;
import net.essentialsx.cmiimporter.migrations.impl.MigrationHomes;
import net.essentialsx.cmiimporter.migrations.impl.MigrationLastLogin;
import net.essentialsx.cmiimporter.migrations.impl.MigrationLastLogout;
import net.essentialsx.cmiimporter.migrations.impl.MigrationMail;
import net.essentialsx.cmiimporter.migrations.impl.MigrationNicknames;
import net.essentialsx.cmiimporter.migrations.impl.MigrationUsers;
import net.essentialsx.cmiimporter.migrations.impl.MigrationWarps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Migrations {

    private final Migration userMigration;
    private final Map<String, Migration> migrationMap;

    public Migrations(CMIImporter importer, Essentials essentials) {
        this.userMigration = new MigrationUsers(importer, essentials);
        this.migrationMap = new ImmutableMap.Builder<String, Migration>()
                .put("economy", new MigrationEconomy(importer, essentials))
                .put("homes", new MigrationHomes(importer, essentials))
                .put("lastlogin", new MigrationLastLogin(importer, essentials))
                .put("lastlogout", new MigrationLastLogout(importer, essentials))
                .put("mail", new MigrationMail(importer, essentials))
                .put("nicknames", new MigrationNicknames(importer, essentials))
                .put("warps", new MigrationWarps(importer, essentials))
                .build();
    }

    public List<Migration> getApplicable(String[] args) {
        List<Migration> migrations = new ArrayList<>();
        for (String arg : args) {
            migrations.add(migrationMap.get(arg.toLowerCase()));
        }

        // if there are any user-dependent migrations, migrate user data first
        if (migrations.stream().anyMatch(Migration::isUserDependent)) {
            migrations.add(0, userMigration);
        }

        // there should probably also be checks somewhere (here?) for previously
        // migrated data (need log file) to ensure that users don't migrate data again ??

        return migrations;
    }

    public Set<String> getAvailable() {
        return migrationMap.keySet();
    }

}
