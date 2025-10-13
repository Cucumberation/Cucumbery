package com.jho5245.cucumbery.listeners.addon.deluxeacutions;

import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import me.sedattr.auctionsapi.events.AuctionCreateEvent;
import me.sedattr.deluxeauctions.managers.Auction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class AuctionCreate implements Listener
{
	@EventHandler
	public void onAuctionCreate(AuctionCreateEvent event)
	{
		if (event.isCancelled())
			return;
		Player player = event.getPlayer();
		Auction auction = event.getAuction();
		ItemStack itemStack = auction.getAuctionItem();
		MessageUtil.broadcastDebug("player:", player, "item:", itemStack);
	}
}
