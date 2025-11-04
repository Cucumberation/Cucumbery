package com.jho5245.cucumbery.listeners.addon.quickshop;

import com.ghostchu.quickshop.api.event.details.ShopItemChangeEvent;
import com.jho5245.cucumbery.util.storage.data.Variable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ShopItemChange implements Listener
{
  @EventHandler
  public void onShopItemChange(ShopItemChangeEvent event)
  {
    Variable.shops.remove(event.getShop());
    Variable.shops.add(event.getShop());
  }
}
