package com.jho5245.cucumbery.custom.customeffect.custom_mining;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record MiningResult(boolean canMine, boolean overrideMode2, double toolSpeed, double miningSpeed, double miningSpeedBeforeHaste, double blockHardness, double miningFortune, double expToDrop, int miningTier, int blockTier, int regenCooldown, List<ItemStack> drops, @Nullable
                           Sound breakSound, @Nullable String breakCustomSound, float breakSoundVolume, float breakSoundPitch, String breakParticle)
{
  public MiningResult(boolean canMine, boolean overrideMode2, double toolSpeed, double miningSpeed, double miningSpeedBeforeHaste, double blockHardness, double miningFortune, double expToDrop, int miningTier, int blockTier, int regenCooldown, @NotNull List<ItemStack> drops, @Nullable
  Sound breakSound, @Nullable String breakCustomSound, float breakSoundVolume, float breakSoundPitch, String breakParticle)
  {
    this.canMine = canMine;
    this.overrideMode2 = overrideMode2;
    this.toolSpeed = toolSpeed;
    this.miningSpeed = miningSpeed;
    this.miningSpeedBeforeHaste = miningSpeedBeforeHaste;
    this.blockHardness = blockHardness;
    this.miningFortune = miningFortune;
    this.expToDrop = expToDrop;
    this.miningTier = miningTier;
    this.blockTier = blockTier;
    this.regenCooldown = regenCooldown;
    this.drops = drops;
    this.breakSound = breakSound;
    this.breakCustomSound = breakCustomSound;
    this.breakSoundVolume = breakSoundVolume;
    this.breakSoundPitch = breakSoundPitch;
    this.breakParticle = breakParticle;
  }
}
