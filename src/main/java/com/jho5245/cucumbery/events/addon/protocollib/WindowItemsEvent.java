package com.jho5245.cucumbery.events.addon.protocollib;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WindowItemsEvent extends PlayerEvent implements Cancellable
{
	boolean cancel;
	private static final HandlerList handlers = new HandlerList();
	private ItemStack itemStack;
	private List<ItemStack> itemStacks;

	public WindowItemsEvent(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull List<ItemStack> itemStacks)
	{
		super(player);
		this.itemStack = itemStack.clone();
		this.itemStacks = new ArrayList<>(itemStacks);
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

	/**
	 * Returns an immutable list of itemstacks involved to this event.
	 * @return an immutable list of itemstacks involved to this event.
	 */
	@NotNull
	public List<ItemStack> getItemStacks()
	{
		return Collections.unmodifiableList(itemStacks);
	}

	/**
	 * Sets list of itemstacks involved to this event.
	 * @param itemStacks ItemStacks to set
	 */
	public void setItemStacks(@NotNull List<ItemStack> itemStacks)
	{
		this.itemStacks = new ArrayList<>(itemStacks);
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
