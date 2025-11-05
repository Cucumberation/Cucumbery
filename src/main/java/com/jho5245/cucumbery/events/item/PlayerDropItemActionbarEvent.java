package com.jho5245.cucumbery.events.item;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PlayerDropItemActionbarEvent extends PlayerEvent implements Cancellable
{
	private boolean cancel;
	private ItemStack itemStack;

	public PlayerDropItemActionbarEvent(@NotNull Player player, @NotNull ItemStack itemStack)
	{
		super(player);
		this.itemStack = itemStack.clone();
	}

	@NotNull
	public ItemStack getItemStack()
	{
		return itemStack.clone();
	}

	public void setItemStack(@NotNull ItemStack itemStack)
	{
		this.itemStack = itemStack.clone();
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

	private static final HandlerList handlers = new HandlerList();

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
