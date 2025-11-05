package com.jho5245.cucumbery.events.addon.protocollib;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityMetaDataEvent extends PlayerEvent implements Cancellable
{
	boolean cancel;
	private static final HandlerList handlers = new HandlerList();
	private ItemStack itemStack;

	private final Entity entity;

	public EntityMetaDataEvent(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull Entity entity)
	{
		super(player);
		this.itemStack = itemStack.clone();
		this.entity = entity;
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

	/**
	 * Returns a copy of ItemStack involved to this event.
	 * @return a copy of ItemStack involved to this event.
	 */
	@NotNull
	public ItemStack getItemStack()
	{
		return this.itemStack.clone();
	}

	/**
	 * Sets an ItemStack involved to this event.
	 * @param itemStack ItemStack to set
	 */
	public void setItemStack(@NotNull ItemStack itemStack)
	{
		this.itemStack = itemStack.clone();
	}

	public Entity getEntity()
	{
		return entity;
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
