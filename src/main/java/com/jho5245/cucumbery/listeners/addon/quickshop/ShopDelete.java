package com.jho5245.cucumbery.listeners.addon.quickshop;

import com.ghostchu.quickshop.api.event.management.ShopDeleteEvent;
import com.jho5245.cucumbery.util.storage.data.Variable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopDelete implements Listener
{
	@EventHandler
	public void onShopDelete(ShopDeleteEvent event)
	{
		event.shop().ifPresent(shop -> Variable.shops.remove(shop));
	}
}
