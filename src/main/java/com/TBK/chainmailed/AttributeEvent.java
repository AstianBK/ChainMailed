package com.TBK.chainmailed;

import com.TBK.chainmailed.common.Events;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class AttributeEvent {
    @SubscribeEvent
    public static void onEntityAttributeModificationEvent(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, Events.SLASH_RESISTANCE);
        event.add(EntityType.PIGLIN, Events.SLASH_RESISTANCE);
        event.add(EntityType.PIGLIN_BRUTE, Events.SLASH_RESISTANCE);
        event.add(EntityType.ZOMBIFIED_PIGLIN, Events.SLASH_RESISTANCE);
        event.add(EntityType.WITHER_SKELETON, Events.SLASH_RESISTANCE);
        event.add(EntityType.SKELETON, Events.SLASH_RESISTANCE);
        event.add(EntityType.STRAY, Events.SLASH_RESISTANCE);
        event.add(EntityType.HUSK,Events.SLASH_RESISTANCE);
        event.add(EntityType.DROWNED,Events.SLASH_RESISTANCE);
        event.add(EntityType.ZOMBIE,Events.SLASH_RESISTANCE);

    }
}