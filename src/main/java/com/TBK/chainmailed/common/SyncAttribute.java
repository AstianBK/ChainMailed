package com.TBK.chainmailed.common;

import com.TBK.chainmailed.network.PacketHandler;
import com.TBK.chainmailed.network.PacketSyncSlashResistToClient;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;

public class SyncAttribute {
    public static void onTickEvent(TickEvent.LevelTickEvent event) {
        event.level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.getAttribute(Events.SLASH_RESISTANCE) != null) {
                float souls = (float) serverPlayer.getAttribute(Events.SLASH_RESISTANCE).getValue();
                PacketHandler.sendToPlayer(new PacketSyncSlashResistToClient(souls), serverPlayer);
            }
        });
    }
}
