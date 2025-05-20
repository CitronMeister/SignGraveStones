package org.shyni.signGraveStone.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.shyni.signGraveStone.SignGraveStone;
import org.shyni.signGraveStone.settings.DeathMessageManager;

import java.util.HashSet;
import java.util.Set;

public class PlayerDeathListener implements Listener {
    private final Set<Location> graveSigns = new HashSet<>();


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location deathLocation = player.getLocation();

        // Get the block below the player
        Block belowBlock = deathLocation.getBlock().getRelative(BlockFace.DOWN);

        // Check if the block below is solid
        if (!belowBlock.getType().isSolid()) {
            SignGraveStone.getInstance().getLogger().info("Couldnt place grave sign - no solid ground below!");
            return;
        }

        // Try to place the sign at the player's death location
        Block signBlock = deathLocation.getBlock();

        // Loop upward to find a valid spot (up to 5 blocks above)
        for (int i = 0; i < 5; i++) {
            Material type = signBlock.getType();

            if (type == Material.AIR || org.shyni.signGraveStone.settings.GraveSettings.getInstance().getReplaceableBlocks().contains(type)) {
                // Found a place to put the sign — clear it if needed
                signBlock.setType(Material.AIR);
                break;
            } else if (type.name().contains("SIGN")) {
                // There's already a sign here — go up one block
                signBlock = signBlock.getRelative(BlockFace.UP);
            } else {
                // Block is solid and not replaceable — try next block up
                signBlock = signBlock.getRelative(BlockFace.UP);
            }

            // If we've reached an invalid height or hit the build limit, cancel
            if (signBlock.getY() >= deathLocation.getWorld().getMaxHeight()) {
                SignGraveStone.getInstance().getLogger().info("Couldn't place grave sign - max height reached!");
                return;
            }
        }



        // Set the block to a standing sign
        signBlock.setType(Material.OAK_SIGN);

        // Configure the sign's rotation
        BlockData blockData = signBlock.getBlockData();
        if (blockData instanceof org.bukkit.block.data.type.Sign) {
            org.bukkit.block.data.type.Sign signData = (org.bukkit.block.data.type.Sign) blockData;
            signData.setRotation(getSignRotation(deathLocation.getYaw()));
            signBlock.setBlockData(signData);
        }

        // Get the Sign and set its text
        Sign sign = (Sign) signBlock.getState();

        String[] signLines = new String[] {
                ChatColor.BLACK + "R.I.P.",
                ChatColor.GREEN + player.getName(),
                ChatColor.YELLOW + DeathMessageManager.getInstance().getCustomMessage(event.getDeathMessage(), player.getName()),
                ChatColor.RED + getFormattedDate()
        };

        for (int i = 0; i < Math.min(4, signLines.length); i++) {
            sign.setLine(i, signLines[i]);
        }

        sign.update();

        // Add to our tracked grave signs
        graveSigns.add(signBlock.getLocation());
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (graveSigns.contains(event.getBlock().getLocation())) {
            event.setDropItems(false); // Prevent item drops
            graveSigns.remove(event.getBlock().getLocation()); // Remove from tracking
        }
    }

    private String shortenDeathMessage(String message) {
        if (message == null || message.isEmpty()) return "";

        Player player = SignGraveStone.getInstance().getServer().getPlayerExact(message.split(" ")[0]);
        if (player != null) {
            // Remove exact player name from beginning of the message
            String name = player.getName();
            if (message.startsWith(name)) {
                return message.substring(name.length()).trim();
            }
        }

        // Fallback: remove any word at the beginning (up to first space)
        return message.replaceFirst("^[A-Za-z0-9_]{3,16}\\s+", "").trim();
    }


    private BlockFace getSignRotation(float yaw) {
        // Convert yaw to nearest BlockFace rotation
        int rotation = (int) ((yaw + 180 + 11.25) % 360 / 22.5);

        switch (rotation) {
            case 0: return BlockFace.SOUTH;
            case 1: return BlockFace.SOUTH_SOUTH_WEST;
            case 2: return BlockFace.SOUTH_WEST;
            case 3: return BlockFace.WEST_SOUTH_WEST;
            case 4: return BlockFace.WEST;
            case 5: return BlockFace.WEST_NORTH_WEST;
            case 6: return BlockFace.NORTH_WEST;
            case 7: return BlockFace.NORTH_NORTH_WEST;
            case 8: return BlockFace.NORTH;
            case 9: return BlockFace.NORTH_NORTH_EAST;
            case 10: return BlockFace.NORTH_EAST;
            case 11: return BlockFace.EAST_NORTH_EAST;
            case 12: return BlockFace.EAST;
            case 13: return BlockFace.EAST_SOUTH_EAST;
            case 14: return BlockFace.SOUTH_EAST;
            case 15: return BlockFace.SOUTH_SOUTH_EAST;
            default: return BlockFace.SOUTH;
        }
    }

    private String getFormattedDate() {
        java.time.LocalDate date = java.time.LocalDate.now();
        return String.format("%02d/%02d/%04d", date.getDayOfMonth(), date.getMonthValue(), date.getYear());
    }

}