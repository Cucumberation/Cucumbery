package com.jho5245.cucumbery.events.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class CustomBlockBreakEvent extends BlockEvent
{
  private static final HandlerList handlers = new HandlerList();

  private final Player player;

	private boolean applyPhysics;

  public CustomBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player, boolean applyPhysics)
  {
    super(theBlock);
    this.player = player;
		this.applyPhysics = applyPhysics;
  }

  public Player getPlayer()
  {
    return player;
  }

	public boolean isApplyPhysics()
	{
		return applyPhysics;
	}

	public void setApplyPhysics(boolean value)
	{
		this.applyPhysics = value;
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
