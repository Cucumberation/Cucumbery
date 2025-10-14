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

public class OpenWindowMerchantEvent extends PlayerEvent implements Cancellable
{
	boolean cancel;
	private static final HandlerList handlers = new HandlerList();
	private ItemStack result;
	private List<ItemStack> ingredients;

	public OpenWindowMerchantEvent(@NotNull Player player, @NotNull ItemStack result, @NotNull List<ItemStack> ingredients)
	{
		super(player);
		this.result = result.clone();
		this.ingredients = new ArrayList<>(ingredients);
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
	public ItemStack getResult()
	{
		return this.result.clone();
	}

	/**
	 * Sets an ItemStack involved to this event.
	 * @param result ItemStack to set
	 */
	public void setResult(@NotNull ItemStack result)
	{
		this.result = result.clone();
	}

	/**
	 * Returns an immutable list of itemstacks involved to this event.
	 * @return an immutable list of itemstacks involved to this event.
	 */
	@NotNull
	public List<ItemStack> getIngredients()
	{
		return Collections.unmodifiableList(ingredients);
	}

	/**
	 * Sets list of itemstacks involved to this event.
	 * @param ingredients ItemStacks to set
	 */
	public void setIngredients(@NotNull List<ItemStack> ingredients)
	{
		this.ingredients = new ArrayList<>(ingredients);
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
