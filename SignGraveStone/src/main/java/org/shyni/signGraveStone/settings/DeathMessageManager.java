package org.shyni.signGraveStone.settings;

import org.bukkit.configuration.file.YamlConfiguration;
import org.shyni.signGraveStone.SignGraveStone;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DeathMessageManager {

    private static final DeathMessageManager instance = new DeathMessageManager();

    private File file;
    private YamlConfiguration config;

    private final Map<String, String> customMessages = new HashMap<>();

    private DeathMessageManager() {}

    public static DeathMessageManager getInstance() {
        return instance;
    }

    public void load() {
        file = new File(SignGraveStone.getInstance().getDataFolder(), "death-messages.yml");

        // Copy default file if it doesn't exist
        if (!file.exists()) {
            SignGraveStone.getInstance().saveResource("death-messages.yml", false);
        }

        config = new YamlConfiguration();
        config.options().parseComments(true); // Enable comment parsing

        try {
            config.load(file);
        } catch (IOException | org.bukkit.configuration.InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (config.contains("death-messages")) {
            for (String key : config.getConfigurationSection("death-messages").getKeys(false)) {
                String msg = config.getString("death-messages." + key);
                if (msg != null) {
                    customMessages.put(key.toLowerCase(), msg);
                }
            }
        } else {
            SignGraveStone.getInstance().getLogger().warning("No 'death-messages' section found in death-messages.yml");
        }
    }

    public String getCustomMessage(String rawDeathMessage, String playerName) {
        String cleaned = rawDeathMessage.replaceFirst("^" + playerName + "\\s*", "").toLowerCase();

        for (Map.Entry<String, String> entry : customMessages.entrySet()) {
            if (cleaned.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return customMessages.getOrDefault("default", "Died.");
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }
}
