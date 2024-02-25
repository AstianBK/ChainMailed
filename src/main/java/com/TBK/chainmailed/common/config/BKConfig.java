package com.TBK.chainmailed.common.config;

import com.TBK.chainmailed.ChainMailed;
import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = ChainMailed.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BKConfig
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.BooleanValue SOUND_CHAINMAILED_BLOCK = BUILDER
            .comment("enable chainmail block sound")
            .define("sound_chainmailed_block",true);
    private static final ForgeConfigSpec.DoubleValue IMPACT_RESISTANCE_CHAINMAILED_BASIC = BUILDER
            .comment("How much base Impact Resistance chainmail armor has")
            .defineInRange("impact_resistance_chainmailed_basic",0.5D,0.1D,Double.MAX_VALUE);
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> ARMOR_FOR_SLOT_CHAIMAILED_BASIC = BUILDER
            .comment("How much base armor chainmail armor has")
            .defineList("armor_for_slot_chainmailed_basic", Lists.newArrayList("head:1","chest:2","legs:2","feet:1"),BKConfig::validateStringName);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> CHAINMAILED_BLACKLIST = BUILDER
            .comment("A blacklist for armors that can't be chainmailed")
            .defineList("chainmailed_blacklist", Lists.newArrayList(""), BKConfig::validateItemName);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
    public static boolean soundChainmailedBlock;
    public static double impactResistanceValueChainmailedBasic;
    public static List<Item> chainMailedBlackList;
    public static int[] armorForSlotChainmailedBasic;

    private static boolean validateItemName(final Object obj)
    {
        return obj instanceof final String itemName && ForgeRegistries.ITEMS.containsKey(new ResourceLocation(itemName));
    }
    private static boolean validateStringName(final Object obj)
    {
        return obj instanceof final String armorValue && (armorValue.contains("head:") || armorValue.contains("chest:") || armorValue.contains("legs:") || armorValue.contains("feet:")) ;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        soundChainmailedBlock = SOUND_CHAINMAILED_BLOCK.get();
        impactResistanceValueChainmailedBasic = IMPACT_RESISTANCE_CHAINMAILED_BASIC.get();
        armorForSlotChainmailedBasic = getArmor(new ArrayList<>(ARMOR_FOR_SLOT_CHAIMAILED_BASIC.get()));
        chainMailedBlackList = CHAINMAILED_BLACKLIST.get().stream()
                .map(itemName -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName)))
                .collect(Collectors.toList());
    }

    static int[] getArmor(List<String> strings){
        int[] ints = new int[]{0,0,0,0};
        for (String s:strings){
            String[] s0 = s.split(":");
            String slot = s0[0];
            EquipmentSlot equipmentSlot = EquipmentSlot.byName(slot);
            int armor = Integer.parseInt(s0[1]);
            ints[equipmentSlot.getIndex()]=armor;
        }
        return ints;
    }
}
