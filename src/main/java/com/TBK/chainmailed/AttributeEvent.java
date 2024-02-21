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
        event.add(EntityType.PLAYER, Events.IMPACT_RESISTANCE);
        event.add(EntityType.PIGLIN, Events.IMPACT_RESISTANCE);
        event.add(EntityType.PIGLIN_BRUTE, Events.IMPACT_RESISTANCE);
        event.add(EntityType.ZOMBIFIED_PIGLIN, Events.IMPACT_RESISTANCE);
        event.add(EntityType.WITHER_SKELETON, Events.IMPACT_RESISTANCE);
        event.add(EntityType.SKELETON, Events.IMPACT_RESISTANCE);
        event.add(EntityType.STRAY, Events.IMPACT_RESISTANCE);
        event.add(EntityType.HUSK,Events.IMPACT_RESISTANCE);
        event.add(EntityType.DROWNED,Events.IMPACT_RESISTANCE);
        event.add(EntityType.ZOMBIE,Events.IMPACT_RESISTANCE);



    }

}