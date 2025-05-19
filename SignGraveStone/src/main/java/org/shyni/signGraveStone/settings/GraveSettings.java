package org.shyni.signGraveStone.settings;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.shyni.signGraveStone.SignGraveStone;

import java.io.File;
import java.util.*;

public class GraveSettings {

    private static final GraveSettings instance = new GraveSettings();

    private File file;
    private YamlConfiguration config;

    private final Set<Material> replaceableBlocks = new HashSet<>();

    private GraveSettings() {}

    public static GraveSettings getInstance() {
        return instance;
    }

    public void load() {
        file = new File(SignGraveStone.getInstance().getDataFolder(), "grave-settings.yml");

        if (!file.exists()) {
            SignGraveStone.getInstance().saveResource("grave-settings.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);
        config.options().parseComments(true);
        loadReplaceableBlocks();
    }

    private void loadReplaceableBlocks() {
        replaceableBlocks.clear();
        List<String> list = config.getStringList("replaceable-blocks");

        for (String materialName : list) {
            try {
                Material material = Material.valueOf(materialName.toUpperCase());
                replaceableBlocks.add(material);
            } catch (IllegalArgumentException e) {
                SignGraveStone.getInstance().getLogger().warning("Invalid replaceable block: " + materialName);
            }
        }
        SignGraveStone.getInstance().getLogger().info("Loaded replaceable blocks: " + replaceableBlocks);
    }

    public Set<Material> getReplaceableBlocks() {
        return Collections.unmodifiableSet(replaceableBlocks);
    }

    public void reload() {
        load();
    }

    public void save() {
        try {
            config.save(file);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void set(String path, Object value) {
        config.set(path, value);
        save();
    }
}
