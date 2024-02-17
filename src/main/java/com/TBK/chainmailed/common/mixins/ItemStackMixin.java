package com.TBK.chainmailed.common.mixins;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract CompoundTag getOrCreateTag();

    @Shadow public abstract boolean isDamageableItem();

    @Shadow public abstract void shrink(int p_41775_);

    @Shadow public abstract boolean hurt(int p_220158_, RandomSource p_220159_, @Nullable ServerPlayer p_220160_);

    @Shadow public abstract void setDamageValue(int p_41722_);

    @Shadow public abstract ItemStack copy();


    @Inject(method = "hurtAndBreak",at = @At(value = "HEAD"), cancellable = true)
    public <T extends LivingEntity> void hurtAndBreak(int p_41623_, T p_41624_, Consumer<T> p_41625_, CallbackInfo ci){
        ci.cancel();
        if (!p_41624_.level().isClientSide && (!(p_41624_ instanceof Player) || !((Player)p_41624_).getAbilities().instabuild)) {
            if (this.isDamageableItem()) {
                p_41623_ = this.getItem().damageItem(this.copy(), p_41623_, p_41624_, p_41625_);
                if (this.hurt(p_41623_, p_41624_.getRandom(), p_41624_ instanceof ServerPlayer ? (ServerPlayer)p_41624_ : null)) {
                    p_41625_.accept(p_41624_);
                    Item item = this.getItem();
                    this.shrink(1);
                    if (p_41624_ instanceof Player) {
                        ((Player)p_41624_).awardStat(Stats.ITEM_BROKEN.get(item));
                    }

                    if (item instanceof ArmorItem reinforcedChain && ((IReinforcedChain)reinforcedChain).hasChainmailed(this.getOrCreateTag())) {
                        ItemStack stack = this.getArmorForSlot(((ArmorItem)item).getEquipmentSlot());
                        p_41624_.setItemSlot(((ArmorItem)item).getEquipmentSlot(),stack);
                    }
                    this.setDamageValue(0);
                }

            }
        }
    }

    public ItemStack getArmorForSlot(EquipmentSlot slot){
        switch (slot){
            case HEAD -> {
                return new ItemStack(Items.CHAINMAIL_HELMET);
            }
            case CHEST -> {
                return new ItemStack(Items.CHAINMAIL_CHESTPLATE);
            }
            case LEGS -> {
                return new ItemStack(Items.CHAINMAIL_LEGGINGS);
            }
            case FEET -> {
                return new ItemStack(Items.CHAINMAIL_BOOTS);
            }
        }
        return ItemStack.EMPTY;
    }
}
