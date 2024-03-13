package com.TBK.chainmailed.common;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.TBK.chainmailed.common.config.BKConfig;
import com.TBK.chainmailed.common.sound.BKSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraftforge.event.ItemAttributeModifierEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class Events {
    private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[]{UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final Attribute IMPACT_RESISTANCE = new RangedAttribute("attribute.impact_resistance",0.0d,-Double.MAX_VALUE, Double.MAX_VALUE);
    @SubscribeEvent
    public static void RightClickOnItem(PlayerInteractEvent.RightClickItem event){
        ItemStack itemStack = event.getItemStack();
        if(itemStack.getItem() instanceof ArmorItem armor){
            Player player = event.getEntity();
            ItemStack itemStack1 = player.getOffhandItem();
            CompoundTag nbt = itemStack.getOrCreateTag();
            if(!BKConfig.chainMailedBlackList.contains(itemStack.getItem())){
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
    }

    @SubscribeEvent
    public static void onItemAttribute(ItemAttributeModifierEvent event){
        ItemStack stack = event.getItemStack();
        EquipmentSlot slot = event.getSlotType();
        if(stack.getItem() instanceof ArmorItem armorItem){
            int defense=armorItem.getDefense();
            UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
            int cc = ((ArmorItem)stack.getItem()).getMaterial()==ArmorMaterials.CHAIN ? getDefenseBonusForSlot(slot) : defense+getDefenseBonusForSlot(slot);
            if((((IReinforcedChain)armorItem).hasChainmailed(stack.getOrCreateTag()) || ((ArmorItem)stack.getItem()).getMaterial()==ArmorMaterials.CHAIN)
                    && slot==armorItem.getSlot()){
                event.removeAttribute(Attributes.ARMOR);
                event.addModifier(Attributes.ARMOR, new AttributeModifier(uuid, "Armor modifier", cc, AttributeModifier.Operation.ADDITION));
                event.addModifier(Events.IMPACT_RESISTANCE,new AttributeModifier(uuid,"Impact Resistance",BKConfig.impactResistanceValueChainmailedBasic,AttributeModifier.Operation.ADDITION));
            }
        }
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
                    if(BKConfig.soundChainmailedBlock){
                        victim.playSound(BKSounds.CHAINMAIL_BLOCK.get());
                        if (victim instanceof Player player) {
                            player.level.playSound(null, victim, BKSounds.CHAINMAIL_BLOCK.get(), SoundSource.PLAYERS, 1.5F, 1.0F);
                        }
                    }
                }
            }
        }
    }

    public static int getDefenseBonusForSlot(EquipmentSlot slot){
        return BKConfig.armorForSlotChainmailedBasic[slot.getIndex()];
    }
}
