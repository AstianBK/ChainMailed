package com.TBK.chainmailed.network;

import com.TBK.chainmailed.common.Events;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketSyncSlashResistToClient {

    private final float armor;

    public PacketSyncSlashResistToClient(float playerSoul) {
        this.armor = playerSoul;
    }

    public PacketSyncSlashResistToClient(FriendlyByteBuf buf) {
        armor = buf.readFloat();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(armor);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Minecraft.getInstance().player.getAttribute(Events.SLASH_RESISTANCE).setBaseValue(armor);
        });
        return true;
    }
}
