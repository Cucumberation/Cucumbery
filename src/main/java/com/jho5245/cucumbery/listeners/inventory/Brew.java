package com.jho5245.cucumbery.listeners.inventory;

import com.gmail.nossr50.api.ExperienceAPI;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.itemlore.ItemLore;
import com.jho5245.cucumbery.util.itemlore.ItemLoreView;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Brew implements Listener
{
  @EventHandler
  public void onBrew(BrewEvent event)
  {
    if (event.isCancelled())
    {
      return;
    }
  }
}
