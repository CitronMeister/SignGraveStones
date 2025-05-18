package org.shyni.signGraveStone;

import org.bukkit.plugin.java.JavaPlugin;

public final class SignGraveStone extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Sign Grave Stone is enabled!");
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Sign Grave Stone has been disabled!");
    }
}
