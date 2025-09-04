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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;


public class Util {

    private static final String HOME_LOC_SEPARATOR = ":";

    public static Map<String, Location> parseHomes(String input) {
        Map<String, Location> result = new LinkedHashMap<>();

        // crude JSON parsing: split on ":", "{", "}", etc.
        // Assumes input is well-formed (your case)
        String json = input.trim();
        if (json.startsWith("{")) json = json.substring(1);
        if (json.endsWith("}")) json = json.substring(0, json.length()-1);

        // split top-level entries by "],"
        String[] entries = json.split("],");
        for (String entry : entries) {
            entry = entry.trim();
            if (entry.endsWith("]")) entry = entry.substring(0, entry.length()-1);

            String[] keyValue = entry.split(":", 2);
            String key = keyValue[0].trim().replaceAll("^\"|\"$", ""); // remove quotes

            // value part looks like ["world:..."]
            String valuePart = keyValue[1].trim();
            int start = valuePart.indexOf("\"");
            int end = valuePart.indexOf("\"", start+1);
            String locStr = valuePart.substring(start+1, end);

            result.put(key, parseLocation(locStr, HOME_LOC_SEPARATOR, true));
        }

        return result;
    }

    public static List<List<String>> parseLists(String input, String listSeparator, String elementSeparator) {
        List<List<String>> parent = new ArrayList<>();
        for (String list : input.split(listSeparator)) {
            parent.add(Arrays.asList(list.split(elementSeparator)));
        }
        return parent;
    }

    public static Location parseLocation(String input, String separator, boolean reverseYawPitch) {
        String[] split = input.split(separator);
        World world = Bukkit.getWorld(split[0]);
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat((reverseYawPitch ? split[5] : split[4]));
        float pitch = Float.parseFloat((reverseYawPitch ? split[4] : split[5]));
        return new Location(world, x, y, z, yaw, pitch);
    }

}
