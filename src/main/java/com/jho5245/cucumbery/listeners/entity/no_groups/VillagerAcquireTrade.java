package com.jho5245.cucumbery.listeners.entity.no_groups;

import com.jho5245.cucumbery.util.itemlore.ItemLore;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class VillagerAcquireTrade implements Listener
{
  @EventHandler
  public void onVillagerAcquireTrade(VillagerAcquireTradeEvent event)
  {
    if (event.isCancelled())
    {
      return;
    }
  }
}
