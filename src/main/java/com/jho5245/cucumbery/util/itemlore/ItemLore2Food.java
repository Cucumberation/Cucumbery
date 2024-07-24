package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.custom.customeffect.VanillaEffectDescription;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.FoodComponent;
import org.bukkit.inventory.meta.components.FoodComponent.FoodEffect;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLore2Food
{
  protected static void setItemLore(@NotNull ItemStack itemStack, @NotNull Material type, @NotNull List<Component> lore, @Nullable Player viewer, boolean hideStatusEffects)
  {
    List<Component> foodLore = new ArrayList<>();

    ItemMeta itemMeta = itemStack.getItemMeta();
    if (itemMeta.hasFood())
    {
      List<FoodEffect> foodEffects = itemMeta.getFood().getEffects();
      for (FoodEffect foodEffect : foodEffects)
      {
        PotionEffect potionEffect = foodEffect.getEffect();
        float probability = foodEffect.getProbability();
        foodLore.add(ItemLorePotionDescription.getDescription(probability * 100, Component.translatable(potionEffect.getType().translationKey()), potionEffect.getDuration(), potionEffect.getAmplifier()));
        foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(potionEffect, viewer), NamedTextColor.GRAY));
      }
    }
    else
    {
      if (!hideStatusEffects && (!NBTAPI.isRestricted(itemStack, RestrictionType.NO_CONSUME) || NBTAPI.getRestrictionOverridePermission(itemStack, RestrictionType.NO_CONSUME) != null))
      {
        switch (type)
        {
          case GOLDEN_APPLE ->
          {
            PotionEffect absorption = new PotionEffect(PotionEffectType.ABSORPTION, 2 * 60 * 20, 0);
            PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1);
            foodLore.add(ItemLorePotionDescription.getDescription(absorption));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(absorption), NamedTextColor.GRAY));
            foodLore.add(ItemLorePotionDescription.getDescription(regeneration));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(regeneration), NamedTextColor.GRAY));
          }
          case ENCHANTED_GOLDEN_APPLE ->
          {
            PotionEffect absorption = new PotionEffect(PotionEffectType.ABSORPTION, 2 * 60 * 20, 3);
            PotionEffect regeneration = new PotionEffect(PotionEffectType.REGENERATION, 20 * 20, 1);
            PotionEffect resistance = new PotionEffect(PotionEffectType.RESISTANCE, 5* 60 * 20, 0);
            PotionEffect fire_resistance = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 5 * 60 * 20, 0);
            foodLore.add(ItemLorePotionDescription.getDescription(absorption));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(absorption), NamedTextColor.GRAY));
            foodLore.add(ItemLorePotionDescription.getDescription(regeneration));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(regeneration), NamedTextColor.GRAY));
            foodLore.add(ItemLorePotionDescription.getDescription(resistance));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(resistance), NamedTextColor.GRAY));
            foodLore.add(ItemLorePotionDescription.getDescription(fire_resistance));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(fire_resistance), NamedTextColor.GRAY));
          }
          case POISONOUS_POTATO ->
          {
            PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 4 * 20, 0);
            foodLore.add(ItemLorePotionDescription.getDescription(60d, poison));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(poison), NamedTextColor.GRAY));
          }
          case SPIDER_EYE ->
          {
            PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 4 * 20, 0);
            foodLore.add(ItemLorePotionDescription.getDescription(poison));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(poison), NamedTextColor.GRAY));
          }
          case PUFFERFISH ->
          {
            PotionEffect hunger = new PotionEffect(PotionEffectType.HUNGER, 15 * 20, 2);
            PotionEffect nausea = new PotionEffect(PotionEffectType.NAUSEA, 15 * 20, 1);
            PotionEffect poison = new PotionEffect(PotionEffectType.POISON, 60 * 20, 3);
            foodLore.add(ItemLorePotionDescription.getDescription(hunger));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(hunger), NamedTextColor.GRAY));
            foodLore.add(ItemLorePotionDescription.getDescription(nausea));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(nausea), NamedTextColor.GRAY));
            foodLore.add(ItemLorePotionDescription.getDescription(poison));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(poison), NamedTextColor.GRAY));
          }
          case ROTTEN_FLESH ->
          {
            PotionEffect hunger = new PotionEffect(PotionEffectType.HUNGER, 30 * 20, 0);
            foodLore.add(ItemLorePotionDescription.getDescription(80d, hunger));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(hunger), NamedTextColor.GRAY));
          }
          case CHICKEN ->
          {
            PotionEffect hunger = new PotionEffect(PotionEffectType.HUNGER, 30 * 20, 0);
            foodLore.add(ItemLorePotionDescription.getDescription(30d, hunger));
            foodLore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(hunger), NamedTextColor.GRAY));
          }
          case HONEY_BOTTLE -> foodLore.add(ComponentUtil.translate(ItemLorePotionDescription.POTION_DESCRIPTION_COLOR + "%s 효과 제거",
							ItemLorePotionDescription.getComponent(PotionEffectType.POISON)));
          case MILK_BUCKET -> foodLore.add(
              ComponentUtil.translate(ItemLorePotionDescription.POTION_DESCRIPTION_COLOR + "모든 효과 제거 (일부 효과 제외)"));
        }
      }
    }
    if (ItemStackUtil.isEdible(type) && type != Material.POTION && type != Material.SUSPICIOUS_STEW)
    {
      foodLore.addAll(ItemLorePotionDescription.getCustomEffectList(viewer, itemStack));
    }
    if (!foodLore.isEmpty())
    {
      lore.addAll(Arrays.asList(Component.empty(), ComponentUtil.translate(Constant.ITEM_LORE_STATUS_EFFECT)));
      lore.addAll(foodLore);
    }
  }
}
