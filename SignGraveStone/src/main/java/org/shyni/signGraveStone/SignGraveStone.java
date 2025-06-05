package org.shyni.signGraveStone;

import org.bukkit.plugin.java.JavaPlugin;
import org.shyni.signGraveStone.listener.PlayerDeathListener;
import org.shyni.signGraveStone.settings.DeathMessageManager;
import org.shyni.signGraveStone.util.Metrics;

public final class SignGraveStone extends JavaPlugin {



    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Sign Grave Stone is enabled!");
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(), this);
        org.shyni.signGraveStone.settings.GraveSettings.getInstance().load();
        DeathMessageManager.getInstance().load();

        int pluginId = 26089;
        Metrics metrics = new Metrics(this, pluginId);

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
