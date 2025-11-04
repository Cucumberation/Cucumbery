package com.jho5245.cucumbery.util.itemlore;

import com.jho5245.cucumbery.custom.custommaterial.CustomMaterialNew;
import com.jho5245.cucumbery.events.itemlore.ItemLoreCustomMaterialEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemLoreCustomMaterial
{
	protected static void itemLore(@Nullable Player player, @NotNull ItemStack itemStack, @NotNull CustomMaterialNew customMaterial)
	{
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.itemName(customMaterial.getDisplayName());
		Material displayMaterial = customMaterial.getDisplayMaterial();
		if (displayMaterial != null)
		{
			itemMeta.setItemModel(displayMaterial.getKey());
		}
		itemStack.setItemMeta(itemMeta);
		ItemLoreCustomMaterialEvent event = new ItemLoreCustomMaterialEvent(player, itemStack);

	}
}
