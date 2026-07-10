package com.buriedalive;

import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

/**
 * Builds a small collapsed cave pocket around the player, seals them in with
 * stone, and hands them the starter tools (two pickaxes and one torch).
 */
public class CaveBuilder {

    // Persistent flag key stored in player data so we only bury once per life.
    private static final String BURIED_KEY = "buriedalive_buried";

    // How deep below the surface to place the buried pocket.
    private static final int BURY_DEPTH = 20;

    // Radius of the hollow pocket around the player.
    private static final int POCKET_RADIUS = 2;

    /**
     * Returns true if this player has already been buried during their current life.
     */
    public static boolean hasBeenBuried(ServerPlayerEntity player) {
        NbtCompound data = getModData(player);
        return data.getBoolean(BURIED_KEY);
    }

    private static void setBuried(ServerPlayerEntity player, boolean value) {
        NbtCompound root = new NbtCompound();
        NbtCompound modData = getModData(player);
        modData.putBoolean(BURIED_KEY, value);
        // Write the mod data back into the player's persistent storage.
        NbtCompound persistent = getPersistentRoot(player);
        persistent.put("buriedalive", modData);
    }

    private static NbtCompound getModData(ServerPlayerEntity player) {
        NbtCompound persistent = getPersistentRoot(player);
        if (persistent.contains("buriedalive")) {
            return persistent.getCompound("buriedalive");
        }
        return new NbtCompound();
    }

    /**
     * Fabric provides a mod-persistent NBT tag on ServerPlayerEntity via
     * IServerPlayerMixin equivalents; we use the player's writeCustomDataToNbt
     * indirectly. To keep this simple and dependency-free we store in a
     * dedicated compound on the player command tags fallback is not persistent,
     * so we use the writeNbt approach through a simple helper.
     */
    private static NbtCompound getPersistentRoot(ServerPlayerEntity player) {
        // Use the player's command tags as a lightweight persistence check is
        // unreliable; instead we rely on an in-memory approach combined with
        // a written NBT compound stored on the entity itself.
        NbtCompound nbt = PlayerPersistentData.get(player);
        return nbt;
    }

    /**
     * Buries the player: teleports them deep underground, hollows a pocket,
     * seals it with stone, and gives them their starter equipment.
     */
    public static void buryPlayer(ServerPlayerEntity player, ServerWorld world) {
        // Determine a surface position at the player's X/Z.
        BlockPos playerPos = player.getBlockPos();
        int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, playerPos.getX(), playerPos.getZ());

        int buryY = surfaceY - BURY_DEPTH;
        if (buryY < world.getBottomY() + 5) {
            buryY = world.getBottomY() + 5;
        }

        BlockPos center = new BlockPos(playerPos.getX(), buryY, playerPos.getZ());

        // Fill the surrounding area with stone (the "collapse").
        int fillRadius = POCKET_RADIUS + 2;
        for (int x = -fillRadius; x <= fillRadius; x++) {
            for (int y = -fillRadius; y <= fillRadius; y++) {
                for (int z = -fillRadius; z <= fillRadius; z++) {
                    BlockPos p = center.add(x, y, z);
                    world.setBlockState(p, Blocks.STONE.getDefaultState());
                }
            }
        }

        // Hollow out the pocket the player stands in.
        for (int x = -POCKET_RADIUS; x <= POCKET_RADIUS; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -POCKET_RADIUS; z <= POCKET_RADIUS; z++) {
                    BlockPos p = center.add(x, y, z);
                    world.setBlockState(p, Blocks.CAVE_AIR.getDefaultState());
                }
            }
        }

        // Make sure the player has a solid floor.
        for (int x = -POCKET_RADIUS; x <= POCKET_RADIUS; x++) {
            for (int z = -POCKET_RADIUS; z <= POCKET_RADIUS; z++) {
                BlockPos floor = center.add(x, -1, z);
                world.setBlockState(floor, Blocks.STONE.getDefaultState());
            }
        }

        // Teleport the player to the middle of the pocket.
        player.teleport(world,
                center.getX() + 0.5,
                center.getY(),
                center.getZ() + 0.5,
                player.getYaw(),
                player.getPitch());

        // Clear their inventory and give the starter kit.
        giveStarterKit(player);

        // Mark them as buried.
        setBuried(player, true);

        // Welcome / warning message.
        WelcomeNotifier.notifyBuried(player);
    }

    private static void giveStarterKit(ServerPlayerEntity player) {
        player.getInventory().clear();

        ItemStack pickaxe1 = new ItemStack(Items.STONE_PICKAXE);
        ItemStack pickaxe2 = new ItemStack(Items.STONE_PICKAXE);
        ItemStack torch = new ItemStack(Items.TORCH, 1);

        player.getInventory().insertStack(pickaxe1);
        player.getInventory().insertStack(pickaxe2);
        player.getInventory().insertStack(torch);
        player.getInventory().markDirty();
    }

    /**
     * Determines whether the player has escaped: they can see the sky
     * (nothing blocking above them up to the world surface).
     */
    public static boolean hasEscaped(ServerPlayerEntity player, ServerWorld world) {
        BlockPos pos = player.getBlockPos();
        // The player has escaped if they are at or above the surface height
        // and the sky is visible directly above them.
        int surfaceY = world.getTopY(Heightmap.Type.WORLD_SURFACE, pos.getX(), pos.getZ());
        boolean atSurface = pos.getY() >= surfaceY - 1;
        boolean skyVisible = world.isSkyVisible(pos);
        return atSurface && skyVisible && !isBlockedByStone(world, pos);
    }

    private static boolean isBlockedByStone(ServerWorld world, BlockPos pos) {
        // Check the columns above the player for solid stone/dirt blockage.
        BlockPos check = pos.up();
        int checks = 0;
        while (check.getY() < world.getTopY() && checks < 6) {
            if (!world.getBlockState(check).isIn(BlockTags.LEAVES)
                    && world.getBlockState(check).isSolidBlock(world, check)) {
                return true;
            }
            check = check.up();
            checks++;
        }
        return false;
    }
}