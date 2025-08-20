package com.jho5245.cucumbery.util.no_groups;

import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemSerializer
{
  @NotNull
  public static String serialize(@Nullable ItemStack itemStack)
  {
    if (!ItemStackUtil.itemExists(itemStack))
    {
      return "";
    }
    return NBTItem.convertItemtoNBT(itemStack).toString();
  }

  @NotNull
  public static ItemStack deserialize(@Nullable String itemStack)
  {
    try
    {
      if (itemStack == null || itemStack.isEmpty())
      {
        return new ItemStack(Material.AIR);
      }
      return NBTItem.convertNBTtoItem(new NBTContainer(itemStack));
    }
    catch (Exception e)
    {
      MessageUtil.consoleSendMessage("ItemStack Deserialization Error! ItemStack String: %s", String.valueOf(itemStack));
      return new ItemStack(Material.AIR);
    }
  }
}
