package com.jho5245.cucumbery.events.itemlore;

import com.jho5245.cucumbery.custom.custommaterial.CustomMaterialNew;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when {@link CustomMaterialNew} item lore is updated. Note that you should listen {@link ItemLore3Event} if you want to add lore to the itemstack.
 * <p>This event is designed only for other item meta except lore.</p>
 */
public class ItemLoreCustomMaterialEvent extends Event
{
	private static final HandlerList handlers = new HandlerList();

	@Nullable
	private final Player player;
	private ItemStack itemStack;

	public ItemLoreCustomMaterialEvent(@Nullable Player player,ItemStack itemStack)
	{
		this.player = player;
		this.itemStack = itemStack.clone();
	}

	@Nullable
	public Player getPlayer()
	{
		return this.player;
	}

	public ItemStack getItemStack()
	{
		return this.itemStack.clone();
	}

	public void setItemStack(ItemStack itemStack)
	{
		this.itemStack = itemStack.clone();
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
