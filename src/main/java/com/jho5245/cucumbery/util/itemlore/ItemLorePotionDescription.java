package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.custom.customeffect.CustomEffect;
import com.jho5245.cucumbery.custom.customeffect.VanillaEffectDescription;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import de.tr7zw.changeme.nbtapi.NBTCompoundList;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.CookiePolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ItemLorePotionDescription
{
  public static final Component NONE = ComponentUtil.translate("&7effect.none");

  @NotNull
  public static Component getComponent(@NotNull PotionEffectType potionEffectType)
  {
    return Component.translatable(potionEffectType.translationKey());
  }

  public static final String POTION_DESCRIPTION_COLOR = "rgb255,97,144;";

  public static Component getDescription(@NotNull PotionEffect potionEffect)
  {
    return getDescription(100d, potionEffect);
  }

  public static Component getDescription(double chance, @NotNull PotionEffect potionEffect)
  {
    return getDescription(chance, Component.translatable(potionEffect.getType().translationKey()), potionEffect.getDuration(), potionEffect.getAmplifier() + 1);
  }

  /**
   * @param level must be amplifier + 1
   * @return description
   */
  @NotNull
  public static Component getDescription(double chance, @NotNull Component effect, long duration, int level)
  {
    List<Component> args = new ArrayList<>();
    if (chance != 100d)
    {
      args.add(Component.text(Constant.Sosu2.format(chance) + "%"));
    }
    args.add(effect);
    if (level > 0)
    {
      args.add(Component.text(level + 1));
    }
    if (duration > 0)
    {
      args.add(ComponentUtil.timeFormat(duration * 50L));
    }
    String key = getKey(chance, duration, level);
    return ComponentUtil.translate(key, args);
  }

  private static @NotNull String getKey(double chance, long duration, int level)
  {
    String key = POTION_DESCRIPTION_COLOR + (chance == 100d ? "" : "%s 확률로 ");
    if (level > 0 && duration > 0)
    {
      key += "%s %s단계 (%s)";
    }
    else if (level == 0 && duration > 0)
    {
      key += "%s (%s)";
    }
    else if (level > 0)
    {
      if (duration == -1)
      {
        key += "%s %s단계 (무제한)";
      }
      else
      {
        key += "%s %s단계";
      }
    }
    else
    {
      if (duration == -1)
      {
        key += "%s (무제한)";
      }
      else
      {
        key += "%s";
      }
    }
    return key;
  }

  @NotNull
  public static Component getDescription(@NotNull Component effect, long duration)
  {
    return getDescription(100d, effect, duration, 1);
  }

  /**
   * @param level must be amplifier + 1
   * @return description
   */
  @NotNull
  public static Component getDescription(@NotNull Component effect, long duration, int level)
  {
    return getDescription(100d, effect, duration, level);
  }

  public static List<Component> getPotionList(@Nullable Player viewer, ItemStack item)
  {
    List<Component> lore = new ArrayList<>(Arrays.asList(Component.empty(), ComponentUtil.translate(Constant.ITEM_LORE_STATUS_EFFECT)));
    PotionMeta potionMeta = (PotionMeta) item.getItemMeta();
    PotionType potionType = potionMeta.getBasePotionType();
    List<Component> customEffects = getCustomEffectList(viewer, item);
    Collection<PotionEffect> effects = new ArrayList<>(potionMeta.getCustomEffects());
    effects.removeIf(effect -> effect.getDuration() == 0);
    if (!effects.isEmpty() || !customEffects.isEmpty())
    {
      for (PotionEffect potionEffect : effects)
      {
        lore.add(getDescription(100d, getComponent(potionEffect.getType()), potionEffect.getDuration(), potionEffect.getAmplifier() + 1));
        lore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(potionEffect, viewer), NamedTextColor.GRAY));
      }
      if (potionType == PotionType.AWKWARD || potionType == PotionType.MUNDANE || potionType == PotionType.THICK
              || potionType == PotionType.WATER)
      {
        lore.addAll(customEffects);
        return lore;
      }
    }
    List<PotionEffect> potionEffects = new ArrayList<>();
    if (potionType != null)
    {
      potionEffects.addAll(potionType.getPotionEffects());
    }
    if (potionEffects.isEmpty())
    {
      lore.add(NONE);
    }
    else
    {
      for (PotionEffect potionEffect : potionEffects)
      {
        lore.add(getDescription(getComponent(potionEffect.getType()), potionEffect.getDuration(), potionEffect.getAmplifier()));
        lore.addAll(ComponentUtil.convertHoverToItemLore(VanillaEffectDescription.getDescription(potionEffect), NamedTextColor.GRAY));
      }
    }
    lore.addAll(customEffects);
    return lore;
  }

  @NotNull
  public static List<Component> getCustomEffectList(@Nullable Player viewer, @NotNull ItemStack item)
  {
    List<Component> list = new ArrayList<>();
    NBTCompoundList potionsTag = NBTAPI.getCompoundList(NBTAPI.getMainCompound(item), CucumberyTag.CUSTOM_EFFECTS);
    if (potionsTag != null)
    {
      for (ReadWriteNBT potionTag : potionsTag)
      {
        try
        {
          String rawKey = potionTag.getString(CucumberyTag.CUSTOM_EFFECTS_ID);
          String[] rawKeySplit = rawKey.split(":");
          CustomEffectType customEffectType = CustomEffectType.getByKey(new NamespacedKey(rawKeySplit[0], rawKeySplit[1]));
          if (customEffectType != null)
          {
            Component effect = ComponentUtil.create(customEffectType).color(null);
            int duration = potionTag.getInteger(CucumberyTag.CUSTOM_EFFECTS_DURATION), amplifier = potionTag.getInteger(CucumberyTag.CUSTOM_EFFECTS_AMPLIFIER);
            list.add(getDescription(effect, duration, amplifier + 1));
            Component description = new CustomEffect(customEffectType, duration, amplifier).getDescription(viewer, true);
            if (!description.equals(Component.empty()) && potionsTag.size() <= 10)
            {
              list.addAll(ComponentUtil.convertHoverToItemLore(description, NamedTextColor.GRAY));
            }
          }
        }
        catch (Exception ignored)
        {

        }
      }
    }
    return list;
  }
}
