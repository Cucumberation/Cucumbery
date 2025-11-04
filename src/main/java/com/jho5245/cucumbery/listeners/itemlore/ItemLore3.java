package com.jho5245.cucumbery.listeners.itemlore;

import com.jho5245.cucumbery.custom.custommaterial.CustomMaterialNew;
import com.jho5245.cucumbery.events.itemlore.ItemLore3Event;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ItemLore3 implements Listener
{
	@EventHandler
	public void onitemLore3(ItemLore3Event event)
	{
		ItemStack itemStack = event.getItemStack();
		List<Component> lore = event.getItemLore();
		CustomMaterialNew customMaterial = CustomMaterialNew.itemStackOf(itemStack);
		if (customMaterial == CustomMaterialNew.TEST_ITEM)
		{
			lore.add(ComponentUtil.translate("&7테스트 아이템 설명"));
		}
		event.setItemLore(lore);
	}
}
