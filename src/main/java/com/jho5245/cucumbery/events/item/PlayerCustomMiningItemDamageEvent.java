package com.jho5245.cucumbery.events.item;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerCustomMiningItemDamageEvent extends PlayerEvent implements Cancellable
{
	boolean cancel;
	private static final HandlerList handlers = new HandlerList();

	public PlayerCustomMiningItemDamageEvent(@NotNull Player player)
	{
		super(player);
	}

	@Override
	public boolean isCancelled()
	{
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancel = cancel;
	}

	@Override
	public @NotNull HandlerList getHandlers()
	{
		return handlers;
	}

	@NotNull
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
