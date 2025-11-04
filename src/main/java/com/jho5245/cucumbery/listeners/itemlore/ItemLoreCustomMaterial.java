package com.jho5245.cucumbery.listeners.itemlore;

import com.jho5245.cucumbery.custom.custommaterial.CustomMaterialNew;
import com.jho5245.cucumbery.events.itemlore.ItemLoreCustomMaterialEvent;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemLoreCustomMaterial implements Listener
{
	@EventHandler
	public void onItemLoreCustomMaterial(ItemLoreCustomMaterialEvent event)
	{
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItemStack();
		ItemMeta itemMeta = itemStack.getItemMeta();
		List<Component> lore = new ArrayList<>();
		CustomMaterialNew customMaterial = CustomMaterialNew.itemStackOf(itemStack);
		if (customMaterial == CustomMaterialNew.TEST_ITEM)
		{
			lore.add(ComponentUtil.translate("&7테스트 아이템"));
		}

		// set lore
		{
			if (!lore.isEmpty())
			{
				lore.addFirst(Component.empty());
				List<Component> newLore = itemMeta.lore();
				if (newLore == null)
				{
					itemMeta.lore(lore);
				}
				else
				{
					newLore.addAll(lore);
					itemMeta.lore(newLore);
				}
			}
		}
		event.setItemStack(itemStack);
	}
}
