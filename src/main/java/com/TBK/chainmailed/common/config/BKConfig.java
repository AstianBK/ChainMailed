package com.TBK.chainmailed.common.config;

import com.TBK.chainmailed.ChainMailed;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ChainMailed.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BKConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CHAINMAILED_BLACKLIST = BUILDER
            .comment("A black list for chainmailed")
            .defineListAllowEmpty("chainmailed_blacklist", List.of("minecraft:leather_helmet","minecraft:leather_chestplate","minecraft:leather_leggings","minecraft:leather_boots"), BKConfig::validateItemName);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
    public static List<Item> chainMailedBlackList;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        chainMailedBlackList = CHAINMAILED_BLACKLIST.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                .collect(Collectors.toList());
    }
}
