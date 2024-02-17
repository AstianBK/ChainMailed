package com.TBK.chainmailed.common;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.TBK.chainmailed.network.PacketSyncSlashResistToClient;
import com.TBK.chainmailed.network.PacketHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.FlintAndSteelItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {
    public static final Attribute SLASH_RESISTANCE = new RangedAttribute("Slash Resistance", 0.0d, -Double.MAX_VALUE, Double.MAX_VALUE);

    @SubscribeEvent
    public static void RightClickOnItem(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();
        if (itemStack.getItem() instanceof ArmorItem armor) {
            Player player = event.getEntity();
            ItemStack itemStack1 = player.getOffhandItem();
            CompoundTag nbt = itemStack.getOrCreateTag();
            if (armor instanceof IReinforcedChain reinforcedChain && !reinforcedChain.hasChainmailed(nbt)
                    && itemStack1.getItem() instanceof ArmorItem armorItem && armor.getMaterial() != ArmorMaterials.CHAIN
                    && armorItem.getMaterial() == ArmorMaterials.CHAIN && armorItem.getType().getSlot() == armor.getType().getSlot()
                    && armorItem.getAllEnchantments(itemStack1).isEmpty()) {
                nbt.putBoolean("reinforcedChain", true);
                itemStack.save(nbt);
                itemStack1.shrink(1);
                player.playSound(SoundEvents.SMITHING_TABLE_USE);
            }
        }
    }




    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.getAttribute(Events.SLASH_RESISTANCE) != null) {
            float f0 = event.getAmount();
            double d0 = victim.getAttribute(Events.SLASH_RESISTANCE).getValue();
            if(d0>0.0D){
                if (event.getSource().is(DamageTypes.MOB_ATTACK) || event.getSource().is(DamageTypes.PLAYER_ATTACK) || event.getSource().is(DamageTypes.GENERIC)) {
                    double d1 =(float) (Math.max(f0 - d0,1.0F));

                    event.setAmount((float) d1);
                    victim.playSound(SoundEvents.CHICKEN_HURT);
                }
            }
        }

    }

}
