package com.jho5245.cucumbery.commands.no_groups;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.jho5245.cucumbery.util.additemmanager.AddItemUtil;
import com.jho5245.cucumbery.util.no_groups.*;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommandCgive implements CucumberyCommandExecutor
{
	private final List<Completion> completions;

	public CommandCgive()
	{
		completions = new ArrayList<>();
		ConfigurationSection root = Variable.customItemsConfig.getConfigurationSection("");
		if (root != null)
		{
			for (String key : root.getKeys(false))
			{
				String displayName = root.getString(key + ".display-name");
				if (displayName != null)
				{
					completions.add(Completion.completion(key, ComponentUtil.create(MessageUtil.n2s(displayName))));
				}
			}
		}
		CustomMaterial[] customMaterials = CustomMaterial.values();
		for (CustomMaterial customMaterial : customMaterials)
		{
			completions.add(Completion.completion(customMaterial.toString().toLowerCase(), customMaterial.getDisplayName()));
		}
	}

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String @NotNull [] args)
	{
		if (!Method.hasPermission(sender, Permission.CMD_GIVE, true))
		{
			return true;
		}
		if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
		{
			return !(sender instanceof BlockCommandSender);
		}
		String usage = cmd.getUsage().replace("/<command> ", "");
		if (args.length < 2)
		{
			MessageUtil.shortArg(sender, 2, args);
			MessageUtil.commandInfo(sender, label, usage);
			return true;
		}
		else if (args.length <= 4)
		{
			List<Player> players = SelectorUtil.getPlayers(sender, args[0]);
			if (players == null)
				return true;
			ItemStack itemStack = convert(args[1]);
			if (itemStack == null)
			{
				MessageUtil.sendError(sender, Prefix.ARGS_WRONG, "(%s)".formatted(args[1]));
				return true;
			}
			int amount = 1;
			boolean hideOutput = false;
			if (args.length >= 3)
			{
				if (!MessageUtil.isInteger(sender, args[2], true))
				{
					return true;
				}
				int input = Integer.parseInt(args[2]);
				if (!MessageUtil.checkNumberSize(sender, input, 1, 2304))
				{
					return true;
				}
				amount = input;
			}
			if (args.length == 4)
			{
				if (!args[3].equals("true") && !args[3].equals("false"))
				{
					MessageUtil.wrongBool(sender, 3, args);
					return true;
				}
				if (args[3].equals("true"))
				{
					hideOutput = true;
				}
			}
			give(sender, players, itemStack, amount, hideOutput);
		}
		else
		{
			MessageUtil.longArg(sender, 4, args);
			MessageUtil.commandInfo(sender, label, usage);
		}
		return true;
	}

	@Override
	public @NotNull List<Completion> completion(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args,
			@NotNull Location location)
	{
		int length = args.length;
		switch (length)
		{
			case 1 ->
			{
				return CommandTabUtil.tabCompleterPlayer(sender, args, "<플레이어>");
			}
			case 2 ->
			{
				return CommandTabUtil.tabCompleterList(args, completions, "<아이템>");
			}
			case 3 ->
			{
				return CommandTabUtil.tabCompleterIntegerRadius(args, 1, 2304, "[개수]");
			}
			case 4 ->
			{
				return CommandTabUtil.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
			}
		}
		return CommandTabUtil.ARGS_LONG;
	}

	private ItemStack convert(String customType) throws NullPointerException
	{
		try
		{
			CustomMaterial customMaterial = CustomMaterial.valueOf(customType.toUpperCase());
			ItemStack itemStack = new ItemStack(customMaterial.getDisplayMaterial());
			NBT.modify(itemStack, nbt ->
			{
				nbt.setString(CustomMaterial.IDENDTIFER, customMaterial.toString().toLowerCase());
			});
			return itemStack;
		}
		catch (IllegalArgumentException e)
		{
			if (Variable.customItemsConfig.getConfigurationSection(customType) == null)
			{
				return null;
			}
			ItemStack itemStack = new ItemStack(Material.STONE);
			NBT.modify(itemStack, nbt ->
			{
				nbt.setString(CustomMaterial.IDENDTIFER, customType);
			});
			return itemStack;
		}
	}

	private void give(@NotNull CommandSender sender, @NotNull Collection<Player> players, @NotNull ItemStack itemStack, int amount, boolean hideOutput)
	{
		if (players.isEmpty())
		{
			throw new IllegalArgumentException("player list empty");
		}
		itemStack.setAmount(amount);
		AddItemUtil.addItemResult2(sender, players, itemStack, amount).stash().sendFeedback(hideOutput);
	}
}
