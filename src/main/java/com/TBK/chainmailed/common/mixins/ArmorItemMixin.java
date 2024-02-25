package com.TBK.chainmailed.common.mixins;

import com.TBK.chainmailed.common.Events;
import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.TBK.chainmailed.common.config.BKConfig;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.EnumMap;
import java.util.List;
import java.util.UUID;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin extends Item implements IReinforcedChain {

    @Shadow public abstract InteractionResultHolder<ItemStack> use(Level p_40395_, Player p_40396_, InteractionHand p_40397_);

    @Shadow @Final private float toughness;

    @Shadow @Final protected float knockbackResistance;

    @Shadow @Final private int defense;
    @Shadow @Final private static UUID[] ARMOR_MODIFIER_UUID_PER_SLOT;
    @Shadow @Final protected EquipmentSlot slot;

    @Shadow public abstract EquipmentSlot getSlot();

    private final double slash_resistance=0.5D;

    public ArmorItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean hasChainmailed(CompoundTag tag) {
        if(!tag.contains("reinforcedChain")){
            return false;
        }
        return !ItemStack.of(tag.getCompound("reinforcedChain")).isEmpty();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        int cc = ((ArmorItem)stack.getItem()).getMaterial()==ArmorMaterials.CHAIN ? this.getDefenseBonusForSlot(slot) : this.defense+this.getDefenseBonusForSlot(slot);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_SLOT[slot.getIndex()];
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "armor modifier", cc, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        builder.put(Events.IMPACT_RESISTANCE,new AttributeModifier(uuid,"Impact Resist",BKConfig.impactResistanceValueChainmailedBasic, AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        if((hasChainmailed(stack.getOrCreateTag()) || ((ArmorItem)stack.getItem()).getMaterial()==ArmorMaterials.CHAIN) && slot==this.getSlot() ){
            return builder.build();
        }else {
            return super.getAttributeModifiers(slot, stack);
        }
    }


    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        if(hasChainmailed(p_41421_.getOrCreateTag())){
            p_41423_.add(Component.translatable("armoritem.chainmailed.has_chainmailed").withStyle(ChatFormatting.GRAY));
        }
    }

    public int getDefenseBonusForSlot(EquipmentSlot slot){
        return BKConfig.armorForSlotChainmailedBasic[slot.getIndex()];
    }
}
