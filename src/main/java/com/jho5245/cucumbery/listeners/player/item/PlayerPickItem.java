package com.jho5245.cucumbery.listeners.player.item;

import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import io.papermc.paper.event.player.PlayerPickItemEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerPickItem implements Listener
{
	@EventHandler
	public void onPlayerPickItem(PlayerPickItemEvent event)
	{
		Player player = event.getPlayer();
		int sourceSlot = event.getSourceSlot(), targetSlot = event.getTargetSlot();
		// MessageUtil.broadcastDebug(player, sourceSlot + ", " + targetSlot);
	}
}
