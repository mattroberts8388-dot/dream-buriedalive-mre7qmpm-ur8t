package com.buriedalive;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Sends the "you are buried alive" warning to a freshly buried player.
 */
public final class WelcomeNotifier {

    private WelcomeNotifier() {
    }

    public static void notifyBuried(ServerPlayerEntity player) {
        player.sendMessage(
                Text.translatable("text.buriedalive.welcome").formatted(Formatting.RED, Formatting.BOLD),
                false);
        player.sendMessage(
                Text.translatable("text.buriedalive.welcome").formatted(Formatting.GRAY),
                true);
    }
}