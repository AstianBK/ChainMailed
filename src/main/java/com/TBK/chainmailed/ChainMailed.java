package com.TBK.chainmailed;

import com.TBK.chainmailed.common.Events;
import com.TBK.chainmailed.common.SyncAttribute;
import com.TBK.chainmailed.common.sound.CMSounds;
import com.TBK.chainmailed.network.PacketHandler;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod(ChainMailed.MODID)
public class ChainMailed
{
    public static final String MODID = "chainmailed";
    private static final Logger LOGGER = LogUtils.getLogger();
    public ChainMailed()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(SyncAttribute::onTickEvent);
        CMSounds.register(eventBus);
        PacketHandler.registerMessages();
        DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, ChainMailed.MODID);
        ATTRIBUTES.register("impact_resistance",()-> Events.IMPACT_RESISTANCE);
        ATTRIBUTES.register(eventBus);
    }

}
