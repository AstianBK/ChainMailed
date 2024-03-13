package com.TBK.chainmailed.common.mixins;

import com.TBK.chainmailed.common.Events;
import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.TBK.chainmailed.common.config.BKConfig;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
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
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Mixin(ArmorItem.class)
public abstract class ArmorItemMixin extends Item implements IReinforcedChain {
    public ArmorItemMixin(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
        if(hasChainmailed(p_41421_.getOrCreateTag())){
            p_41423_.add(Component.translatable("armoritem.chainmailed.has_chainmailed").withStyle(ChatFormatting.GRAY));
        }
    }

    public boolean hasChainmailed(CompoundTag tag) {
        if(!tag.contains("reinforcedChain")){
            return false;
        }
        return !ItemStack.of(tag.getCompound("reinforcedChain")).isEmpty();
    }

}
