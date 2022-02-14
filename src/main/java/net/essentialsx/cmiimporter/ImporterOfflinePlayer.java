package net.essentialsx.cmiimporter;

import com.earth2me.essentials.OfflinePlayer;
import org.bukkit.Server;

import java.util.UUID;

public class ImporterOfflinePlayer extends OfflinePlayer {

    public ImporterOfflinePlayer(String name, UUID uuid, Server server) {
        super(uuid, server);
        this.setName(name);
    }

}
