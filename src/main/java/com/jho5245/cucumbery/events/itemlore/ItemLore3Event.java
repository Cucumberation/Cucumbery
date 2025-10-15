package com.jho5245.cucumbery.events.itemlore;

import net.kyori.adventure.text.Component;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemLore3Event extends Event
{
	private static final HandlerList handlers = new HandlerList();

	private ItemStack itemStack;
	private List<Component> itemLore;

	public ItemLore3Event(ItemStack itemStack, List<Component> itemLore)
	{
		this.itemStack = itemStack.clone();
		this.itemLore = new ArrayList<>(itemLore);
	}

	public ItemStack getItemStack()
	{
		return this.itemStack.clone();
	}

	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack.clone();
	}

	public List<Component> getItemLore()
	{
		return new ArrayList<>(itemLore);
	}

	public void setItemLore(List<Component> list)
	{
		this.itemLore = new ArrayList<>(list);
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
