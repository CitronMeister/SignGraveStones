package org.shyni.signGraveStone;

import org.bukkit.plugin.java.JavaPlugin;
import org.shyni.signGraveStone.listener.PlayerDeathListener;

public final class SignGraveStone extends JavaPlugin {



    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Sign Grave Stone is enabled!");
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        org.shyni.signGraveStone.settings.GraveSettings.getInstance().load();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Sign Grave Stone has been disabled!");
    }
    public static SignGraveStone getInstance() {
        return getPlugin(SignGraveStone.class);
    }
}
