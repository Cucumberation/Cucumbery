package com.jho5245.cucumbery.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * Called when player gets items directly into their inventory from mob/block drops when player has telekinesis feature(item enchants, potion effects and so on).
 */
public class PlayerTelekinesisItemEvent extends PlayerEvent implements Cancellable
{
	private static final HandlerList handlers = new HandlerList();

	private boolean cancelled;
	private Collection<ItemStack> drops;

	public PlayerTelekinesisItemEvent(Player who, Collection<ItemStack> drops)
	{
		super(who);
		this.drops = new ArrayList<>(drops);
	}

	@Override
	public boolean isCancelled()
	{
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel)
	{
		this.cancelled = cancel;
	}

	/**
	 * Gets an immutable collection of item stack drops involved to this event.
	 * @return an immutable collection of item stack
	 */
	public Collection<ItemStack> getDrops()
	{
		return Collections.unmodifiableCollection(drops);
	}

	public void setDrops(Collection<ItemStack> drops)
	{
		this.drops = new ArrayList<>(drops);
	}

	@Override
	public @NotNull HandlerList getHandlers()
	{
		return handlers;
	}

	@NotNull
	public static HandlerList getHandlerList()
	{
		return handlers;
	}
}
