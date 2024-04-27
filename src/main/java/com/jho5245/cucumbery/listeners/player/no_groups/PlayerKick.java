package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerKickEvent.Cause;

public class PlayerKick implements Listener
{
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (event.isCancelled())
		{
			return;
		}
		Player player = event.getPlayer();
		Cause cause = event.getCause();
		if (player.hasPermission("asdf"))
		{
			event.setCancelled(true);
			MessageUtil.sendWarn(player, "관리자여서 서버에서 강퇴당하지 않았습니다. (원인 : %s, 사유 : %s, 강퇴 메시지: %s)", cause, event.reason(), event.leaveMessage());
		}
		switch (cause)
		{
			case INVALID_PAYLOAD, CHAT_VALIDATION_FAILED, EXPIRED_PROFILE_PUBLIC_KEY, INVALID_PUBLIC_KEY_SIGNATURE, OUT_OF_ORDER_CHAT ->
			{
				MessageUtil.broadcastDebug(player, "이/가 강퇴 이벤트 cancel됨 (원인 : %s, 사유 : %s, 강퇴 메시지 : %s)", cause, event.reason(), event.leaveMessage());
				event.setCancelled(true);
			}
		}
	}
}
