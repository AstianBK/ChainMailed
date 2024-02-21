package com.TBK.chainmailed.common;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.TBK.chainmailed.common.sound.CMSounds;
import com.TBK.chainmailed.network.PacketHandler;
import com.TBK.chainmailed.network.PacketSyncSlashResistToClient;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Events {
    public static final Attribute IMPACT_RESISTANCE = new RangedAttribute("attribute.impact_resistance",0.0d,-Double.MAX_VALUE, Double.MAX_VALUE);
    @SubscribeEvent
    public static void RightClickOnItem(PlayerInteractEvent.RightClickItem event){
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() instanceof ArmorItem armor){
            Player player = event.getEntity();
            ItemStack itemStack1 = player.getOffhandItem();
            CompoundTag nbt = itemStack.getOrCreateTag();
            if(armor instanceof IReinforcedChain reinforcedChain && !reinforcedChain.hasChainmailed(nbt) &&
                    itemStack1.getItem() instanceof ArmorItem armorItem && armor.getMaterial()!=ArmorMaterials.CHAIN
                    && armorItem.getMaterial() == ArmorMaterials.CHAIN && armorItem.getSlot()==armor.getSlot()){
                CompoundTag tag = new CompoundTag();
                itemStack1.save(tag);
                nbt.put("reinforcedChain",tag);
                itemStack1.shrink(1);
                player.playSound(SoundEvents.SMITHING_TABLE_USE);
            }else if(player.isShiftKeyDown() && armor instanceof IReinforcedChain reinforcedChain && reinforcedChain.hasChainmailed(nbt) && itemStack1.isEmpty()) {
                ItemStack chainmailed=ItemStack.of(nbt.getCompound("reinforcedChain"));
                player.setItemSlot(EquipmentSlot.OFFHAND,chainmailed);
                nbt.remove("reinforcedChain");
                player.playSound(SoundEvents.ANVIL_USE);
            }
        }
    }
    @SubscribeEvent
    public static void onTickEvent(TickEvent.LevelTickEvent event){
        event.level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer && serverPlayer.getAttribute(Events.IMPACT_RESISTANCE) != null) {
                float souls = (float) serverPlayer.getAttribute(Events.IMPACT_RESISTANCE).getValue();
                PacketHandler.sendToPlayer(new PacketSyncSlashResistToClient(souls), serverPlayer);
            }
        });
    }

    @SubscribeEvent
    public static void onHurtEvent(LivingHurtEvent event) {
        LivingEntity victim = event.getEntity();
        if (victim.getAttribute(Events.IMPACT_RESISTANCE) != null) {
            float f0 = event.getAmount();
            double d0 = victim.getAttribute(Events.IMPACT_RESISTANCE).getValue();
            if (d0 > 0) {
                if (event.getSource()==DamageSource.GENERIC || event.getSource().getEntity() instanceof Player || event.getSource().getEntity() instanceof Mob) {
                    double d1 = f0 - d0;
                    event.setAmount((float) (Math.max(d1, 1.0F)));
                    victim.playSound(CMSounds.CHAINMAIL_BLOCK.get());
                    if (victim instanceof Player player) {
                        player.level.playSound(null, victim, CMSounds.CHAINMAIL_BLOCK.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
                    }
                }
            }
        }

    }
}
