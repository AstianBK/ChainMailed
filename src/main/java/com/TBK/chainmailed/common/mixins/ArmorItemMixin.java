package com.TBK.chainmailed.common.mixins;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumMap;
import java.util.UUID;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin extends Item implements IReinforcedChain {
    @Shadow @Final private static EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE;

    @Shadow public abstract int getDefense();

    @Shadow @Final protected ArmorItem.Type type;

    @Shadow public abstract InteractionResultHolder<ItemStack> use(Level p_40395_, Player p_40396_, InteractionHand p_40397_);

    @Shadow @Final private float toughness;

    @Shadow @Final protected float knockbackResistance;

    @Shadow @Final private int defense;

    public ArmorItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public boolean hasChainmailed(CompoundTag tag) {
        if(!tag.contains("reinforcedChain")){
            return false;
        }
        return tag.getBoolean("reinforcedChain");
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(this.type);
        builder.put(Attributes.ARMOR, new AttributeModifier(uuid, "armor modifier", this.defense+this.getDefenseBonusForSlot(slot), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(uuid, "Armor toughness", (double)this.toughness, AttributeModifier.Operation.ADDITION));
        if (this.knockbackResistance > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor knockback resistance", (double)this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        if(hasChainmailed(stack.getOrCreateTag()) && slot==this.type.getSlot()){
            return builder.build();
        }else {
            return super.getAttributeModifiers(slot, stack);
        }
    }

    public int getDefenseBonusForSlot(EquipmentSlot slot){
        switch (slot){
            case HEAD,FEET -> {
                return 1;
            }
            case CHEST, LEGS -> {
                return 2;
            }
        }
        return 0;
    }
}
