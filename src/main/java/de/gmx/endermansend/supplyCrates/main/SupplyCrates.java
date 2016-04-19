package de.gmx.endermansend.supplyCrates.main;

import de.gmx.endermansend.supplyCrates.chest.Spawner;
import de.gmx.endermansend.supplyCrates.config.ConfigHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class SupplyCrates extends JavaPlugin {

    private ConfigHandler config;

    @Override
    public void onEnable() {

        config = new ConfigHandler(this);

        (new Spawner(this)).runTaskTimer(this, 0L, 200L);

        getLogger().info("Enabled");

    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled");
    }

    public ConfigHandler getConfigHandler() {
        return config;
    }

}
