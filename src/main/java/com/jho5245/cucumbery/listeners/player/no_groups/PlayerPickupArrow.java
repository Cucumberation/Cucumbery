package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class PlayerPickupArrow implements Listener
{
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerPickupArrow(PlayerPickupArrowEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Player player = event.getPlayer();
		if (UserData.SPECTATOR_MODE.getBoolean(player))
		{
			event.setCancelled(true);
			return;
		}
		String pickUpMode = UserData.ITEM_PICKUP_MODE.getString(player);
		switch (pickUpMode)
		{
			case "sneak" ->
			{
				if (!player.isSneaking())
				{
					event.setCancelled(true);
					return;
				}
			}
			case "disabled" ->
			{
				event.setCancelled(true);
				return;
			}
		}
		AbstractArrow abstractArrow = event.getArrow();
		Variable.entityShootBowConsumableMap.remove(abstractArrow.getUniqueId());
	}
}
