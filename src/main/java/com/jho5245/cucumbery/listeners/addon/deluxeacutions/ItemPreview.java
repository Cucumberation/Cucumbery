package com.jho5245.cucumbery.listeners.addon.deluxeacutions;

import com.jho5245.cucumbery.util.nbt.NBTAPI;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.data.Constant.RestrictionType;
import me.sedattr.auctionsapi.events.ItemPreviewEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ItemPreview implements Listener
{
	@EventHandler
	public void onItemPreview(ItemPreviewEvent event)
	{
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItem();

		if (NBTAPI.isRestricted(player, itemStack, RestrictionType.NO_TRADE))
		{
			MessageUtil.sendWarn(player, "key:message.auction.no_trade_item|%s은(는) 거래할 수 없는 아이템입니다.", itemStack);
			event.setCancelled(true);
			return;
		}
	}
}
