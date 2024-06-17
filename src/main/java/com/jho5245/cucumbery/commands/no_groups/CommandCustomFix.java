package com.jho5245.cucumbery.commands.no_groups;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.CommandTabUtil;
import com.jho5245.cucumbery.util.no_groups.CucumberyCommandExecutor;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CommandCustomFix implements CucumberyCommandExecutor
{
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		if (!Method.hasPermission(sender, Permission.CMD_CUSTOM_FIX, true))
		{
			return true;
		}
		if (!MessageUtil.checkQuoteIsValidInArgs(sender, MessageUtil.wrapWithQuote(args)))
		{
			return !(sender instanceof BlockCommandSender);
		}
		if (!(sender instanceof Player player))
		{
			MessageUtil.sendError(sender, Prefix.ONLY_PLAYER);
			return true;
		}
		ItemStack itemStack = player.getInventory().getItemInMainHand().clone();
		if (!ItemStackUtil.itemExists(itemStack))
		{
			MessageUtil.sendError(player, Prefix.NO_HOLDING_ITEM);
			return true;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (!(itemMeta instanceof Damageable damageable))
		{
			MessageUtil.sendError(player, "수리할 수 없는 아이템입니다");
			return true;
		}
		if (damageable.getDamage() <= 0)
		{
			MessageUtil.sendError(player, "내구도가 가득 찬 아이템은 수리할 수 없습니다");
			return true;
		}
		double cost = Cucumbery.config.getDouble("fix-command-cost");
		boolean useCost = Cucumbery.using_Vault_Economy && cost > 0;
		String fixMessage = "주로 사용하는 손에 들고 있는 아이템을 수리했습니다";
		if (player.getGameMode() != GameMode.CREATIVE && useCost)
		{
			double playerMoney = Cucumbery.eco.getBalance(player);
			if (cost > playerMoney)
			{
				MessageUtil.sendError(player,
						"수리하는데 필요한 비용이 부족합니다. 소지 금액 : rg255,204;" + Constant.Sosu2.format(playerMoney) + "원&r, 수리 비용 : rg255,204;" + Constant.Sosu2.format(cost) + "원");
				return true;
			}
			else
			{
				Cucumbery.eco.withdrawPlayer(player, cost);
			}
			fixMessage = Constant.THE_COLOR_HEX + Constant.Sosu2.format(cost) + "원&r을 지불하고 " + fixMessage + " 소지 금액 : rg255,204;" + Constant.Sosu2.format(
					Cucumbery.eco.getBalance(player)) + "원";
		}
		MessageUtil.sendMessage(player, Prefix.INFO_CUSTOM_FIX, fixMessage);
		damageable.setDamage(0);
		itemStack.setItemMeta(damageable);
		player.getInventory().setItemInMainHand(itemStack);
		ItemStackUtil.updateInventory(player);
		return true;
	}

	@Override
	public @NotNull List<Completion> completion(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args,
			@NotNull Location location)
	{

		int length = args.length;
		if (length == 1)
		{
			return CommandTabUtil.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
		}
		return CommandTabUtil.ARGS_LONG;
	}
}
