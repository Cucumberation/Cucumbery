package com.jho5245.cucumbery.commands.no_groups;

import com.jho5245.cucumbery.util.itemlore.ItemLoreUtil;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.NBTList;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandSetItemMeta implements CommandExecutor, TabCompleter
{
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		if (!Method.hasPermission(sender, Permission.CMD_SETDATA, true))
		{
			return true;
		}
		if (!(sender instanceof Player player))
		{
			MessageUtil.sendError(sender, Prefix.ONLY_PLAYER);
			return true;
		}
		PlayerInventory inventory = player.getInventory();
		ItemStack itemStack = inventory.getItemInMainHand();
		if (!ItemStackUtil.itemExists(itemStack))
		{
			MessageUtil.sendError(sender, Prefix.NO_HOLDING_ITEM);
			return true;
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		String usage = cmd.getUsage().replace("/<command> ", "");
		if (cmd.getName().equals("setname"))
		{
			String name = MessageUtil.n2s(MessageUtil.listToString(" ", args), MessageUtil.N2SType.SPECIAL_ONLY);
			boolean silent = name.contains("--silent");
			if (silent)
			{
				name = name.replaceFirst("--silent", "");
			}
			if (args.length == 0)
			{
				MessageUtil.shortArg(sender, 1, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			else
			{
				if (args.length == 1 && args[0].equals("--remove"))
				{
					itemMeta.displayName(null);
					itemStack.setItemMeta(itemMeta);
					inventory.setItemInMainHand(itemStack);
					ItemStackUtil.updateInventory(player);
					if (!silent)
					{
						MessageUtil.sendMessage(player, Prefix.INFO_SETDATA, "주로 사용하는 손에 들고 있는 아이템의 이름을 제거했습니다 (%s)", itemStack);
					}
				}
				else
				{
					itemMeta.displayName(ComponentUtil.create(name));
					itemStack.setItemMeta(itemMeta);
					inventory.setItemInMainHand(itemStack);
					ItemStackUtil.updateInventory(player);
					if (!silent)
					{
						MessageUtil.sendMessage(player, Prefix.INFO_SETDATA, "주로 사용하는 손에 들고 있는 아이템의 이름을 %s(으)로 설정했습니다 (%s)", ItemNameUtil.itemName(itemStack), itemStack);
					}
				}
			}
		}
		else if (cmd.getName().equals("setname2"))
		{
			if (args.length == 0)
			{
				MessageUtil.shortArg(sender, 1, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			else
			{
				if (args.length == 1 && args[0].startsWith("--") && (MessageUtil.isInteger(sender, args[0].substring(2), false) || args[0].equals("--")))
				{
					int subString;
					if (args[0].equals("--"))
					{
						subString = 1;
					}
					else
					{
						subString = Integer.parseInt(args[0].substring(2));
					}
					if (!MessageUtil.checkNumberSize(sender, subString, 1, Integer.MAX_VALUE))
					{
						return true;
					}
					String displayName = MessageUtil.stripColor(ComponentUtil.serialize(ItemNameUtil.itemName(itemStack)));
					if (subString >= displayName.length())
					{
						itemMeta.displayName(null);
						itemStack.setItemMeta(itemMeta);
						inventory.setItemInMainHand(itemStack);
						MessageUtil.sendMessage(player, Prefix.INFO_SETDATA, "주로 사용하는 손에 들고 있는 아이템의 이름을 제거했습니다 (%s)", itemStack);
					}
					else
					{
						displayName = displayName.substring(0, displayName.length() - subString);
						itemMeta.displayName(ComponentUtil.create(displayName));
						itemStack.setItemMeta(itemMeta);
						inventory.setItemInMainHand(itemStack);
						MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,"주로 사용하는 손에 들고 있는 아이템의 이름을 %s글자 제거했습니다 (%s)", Constant.THE_COLOR_HEX + subString, itemStack);
					}
				}
				else
				{
					String name = MessageUtil.n2s(MessageUtil.listToString(" ", args), MessageUtil.N2SType.SPECIAL_ONLY);
					Component input = ComponentUtil.create(name);
					Component origin = itemMeta.displayName();
					input = origin != null ? origin.append(input) : input;
					itemMeta.displayName(input);
					itemStack.setItemMeta(itemMeta);
					inventory.setItemInMainHand(itemStack);
					// 메시지에만 색깔 추가
					input = ComponentUtil.create(name);
					if (input.color() == null)
					{
						input = input.color(Constant.THE_COLOR);
					}
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA, ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템에 %s(이)라는 이름을 추가로 입력했습니다", input));
				}
			}
		}
		else if (cmd.getName().equals("setlore"))
		{
			if (args.length < 2)
			{
				MessageUtil.shortArg(sender, 2, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			else
			{
				if (!MessageUtil.isInteger(sender, args[0], true))
				{
					return true;
				}
				int index = Integer.parseInt(args[0]);
				if (!MessageUtil.checkNumberSize(player, index, 1, Integer.MAX_VALUE))
				{
					return true;
				}
				List<Component> lore = itemMeta.lore();
				if (lore == null)
				{
					lore = new ArrayList<>();
					for (int i = 0; i < index; i++)
					{
						lore.add(Component.empty());
					}
				}
				else if (lore.size() < index)
				{
					for (int i = lore.size(); i < index; i++)
					{
						lore.add(Component.empty());
					}
				}
				if (args.length == 2 && args[1].equals("--empty"))
				{
					lore.set(index - 1, Component.empty());
					itemMeta.lore(lore);
					itemStack.setItemMeta(itemMeta);
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명을 공백으로 설정했습니다", Constant.THE_COLOR_HEX + index).hoverEvent(itemStack.asHoverEvent()));
				}
				else
				{
					String input = MessageUtil.n2s(MessageUtil.listToString(" ", 1, args.length, args), MessageUtil.N2SType.SPECIAL_ONLY);
					Component inputComponent = ComponentUtil.create(input);
					lore.set(index - 1, inputComponent);
					itemMeta.lore(lore);
					itemStack.setItemMeta(itemMeta);
					inventory.setItemInMainHand(itemStack);
					// 메시지에만 색깔 추가
					if (inputComponent.color() == null)
					{
						inputComponent = inputComponent.color(Constant.THE_COLOR);
					}
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명을 %s(으)로 설정했습니다", index, inputComponent).hoverEvent(itemStack.asHoverEvent()));
				}
			}
		}
		else if (cmd.getName().equals("setlore2"))
		{
			if (args.length < 2)
			{
				MessageUtil.shortArg(sender, 2, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			else
			{
				if (!MessageUtil.isInteger(sender, args[0], true))
				{
					return true;
				}
				int index = Integer.parseInt(args[0]);
				if (!MessageUtil.checkNumberSize(player, index, 1, Integer.MAX_VALUE))
				{
					return true;
				}
				List<Component> lores = itemMeta.lore();
				if (lores == null)
				{
					lores = new ArrayList<>();
					for (int i = 0; i < index; i++)
					{
						lores.add(Component.empty());
					}
				}
				else if (lores.size() < index)
				{
					for (int i = lores.size(); i < index; i++)
					{
						lores.add(Component.empty());
					}
				}
				if (args.length == 2 && args[1].startsWith("--") && (MessageUtil.isInteger(sender, args[1].substring(2), false) || args[1].equals("--")))
				{
					int subString;
					if (args[1].equals("--"))
					{
						subString = 1;
					}
					else
					{
						subString = Integer.parseInt(args[1].substring(2));
					}
					if (!MessageUtil.checkNumberSize(sender, subString, 1, Integer.MAX_VALUE))
					{
						return true;
					}
					Component lore = lores.get(index - 1);
					String loreSerial = ComponentUtil.serialize(lore);
					if (subString >= loreSerial.length())
					{
						lores.remove(index - 1);
						itemMeta.lore(lores);
						itemStack.setItemMeta(itemMeta);
						inventory.setItemInMainHand(itemStack);
						MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
								ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명을 제거했습니다", Constant.THE_COLOR_HEX + index).hoverEvent(itemStack.asHoverEvent()));
					}
					else
					{
						loreSerial = loreSerial.substring(0, loreSerial.length() - subString);
						lores.set(index - 1, ComponentUtil.create(loreSerial));
						itemMeta.lore(lores);
						itemStack.setItemMeta(itemMeta);
						if (ItemLoreUtil.isCucumberyTMIFood(itemStack))
						{
							ItemLoreUtil.removeCucumberyTMIFood(itemStack);
						}
						inventory.setItemInMainHand(itemStack);
						MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
								ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명을 %s글자 제거했습니다", Constant.THE_COLOR_HEX + index, Constant.THE_COLOR_HEX + subString)
										.hoverEvent(itemStack.asHoverEvent()));
					}
				}
				else
				{
					String input = MessageUtil.n2s(MessageUtil.listToString(" ", 1, args.length, args), MessageUtil.N2SType.SPECIAL_ONLY);
					Component inputComponent = ComponentUtil.create(input);
					Component origin = lores.get(index - 1);
					inputComponent = origin != null ? origin.append(inputComponent) : inputComponent;
					lores.set(index - 1, inputComponent);
					itemMeta.lore(lores);
					itemStack.setItemMeta(itemMeta);
					if (ItemLoreUtil.isCucumberyTMIFood(itemStack))
					{
						ItemLoreUtil.removeCucumberyTMIFood(itemStack);
					}
					inventory.setItemInMainHand(itemStack);
					// 피드백에만 색깔
					inputComponent = ComponentUtil.create(input);
					if (inputComponent.color() == null)
					{
						inputComponent = inputComponent.color(Constant.THE_COLOR);
					}
					MessageUtil.sendMessage(player,
							ComponentUtil.translate(Prefix.INFO_SETDATA + "주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명에 %s(이)라는 설명을 추가로 입력했습니다", Constant.THE_COLOR_HEX + index,
									inputComponent).hoverEvent(itemStack.asHoverEvent()));
				}
			}
		}
		else if (cmd.getName().equals("addlore"))
		{
			if (args.length == 0)
			{
				MessageUtil.shortArg(sender, 1, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			else
			{
				List<Component> lore = itemMeta.lore();
				if (lore == null)
				{
					lore = new ArrayList<>();
				}
				if (args.length == 1 && args[0].equals("--empty"))
				{
					lore.add(Component.empty());
					itemMeta.lore(lore);
					itemStack.setItemMeta(itemMeta);
					if (ItemLoreUtil.isCucumberyTMIFood(itemStack))
					{
						ItemLoreUtil.removeCucumberyTMIFood(itemStack);
					}
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄에 공백 설명을 추가했습니다", Constant.THE_COLOR_HEX + lore.size())
									.hoverEvent(itemStack.asHoverEvent()));
				}
				else
				{
					String input = MessageUtil.n2s(MessageUtil.listToString(" ", args), MessageUtil.N2SType.SPECIAL_ONLY);
					Component inputComponent = ComponentUtil.create(input);
					if (input.contains("--json"))
					{
						input = input.replaceFirst("--json", "");
						NBTItem nbtItem = new NBTItem(itemStack);
						NBTCompound display = nbtItem.addCompound("display");
						NBTList<String> loreCompound = display.getStringList("Lore");
						loreCompound.add(input);
						itemStack = nbtItem.getItem();
						lore = itemStack.getItemMeta().lore();
						if (lore == null)
						{
							lore = new ArrayList<>();
						}
					}
					else
					{
						lore.add(inputComponent);
						itemMeta.lore(lore);
						itemStack.setItemMeta(itemMeta);
					}
					if (ItemLoreUtil.isCucumberyTMIFood(itemStack))
					{
						ItemLoreUtil.removeCucumberyTMIFood(itemStack);
					}
					inputComponent = ComponentUtil.create(input);
					if (inputComponent.color() == null)
					{
						inputComponent = inputComponent.color(Constant.THE_COLOR);
					}
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명에 %s(이)라는 설명을 추가했습니다", Constant.THE_COLOR_HEX + lore.size(), inputComponent)
									.hoverEvent(itemStack.asHoverEvent()));
				}
				inventory.setItemInMainHand(itemStack);

			}
		}
		else if (cmd.getName().equals("deletelore"))
		{
			List<Component> lores = itemMeta.lore();
			if (lores == null || lores.isEmpty())
			{
				MessageUtil.sendError(player, "더 이상 제거할 수 있는 설명이 없습니다");
				return true;
			}
			if (args.length == 0)
			{
				lores.removeLast();
				itemMeta.lore(lores);
				itemStack.setItemMeta(itemMeta);
				if (lores.isEmpty())
				{
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 모든 설명을 제거했습니다").hoverEvent(itemStack.asHoverEvent()));
				}
				else
				{
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명을 제거했습니다", Constant.THE_COLOR_HEX + (lores.size() + 1))
									.hoverEvent(itemStack.asHoverEvent()));
				}
				inventory.setItemInMainHand(itemStack);
			}
			else if (args.length == 1)
			{
				if (args[0].equals("--all"))
				{
					itemMeta.lore(null);
					itemStack.setItemMeta(itemMeta);
					inventory.setItemInMainHand(itemStack);
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 모든 설명을 제거했습니다").hoverEvent(itemStack.asHoverEvent()));
					return true;
				}
				if (!MessageUtil.isInteger(sender, args[0], true))
				{
					return true;
				}
				int index = Integer.parseInt(args[0]);
				if (!MessageUtil.checkNumberSize(sender, index, 1, lores.size()))
				{
					return true;
				}
				lores.remove(index - 1);
				itemMeta.lore(lores);
				itemStack.setItemMeta(itemMeta);
				inventory.setItemInMainHand(itemStack);
				if (lores.isEmpty())
				{
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 모든 설명을 제거했습니다").hoverEvent(itemStack.asHoverEvent()));
				}
				else
				{
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명을 제거했습니다", Constant.THE_COLOR_HEX + (lores.size() + 1))
									.hoverEvent(itemStack.asHoverEvent()));
				}
			}
			else
			{
				MessageUtil.longArg(sender, 4, args);
				MessageUtil.commandInfo(sender, label, usage);
			}
		}
		else if (cmd.getName().equals("insertlore"))
		{
			if (args.length < 2)
			{
				MessageUtil.shortArg(sender, 2, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			if (!MessageUtil.isInteger(sender, args[0], true))
			{
				return true;
			}
			List<Component> lore = itemMeta.lore();
			if (lore == null)
			{
				MessageUtil.sendError(sender, "설명을 들여쓸 수 없습니다");
				return true;
			}
			int index = Integer.parseInt(args[0]);
			if (!MessageUtil.checkNumberSize(sender, index, 1, lore.size()))
			{
				return true;
			}
			String input = MessageUtil.n2s(MessageUtil.listToString(" ", 1, args.length, args), MessageUtil.N2SType.SPECIAL_ONLY);
			boolean empty = input.equals("--empty");
			Component inputComponent = empty ? Component.empty() : ComponentUtil.create(input);
			lore.add(index - 1, inputComponent);
			itemMeta.lore(lore);
			itemStack.setItemMeta(itemMeta);
			if (ItemLoreUtil.isCucumberyTMIFood(itemStack))
			{
				ItemLoreUtil.removeCucumberyTMIFood(itemStack);
			}
			inventory.setItemInMainHand(itemStack);
			if (empty)
			{
				MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
						ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 %s번째 줄에 공백 설명을 들여썼습니다", Constant.THE_COLOR_HEX + index).hoverEvent(itemStack.asHoverEvent()));
			}
			else
			{
				if (inputComponent.color() == null)
				{
					inputComponent = inputComponent.color(Constant.THE_COLOR);
				}
				MessageUtil.sendMessage(player,
						ComponentUtil.translate(Prefix.INFO_SETDATA + "주로 사용하는 손에 들고 있는 아이템의 %s번째 줄의 설명에 %s(이)라는 설명을 들여썼습니다", Constant.THE_COLOR_HEX + index,
								inputComponent).hoverEvent(itemStack.asHoverEvent()));
			}
		}
		else if (cmd.getName().equals("setrepaircost"))
		{
			if (args.length == 0)
			{
				MessageUtil.shortArg(sender, 1, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
			else if (args.length <= 2)
			{
				if (!MessageUtil.isInteger(sender, args[0], true))
				{
					return true;
				}
				int repairCost = Integer.parseInt(args[0]);
				if (!MessageUtil.checkNumberSize(sender, repairCost, 0, 30))
				{
					return true;
				}
				((Repairable) itemMeta).setRepairCost((int) Math.pow(2, repairCost) - 1);
				itemStack.setItemMeta(itemMeta);
				inventory.setItemInMainHand(itemStack);
				ItemStackUtil.updateInventory(player);
				if (!MessageUtil.isBoolean(sender, args, 2, true))
				{
					return true;
				}
				boolean hideOutput = args.length == 2 && Boolean.parseBoolean(args[1]);
				if (!hideOutput)
				{
					MessageUtil.sendMessage(player, Prefix.INFO_SETDATA,
							ComponentUtil.translate("주로 사용하는 손에 들고 있는 아이템의 모루 사용 횟수를 %s번으로 설정했습니다", Constant.THE_COLOR_HEX + repairCost)
									.hoverEvent(itemStack.asHoverEvent()));
				}
			}
			else
			{
				MessageUtil.longArg(sender, 2, args);
				MessageUtil.commandInfo(sender, label, usage);
				return true;
			}
		}
		return true;
	}

	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(true, args), true))
		{
			return Collections.singletonList(args[0]);
		}
		int length = args.length;
		String name = cmd.getName();
		if (name.equals("setname") && sender instanceof Player player)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemStackUtil.itemExists(item))
			{
				return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
			}
			if (length == 1)
			{
				return Method.tabCompleterList(args, "<아이템 이름>", true, "<아이템 이름>", "--remove");
			}
			return Method.tabCompleterList(args, "[아이템 이름]", true);
		}
		else if (name.equals("setname2") && sender instanceof Player player)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemStackUtil.itemExists(item))
			{
				return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
			}
			if (length == 1)
			{
				return Method.tabCompleterList(args, "<추가로 입력할 이름|--지울 글자 수>", true, "<추가로 입력할 이름>", "--");
			}
			return Method.tabCompleterList(args, "[추가로 입력할 이름]", true);
		}
		else if (name.equals("setlore") && sender instanceof Player player)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemStackUtil.itemExists(item))
			{
				return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
			}
			if (length == 1)
			{
				return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "<줄>");
			}
			else
			{
				try
				{
					if (args[1].isEmpty())
					{
						return Method.tabCompleterList(args, "<설명>", true, "<설명>", "--empty",
								item.getItemMeta().getLore().get(Integer.parseInt(args[0]) - 1).replace("§", "&"));
					}
				}
				catch (Exception ignored)
				{

				}
				if (length == 2)
				{
					return Method.tabCompleterList(args, "<설명>", true, "<설명>", "--empty");
				}
				return Method.tabCompleterList(args, "[설명]", true);
			}
		}
		else if (name.equals("setlore2") && sender instanceof Player player)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemStackUtil.itemExists(item))
			{
				return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
			}
			if (length == 1)
			{
				return Method.tabCompleterIntegerRadius(args, 1, Integer.MAX_VALUE, "<줄>");
			}
			else
			{
				if (length == 2)
				{
					return Method.tabCompleterList(args, "<추가로 입력할 설명|--지울 글자 수>", true, "<추가로 입력할 설명>", "--");
				}
				return Method.tabCompleterList(args, "[추가로 입력할 설명]", true);
			}
		}
		else if (name.equals("addlore") && sender instanceof Player player)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemStackUtil.itemExists(item))
			{
				return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
			}
			if (length == 1)
			{
				return Method.tabCompleterList(args, "<설명>", true, "<설명>", "--empty");
			}
			return Method.tabCompleterList(args, "[설명]", true);
		}
		else if (name.equals("deletelore") && sender instanceof Player)
		{
			if (length == 1)
			{
				Player player = (Player) sender;
				ItemStack item = player.getInventory().getItemInMainHand();
				if (!ItemStackUtil.itemExists(item))
				{
					return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
				}
				else if (!ItemStackUtil.hasLore(item, true))
				{
					return Collections.singletonList("더 이상 제거할 수 있는 설명이 없습니다");
				}
				List<String> lore = item.getItemMeta().getLore();
				return Method.tabCompleterIntegerRadius(args, 1, lore == null ? 1 : lore.size(), "[줄]", "--all");
			}
		}
		else if (name.equals("insertlore") && sender instanceof Player player)
		{
			ItemStack item = player.getInventory().getItemInMainHand();
			if (!ItemStackUtil.itemExists(item))
			{
				return Collections.singletonList(Prefix.NO_HOLDING_ITEM.toString());
			}
			else if (!ItemStackUtil.hasLore(item, true))
			{
				return Collections.singletonList("설명을 들여쓸 수 없습니다");
			}
			if (length == 1)
			{
				List<String> lore = item.getItemMeta().getLore();
				return Method.tabCompleterIntegerRadius(args, 1, lore == null ? 1 : lore.size(), "<줄>");
			}
			else
			{
				if (length == 2)
				{
					return Method.tabCompleterList(args, "<설명>", true, "<설명>", "--empty");
				}
				return Method.tabCompleterList(args, "[설명]", true);
			}
		}
		else if (name.equals("setrepaircost"))
		{
			if (length == 1)
			{
				return Method.tabCompleterIntegerRadius(args, 0, 30, "<누적 모루 합성 횟수>");
			}
			else if (length == 2)
			{
				return Method.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
			}
		}
		return Collections.singletonList(Prefix.ARGS_LONG.toString());
	}
}
