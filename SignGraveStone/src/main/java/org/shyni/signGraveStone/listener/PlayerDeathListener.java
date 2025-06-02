package org.shyni.signGraveStone.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.shyni.signGraveStone.SignGraveStone;
import org.shyni.signGraveStone.settings.DeathMessageManager;
import java.util.HashSet;
import java.util.Set;

public class PlayerDeathListener implements Listener {
    private final Set<Location> graveSigns = new HashSet<>();


    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Location[] deathLocation = new Location[]{ player.getLocation()};
        Location checkLoc = deathLocation[0].clone();
        int minY = checkLoc.getWorld().getMinHeight();
        String deathMessage = DeathMessageManager.getInstance().getCustomMessage(event.getDeathMessage(), player.getName());



        if (!deathLocation[0].getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
            SignGraveStone.getInstance().getLogger().info("Could not find solid ground below to place the sign.");
            return;
        }


        Bukkit.getScheduler().runTaskLater(SignGraveStone.getInstance(), () -> {
            while (checkLoc.getY() > minY) {
                checkLoc.subtract(0, 1, 0);
                Block block = checkLoc.getBlock();
                if (block.getType().isSolid()) {
                    deathLocation[0] = block.getLocation().add(0, 1, 0);
                    break;
                }
            }

            Block signBlock = deathLocation[0].getBlock();

            for (int i = 0; i < 5; i++) {
                Material type = signBlock.getType();
                if (type == Material.AIR || org.shyni.signGraveStone.settings.GraveSettings.getInstance().getReplaceableBlocks().contains(type)) {
                    signBlock.setType(Material.AIR);
                    break;
                } else if (type.name().contains("SIGN")) {
                    signBlock = signBlock.getRelative(BlockFace.UP);
                } else {
                    signBlock = signBlock.getRelative(BlockFace.UP);
                }

                if (signBlock.getY() >= deathLocation[0].getWorld().getMaxHeight()) {
                    SignGraveStone.getInstance().getLogger().info("Couldn't place grave sign - max height reached!");
                    return;
                }
            }

            signBlock.setType(Material.OAK_SIGN);
            BlockData blockData = signBlock.getBlockData();
            if (blockData instanceof org.bukkit.block.data.type.Sign signData) {
                BlockFace face = getSignRotation(player.getLocation().getYaw());
                signData.setRotation(face);
                signBlock.setBlockData(signData);
            }

            Sign sign = (Sign) signBlock.getState();
            String[] signLines = new String[] {
                    ChatColor.BLACK + "R.I.P.",
                    ChatColor.GREEN + player.getName(),
                    ChatColor.YELLOW + deathMessage,
                    ChatColor.RED + getFormattedDate()
            };

            for (int i = 0; i < Math.min(4, signLines.length); i++) {
                sign.setLine(i, signLines[i]);
            }

            sign.update(true, false);
            graveSigns.add(signBlock.getLocation());
        }, 20L);

    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (graveSigns.contains(event.getBlock().getLocation())) {
            event.setDropItems(false); // Prevent item drops
            graveSigns.remove(event.getBlock().getLocation()); // Remove from tracking
        }
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

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityExplode(EntityExplodeEvent event) {
        event.blockList().removeIf(block -> graveSigns.contains(block.getLocation()));
    }

    @EventHandler( priority = EventPriority.LOW)
    public void onBlockExplode(BlockExplodeEvent event) {
        event.blockList().removeIf(block -> graveSigns.contains(block.getLocation()));
    }

}