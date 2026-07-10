package com.buriedalive;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Registers server events that detect when a player first joins (or respawns)
 * and buries them in a collapsed cave with the starter equipment.
 */
public class BuriedAliveEvents {

    // Players that still need to be buried once the world has loaded around them.
    private static final Set<UUID> pendingBurial = new HashSet<>();

    // Players already marked as escaped so we don't spam the message.
    private static final Set<UUID> escapedPlayers = new HashSet<>();

    public static void register() {
        // When a player copies data (first login or respawn), decide if they need burying.
        ServerPlayerEvents.COPY_FROM.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                // Player died and respawned -> bury them again.
                pendingBurial.add(newPlayer.getUuid());
                escapedPlayers.remove(newPlayer.getUuid());
            }
        });

        // When a player fully joins for the first time we also want to bury them.
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            pendingBurial.add(newPlayer.getUuid());
            escapedPlayers.remove(newPlayer.getUuid());
        });

        // Handle first-ever join and process the queue of players to bury.
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                UUID id = player.getUuid();

                // First join detection: give burial once if the player has never been processed.
                if (!CaveBuilder.hasBeenBuried(player) && !pendingBurial.contains(id)) {
                    pendingBurial.add(id);
                }

                if (pendingBurial.contains(id)) {
                    if (player.getWorld() instanceof ServerWorld world) {
                        CaveBuilder.buryPlayer(player, world);
                        pendingBurial.remove(id);
                    }
                }

                // Escape detection: player reaches the surface / open sky.
                if (!escapedPlayers.contains(id) && CaveBuilder.hasBeenBuried(player)) {
                    if (player.getWorld() instanceof ServerWorld world) {
                        if (CaveBuilder.hasEscaped(player, world)) {
                            escapedPlayers.add(id);
                            EscapeNotifier.notifyEscape(player);
                        }
                    }
                }
            }
        });
    }
}