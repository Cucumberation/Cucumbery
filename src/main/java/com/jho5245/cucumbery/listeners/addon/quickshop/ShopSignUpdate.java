package com.jho5245.cucumbery.listeners.addon.quickshop;

import com.ghostchu.quickshop.api.event.general.ShopSignUpdateEvent;
import com.ghostchu.quickshop.api.shop.Shop;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Sign;
import org.bukkit.block.sign.Side;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopSignUpdate implements Listener
{
	@EventHandler
	public void onShopSignUpdate(ShopSignUpdateEvent event)
	{
		Shop shop = event.getShop();
		Sign sign = event.getSign();
		sign.getSide(Side.FRONT).line(2, ItemNameUtil.itemName(shop.getItem(), NamedTextColor.BLACK));
		sign.update();
	}
}
