package com.jho5245.cucumbery.listeners.itemlore;

import com.jho5245.cucumbery.events.itemlore.ItemLoreCustomMaterialEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("unused")
public class ItemLoreCustomMaterial implements Listener
{
	public void onItemLoreCustomMaterial(ItemLoreCustomMaterialEvent event)
	{
		ItemStack itemStack = event.getItemStack();
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemStack.setItemMeta(itemMeta);
		event.setItemStack(itemStack);
	}
}
