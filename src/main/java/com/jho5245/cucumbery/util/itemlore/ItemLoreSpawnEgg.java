package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTType;
import de.tr7zw.changeme.nbtapi.iface.NBTHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemLoreSpawnEgg
{
	protected static void setItemLore(@NotNull ItemStack itemStack, @NotNull ItemMeta itemMeta, @NotNull List<Component> lore, @Nullable Player viewer)
	{
		if (!(itemMeta instanceof SpawnEggMeta spawnEggMeta))
			return;

		EntityType entityType = spawnEggMeta.getCustomSpawnedType();
		if (entityType != null)
		{
			lore.add(Component.empty());
			lore.add(ComponentUtil.translate("&7key:cucumbery.item_lore.spawn_egg.type|생성 개체 유형 : %s", Component.translatable(entityType.translationKey(), Constant.THE_COLOR)));
		}
		NBTItem nbtItem = new NBTItem(itemStack);
		NBTCompound entityTag = nbtItem.getCompound("EntityTag");
		if (entityTag != null && !entityTag.getKeys().isEmpty())
		{
			lore.add(Component.empty());
			if (viewer != null && UserData.SHOW_PLUGIN_DEV_DEBUG_MESSAGE.getBoolean(viewer))
			{
				lore.add(ComponentUtil.translate("&7key:cucumbery.item_lore.spawn_egg.entity_tag|Raw NBT : %s", entityTag.toString()));
			}
/*			lore.add(Component.empty());
			for (String key : entityTag.getKeys())
			{
				NBTType nbtType = entityTag.getType(key);
				Object value = entityTag.getOrNull(key, nbtType.getClass());
				String clazz = value != null ? value.getClass().getSimpleName() : "null";
				lore.add(ComponentUtil.translate("&7%s %s : %s", "&3" + clazz, "&b" + key, (value instanceof String ? "&a" : (value instanceof Number ? "&6" : "")) + value));
			}*/
		}
	}
}
