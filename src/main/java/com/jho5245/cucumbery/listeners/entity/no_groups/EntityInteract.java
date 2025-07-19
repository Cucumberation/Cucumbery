package com.jho5245.cucumbery.listeners.entity.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.Method;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityInteractEvent;

public class EntityInteract implements Listener
{
	@EventHandler
	public void onEntityInteract(EntityInteractEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Block block = event.getBlock();

		if (Cucumbery.config.getBoolean("block-entity-trample-soil"))
		{
			blockEntityTrampleSoil(event, block);
		}
	}

	/** 플레이어가 아닌 개체가 경작지 파괴하는 행위 방지
	 */
	private void blockEntityTrampleSoil(EntityInteractEvent event, Block block)
	{
		if (block.getType() != Material.FARMLAND)
			return;

		if (!Method.configContainsLocation(block.getLocation(), Cucumbery.config.getStringList("no-block-entity-trample-soil-worlds")))
		{
			event.setCancelled(true);
		}
	}
}
