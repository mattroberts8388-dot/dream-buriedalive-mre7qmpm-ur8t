package com.buriedalive;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Congratulates the player for escaping the collapsed cave.
 */
public final class EscapeNotifier {

    private EscapeNotifier() {
    }

    public static void notifyEscape(ServerPlayerEntity player) {
        player.sendMessage(
                Text.translatable("text.buriedalive.escaped").formatted(Formatting.GREEN, Formatting.BOLD),
                false);
    }
}