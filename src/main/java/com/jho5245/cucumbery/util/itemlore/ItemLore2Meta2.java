package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.storage.component.ItemStackComponent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemLore2Meta2
{
  protected static void setItemLore(@NotNull ItemStack item, @NotNull Material type, @NotNull ItemMeta itemMeta, @NotNull List<Component> lore, @NotNull NBTItem nbtItem, boolean hideFireworkEffects)
  {
    switch (type)
    {
      case FIREWORK_ROCKET ->
      {
        FireworkMeta fireworkMeta = (FireworkMeta) itemMeta;
        fireworkMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        int power = fireworkMeta.getPower();
        if (power >= 0 && power <= 127)
        {
          ItemLoreUtil.setItemRarityValue(lore, 20 * power);
        }
        if (!hideFireworkEffects)
        {
          lore.add(Component.empty());
          if (power >= 0 && power <= 127)
          {
            lore.add(ComponentUtil.translate("&7체공 시간 : %s", ComponentUtil.translate("&6약 ").append(ComponentUtil.translate("&6%s초", "" + (0.5d * (power + 1d) + 0.3)))));
          }
          else if (power == 255)
          {
            lore.add(ComponentUtil.translate("&7체공 시간 : %s", ComponentUtil.translate("&6약 ").append(ComponentUtil.translate("&6%s초", "0.3"))));
          }
          else
          {
            lore.add(ComponentUtil.translate("&7체공 시간 : %s", ComponentUtil.translate("&6즉시 폭발")));
          }

          // 폭발형 폭죽은 효과 출력 안함
          if (fireworkMeta.hasEffects()/* &&
                  customMaterial != CustomMaterial.FIREWORK_ROCKET_EXPLOSIVE &&
                  customMaterial != CustomMaterial.FIREWORK_ROCKET_EXPLOSIVE_DESTRUCTION && customMaterial != CustomMaterial.FIREWORK_ROCKET_EXPLOSIVE_FLAME*/)
          {
            lore.add(Component.empty());
            lore.add(ComponentUtil.translate("rg255,204;[폭죽 효과 목록]"));

            int effectSize = fireworkMeta.getEffectsSize();
            for (int i = 0; i < fireworkMeta.getEffectsSize(); i++)
            {
              if (effectSize > 5)
              {
                int skipped = effectSize - 5;
                if (i > 2 && i < effectSize - 2)
                {
                  if (i == 3)
                  {
                    lore.add(ComponentUtil.translate("&7&ocontainer.shulkerBox.more", Component.text(skipped)));
                  }
                  continue;
                }
              }
              Component add = ComponentUtil.translate("&3&m          %s          ", ComponentUtil.translate("&m&q[%s]", ComponentUtil.translate("&9%s번째 효과", i + 1)));
              lore.add(add);
              FireworkEffect fireworkEffect = fireworkMeta.getEffects().get(i);
              ItemLoreUtil.addFireworkEffectLore(lore, fireworkEffect);
            }
          }
        }
      }
      case EXPERIENCE_BOTTLE ->
      {
        lore.add(Component.empty());
        int minExp = 3, maxExp = 11;
        if (nbtItem.hasTag("MinExp") && nbtItem.getType("MinExp") == NBTType.NBTTagInt)
        {
          minExp = nbtItem.getInteger("MinExp");
        }
        if (nbtItem.hasTag("MaxExp") && nbtItem.getType("MaxExp") == NBTType.NBTTagInt)
        {
          maxExp = nbtItem.getInteger("MaxExp");
        }
        if (minExp > maxExp)
        {
          minExp = maxExp;
        }
        lore.add(ComponentUtil.translate("&7경험치 : %s", minExp != maxExp ? ComponentUtil.translate("&a%s~%s", minExp, maxExp) : "&a" + maxExp));
//        if (customMaterial == null)
        {
          ItemLoreUtil.setItemRarityValue(lore, (long) (minExp * 0.01 + maxExp * 0.001));
        }

      }
      case BUNDLE -> {
        if (itemMeta instanceof BundleMeta bundleMeta)
        {
          List<ItemStack> noDrops = new ArrayList<>(), noTrades = new ArrayList<>();
          for (ItemStack itemStack : bundleMeta.getItems())
          {
            if (NBTAPI.isRestricted(itemStack, RestrictionType.NO_DROP))
            {
              noDrops.add(itemStack);
            }
            if (NBTAPI.isRestricted(itemStack, RestrictionType.NO_TRADE))
            {
              noTrades.add(itemStack);
            }
          }
          if (!noDrops.isEmpty())
          {
            lore.add(Component.empty());
            lore.add(ComponentUtil.translate("&c%s에 버릴 수 없는 아이템이 들어있습니다!", ItemNameUtil.itemName(item, NamedTextColor.RED)));
            for (ItemStack itemStack : noDrops)
            {
              lore.add(ItemStackComponent.itemStackComponent(itemStack, NamedTextColor.GRAY));
            }
          }
          if (!noTrades.isEmpty())
          {
            lore.add(Component.empty());
            lore.add(ComponentUtil.translate("&c%s에 캐릭터 귀속 아이템이 들어있습니다!", ItemNameUtil.itemName(item, NamedTextColor.RED)));
            for (ItemStack itemStack : noTrades)
            {
              lore.add(ItemStackComponent.itemStackComponent(itemStack, NamedTextColor.GRAY));
            }
          }
        }
      }
      case SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, COAST_ARMOR_TRIM_SMITHING_TEMPLATE, DUNE_ARMOR_TRIM_SMITHING_TEMPLATE, EYE_ARMOR_TRIM_SMITHING_TEMPLATE, HOST_ARMOR_TRIM_SMITHING_TEMPLATE,
          RAISER_ARMOR_TRIM_SMITHING_TEMPLATE, RIB_ARMOR_TRIM_SMITHING_TEMPLATE, SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE, SILENCE_ARMOR_TRIM_SMITHING_TEMPLATE, SNOUT_ARMOR_TRIM_SMITHING_TEMPLATE,
          SPIRE_ARMOR_TRIM_SMITHING_TEMPLATE, TIDE_ARMOR_TRIM_SMITHING_TEMPLATE, VEX_ARMOR_TRIM_SMITHING_TEMPLATE, WARD_ARMOR_TRIM_SMITHING_TEMPLATE, WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE,
          WILD_ARMOR_TRIM_SMITHING_TEMPLATE -> {
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        String pattern = type.toString().toLowerCase().split("_")[0];
        lore.add(Component.empty());
        lore.add(ComponentUtil.translate("&7key:cucumbery.item_lore.description.trim_smithing_template|%s에서 갑옷에 주괴 및 수정으로 장식할 수 있다.", Material.SMITHING_TABLE));
        lore.add(Component.empty());
        lore.add(ComponentUtil.translate("&ekey:cucumbery.item_lore.trim_smithing_template|형판 유형 : %s", ComponentUtil.translate("&6trim_pattern.minecraft." + pattern)));
      }
      case NETHERITE_UPGRADE_SMITHING_TEMPLATE -> {
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        lore.add(Component.empty());
        lore.add(ComponentUtil.translate("&7key:cucumbery.item_lore.description.netherite_upgrade_smithing_template|%s에서 다이아몬드 장비와 네더라이트 주괴를", Material.SMITHING_TABLE));
        lore.add(ComponentUtil.translate("&7key:cucumbery.item_lore.description.netherite_upgrade_smithing_template_2|사용하여 네더라이트 장비로 업그레이드할 수 있다."));
      }
    }

    if (itemMeta instanceof ArmorMeta armorMeta)
    {
      if (armorMeta.hasTrim())
      {
        ArmorTrim armorTrim = armorMeta.getTrim();
        if (armorTrim == null)
        {
          itemMeta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
          return;
        }
        itemMeta.addItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
        TrimMaterial trimMaterial = armorTrim.getMaterial();
        TrimPattern trimPattern = armorTrim.getPattern();
        lore.add(Component.empty());
        lore.add(ComponentUtil.translate("&akey:cucumbery.item_lore.armor_trim_smithing_template|[형판 장식]"));
        String color = switch (trimMaterial.getKey().getKey()) {
          case "amethyst" -> "rgb152,91,196;";
          case "copper" -> "rgb178,103;76;";
          case "diamond" -> "rgb109,233,207;";
          case "emerald" -> "rgb17,158,53;";
          case "gold" -> "rgb219,175,44;";
          case "iron" -> "rgb233,233,233;";
          case "lapis" -> "rgb64,109,149;";
          case "netherite" -> "rgb97,87,88;";
          case "quartz" -> "rgb224,209,194;";
          case "redstone" -> "rgb149,22,7;";
          default -> "&7&o";
        };
        lore.add(ComponentUtil.translate(color + "trim_pattern.minecraft." + trimPattern.getKey().getKey()));
        lore.add(ComponentUtil.translate(color + "trim_material.minecraft." + trimMaterial.getKey().getKey()));
      }
      else
      {
        itemMeta.removeItemFlags(ItemFlag.HIDE_ARMOR_TRIM);
      }
    }
  }
}
