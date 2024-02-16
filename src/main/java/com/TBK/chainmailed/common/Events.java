package com.TBK.chainmailed.common;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {
    @SubscribeEvent
    public static void RightClickOnItem(PlayerInteractEvent.RightClickItem event){
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() instanceof ArmorItem armor){
            Player player = event.getEntity();
            ItemStack itemStack1 = player.getOffhandItem();
            CompoundTag nbt = itemStack.getOrCreateTag();
            if(itemStack1.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.CHAIN && armorItem.getType().getSlot()==armor.getType().getSlot()){
                nbt.putBoolean("reinforcedChain",true);
                itemStack.save(nbt);
                itemStack1.shrink(1);
                player.playSound(SoundEvents.SMITHING_TABLE_USE);
            }
        }
    }
}
