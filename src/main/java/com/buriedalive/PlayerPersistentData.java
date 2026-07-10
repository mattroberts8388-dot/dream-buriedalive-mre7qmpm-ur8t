package com.buriedalive;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Lightweight in-memory persistence for per-player mod state.
 *
 * We track which players have been buried during the current server session.
 * Because a player is re-buried on death/respawn anyway, an in-memory map is
 * sufficient for correct gameplay behaviour and avoids mixins entirely.
 */
public final class PlayerPersistentData {

    private static final Map<UUID, NbtCompound> DATA = new HashMap<>();

    private PlayerPersistentData() {
    }

    public static NbtCompound get(ServerPlayerEntity player) {
        return DATA.computeIfAbsent(player.getUuid(), k -> new NbtCompound());
    }
}