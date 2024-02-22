package com.TBK.chainmailed.common.mixins;

import com.TBK.chainmailed.common.api.IReinforcedChain;
import com.TBK.chainmailed.common.config.BKConfig;
import com.TBK.chainmailed.common.sound.BKSounds;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerMixin extends Player {

    @Shadow public abstract void setCamera(@Nullable Entity p_9214_);

    @Shadow @Final public ServerPlayerGameMode gameMode;

    public PlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(method = "attack",at = @At(value = "HEAD"), cancellable = true)
    public void attack(Entity p_36347_, CallbackInfo ci){
        ci.cancel();
        if (this.gameMode.getGameModeForPlayer() == GameType.SPECTATOR) {
            this.setCamera(p_36347_);
        } else {
            if (!net.minecraftforge.common.ForgeHooks.onPlayerAttackTarget(this, p_36347_)) return;
            if (p_36347_.isAttackable()) {
                if (!p_36347_.skipAttackInteraction(this)) {
                    float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
                    float f1;
                    if (p_36347_ instanceof LivingEntity) {
                        f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)p_36347_).getMobType());
                    } else {
                        f1 = EnchantmentHelper.getDamageBonus(this.getMainHandItem(), MobType.UNDEFINED);
                    }

                    float f2 = this.getAttackStrengthScale(0.5F);
                    f *= 0.2F + f2 * f2 * 0.8F;
                    f1 *= f2;
                    if (f > 0.0F || f1 > 0.0F) {
                        boolean flag = f2 > 0.9F;
                        boolean flag1 = false;
                        float i = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK); // Forge: Initialize this value to the attack knockback attribute of the player, which is by default 0
                        i += EnchantmentHelper.getKnockbackBonus(this);
                        if (this.isSprinting() && flag) {
                            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_KNOCKBACK, this.getSoundSource(), 1.0F, 1.0F);
                            ++i;
                            flag1 = true;
                        }

                        boolean flag2 = flag && this.fallDistance > 0.0F && !this.onGround() && !this.onClimbable() && !this.isInWater() && !this.hasEffect(MobEffects.BLINDNESS) && !this.isPassenger() && p_36347_ instanceof LivingEntity;
                        flag2 = flag2 && !this.isSprinting();
                        net.minecraftforge.event.entity.player.CriticalHitEvent hitResult = net.minecraftforge.common.ForgeHooks.getCriticalHit(this, p_36347_, flag2, flag2 ? 1.5F : 1.0F);
                        flag2 = hitResult != null;
                        if (flag2) {
                            f *= hitResult.getDamageModifier();
                        }

                        f += f1;
                        boolean flag3 = false;
                        double d0 = (double)(this.walkDist - this.walkDistO);
                        if (flag && !flag2 && !flag1 && this.onGround() && d0 < (double)this.getSpeed()) {
                            ItemStack itemstack = this.getItemInHand(InteractionHand.MAIN_HAND);
                            flag3 = itemstack.canPerformAction(net.minecraftforge.common.ToolActions.SWORD_SWEEP);
                        }

                        float f4 = 0.0F;
                        boolean flag4 = false;
                        int j = EnchantmentHelper.getFireAspect(this);
                        if (p_36347_ instanceof LivingEntity) {
                            f4 = ((LivingEntity)p_36347_).getHealth();
                            if (j > 0 && !p_36347_.isOnFire()) {
                                flag4 = true;
                                p_36347_.setSecondsOnFire(1);
                            }
                        }

                        Vec3 vec3 = p_36347_.getDeltaMovement();
                        boolean flag5 = p_36347_.hurt(this.damageSources().playerAttack(this), f);
                        if (flag5) {
                            if (i > 0) {
                                if (p_36347_ instanceof LivingEntity) {
                                    ((LivingEntity)p_36347_).knockback((double)((float)i * 0.5F), (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                                } else {
                                    p_36347_.push((double)(-Mth.sin(this.getYRot() * ((float)Math.PI / 180F)) * (float)i * 0.5F), 0.1D, (double)(Mth.cos(this.getYRot() * ((float)Math.PI / 180F)) * (float)i * 0.5F));
                                }

                                this.setDeltaMovement(this.getDeltaMovement().multiply(0.6D, 1.0D, 0.6D));
                                this.setSprinting(false);
                            }

                            if (flag3) {
                                float f3 = 1.0F + EnchantmentHelper.getSweepingDamageRatio(this) * f;
                                for(LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getItemInHand(InteractionHand.MAIN_HAND).getSweepHitBox(this, p_36347_))) {
                                    double entityReachSq = Mth.square(this.getEntityReach());
                                    if (livingentity != this && livingentity != p_36347_ && !this.isAlliedTo(livingentity) && (!(livingentity instanceof ArmorStand) || !((ArmorStand)livingentity).isMarker()) && this.distanceToSqr(livingentity) < entityReachSq) {
                                        if(this.hasntChainmailed(livingentity)){
                                            livingentity.knockback((double)0.4F, (double)Mth.sin(this.getYRot() * ((float)Math.PI / 180F)), (double)(-Mth.cos(this.getYRot() * ((float)Math.PI / 180F))));
                                            livingentity.hurt(this.damageSources().playerAttack(this), f3);
                                        }else{
                                            if(BKConfig.soundChainmailedBlock){
                                                livingentity.playSound(BKSounds.CHAINMAIL_BLOCK.get());
                                            }
                                        }
                                    }
                                }

                                this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, this.getSoundSource(), 1.0F, 1.0F);
                                this.sweepAttack();
                            }

                            if (p_36347_ instanceof ServerPlayer && p_36347_.hurtMarked) {
                                ((ServerPlayer)p_36347_).connection.send(new ClientboundSetEntityMotionPacket(p_36347_));
                                p_36347_.hurtMarked = false;
                                p_36347_.setDeltaMovement(vec3);
                            }

                            if (flag2) {
                                this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_CRIT, this.getSoundSource(), 1.0F, 1.0F);
                                this.crit(p_36347_);
                            }

                            if (!flag2 && !flag3) {
                                if (flag) {
                                    this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_STRONG, this.getSoundSource(), 1.0F, 1.0F);
                                } else {
                                    this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_WEAK, this.getSoundSource(), 1.0F, 1.0F);
                                }
                            }

                            if (f1 > 0.0F) {
                                this.magicCrit(p_36347_);
                            }

                            this.setLastHurtMob(p_36347_);
                            if (p_36347_ instanceof LivingEntity) {
                                EnchantmentHelper.doPostHurtEffects((LivingEntity)p_36347_, this);
                            }

                            EnchantmentHelper.doPostDamageEffects(this, p_36347_);
                            ItemStack itemstack1 = this.getMainHandItem();
                            Entity entity = p_36347_;
                            if (p_36347_ instanceof net.minecraftforge.entity.PartEntity) {
                                entity = ((net.minecraftforge.entity.PartEntity<?>) p_36347_).getParent();
                            }

                            if (!this.level().isClientSide && !itemstack1.isEmpty() && entity instanceof LivingEntity) {
                                ItemStack copy = itemstack1.copy();
                                itemstack1.hurtEnemy((LivingEntity)entity, this);
                                if (itemstack1.isEmpty()) {
                                    net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(this, copy, InteractionHand.MAIN_HAND);
                                    this.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
                                }
                            }

                            if (p_36347_ instanceof LivingEntity) {
                                float f5 = f4 - ((LivingEntity)p_36347_).getHealth();
                                this.awardStat(Stats.DAMAGE_DEALT, Math.round(f5 * 10.0F));
                                if (j > 0) {
                                    p_36347_.setSecondsOnFire(j * 4);
                                }

                                if (this.level() instanceof ServerLevel && f5 > 2.0F) {
                                    int k = (int)((double)f5 * 0.5D);
                                    ((ServerLevel)this.level()).sendParticles(ParticleTypes.DAMAGE_INDICATOR, p_36347_.getX(), p_36347_.getY(0.5D), p_36347_.getZ(), k, 0.1D, 0.0D, 0.1D, 0.2D);
                                }
                            }

                            this.causeFoodExhaustion(0.1F);
                        } else {
                            this.level().playSound((Player)null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_NODAMAGE, this.getSoundSource(), 1.0F, 1.0F);
                            if (flag4) {
                                p_36347_.clearFire();
                            }
                        }
                    }
                    this.resetAttackStrengthTicker(); // FORGE: Moved from beginning of attack() so that getAttackStrengthScale() returns an accurate value during all attack events

                }
            }
        }

    }

    public boolean hasntChainmailed(LivingEntity living){
        return !this.hasChainmailed(living);
    }

    public boolean hasChainmailed(LivingEntity living){
        boolean flag=true;
        for(ItemStack stack : living.getArmorSlots()){
            if(!isChainMailed(stack)){
                flag=false;
            }
        }
        return flag;
    }
    public boolean isChainMailed(ItemStack stack){
        return (stack.getItem() instanceof IReinforcedChain armor && armor.hasChainmailed(stack.getOrCreateTag())) || (stack.getItem() instanceof ArmorItem armorItem && armorItem.getMaterial() == ArmorMaterials.CHAIN);
    }
}
