package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PlayerPortal implements Listener
{
	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}

		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE && UserData.DISALBE_PORTAL_USAGE_ON_CREATIVE.getBoolean(player))
		{
			event.setCancelled(true);
			MessageUtil.sendWarn(player, "포탈 사용이 비활성화되어있는 상태입니다.");
		}
	}
}
