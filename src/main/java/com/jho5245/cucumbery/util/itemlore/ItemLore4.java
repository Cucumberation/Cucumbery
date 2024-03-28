package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.no_groups.ItemCategory;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemLore4
{
  public static void setItemLore(@NotNull ItemStack itemStack)
  {
    NBTItem nbtItem = new NBTItem(itemStack);
    NBTCompound nbtCompound = nbtItem.getCompound(CucumberyTag.KEY_TMI);
    if (nbtCompound == null)
    {
      nbtCompound = nbtItem.addCompound(CucumberyTag.KEY_TMI);
    }
    nbtCompound.removeKey(CucumberyTag.TMI_VANILLA_TAGS);
    NBTCompound customTags = nbtCompound.getCompound(CucumberyTag.TMI_CUSTOM_TAGS);
    CustomMaterial customMaterial = CustomMaterial.itemStackOf(itemStack);
    if (customMaterial != null)
    {
      customTags = nbtCompound.addCompound(CucumberyTag.TMI_CUSTOM_TAGS);
      switch (customMaterial)
      {
        case AMBER, JADE, MORGANITE, SAPPHIRE, TOPAZ -> customTags.setBoolean("gemstones", true);
      }
      if (customTags.getKeys().isEmpty())
      {
        nbtCompound.removeKey(CucumberyTag.TMI_CUSTOM_TAGS);
      }
      itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
      return;
    }
    if (customTags != null || nbtItem.hasTag(CustomMaterial.IDENDIFER) && !"".equals(nbtItem.getString(CustomMaterial.IDENDIFER)))
    {
      itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
      return;
    }
    NBTCompound vanillaTags = nbtCompound.addCompound(CucumberyTag.TMI_VANILLA_TAGS);
    Material type = itemStack.getType();
    String customType = nbtItem.getString(CustomMaterial.IDENDIFER);
    try
    {
      type = Material.valueOf(customType.toUpperCase());
    }
    catch (Exception ignored)
    {

    }
    List<Tag<Material>> tags = new ArrayList<>();
    if (type.isBlock())
    {
      Bukkit.getTags(Tag.REGISTRY_BLOCKS, Material.class).forEach(tags::add);
    }
    Bukkit.getTags(Tag.REGISTRY_ITEMS, Material.class).forEach(tags::add);
    Material finalType = type;
    tags.forEach(t ->
    {
      if (t.getValues().contains(finalType))
      {
        vanillaTags.setBoolean(t.getKey().toString(), true);
      }
    });
    vanillaTags.setBoolean("material_" + type.toString().toLowerCase(), true);
    if (vanillaTags.getKeys().isEmpty())
    {
      nbtCompound.removeKey(CucumberyTag.TMI_VANILLA_TAGS);
    }
    itemStack.setItemMeta(nbtItem.getItem().getItemMeta());
  }
}
