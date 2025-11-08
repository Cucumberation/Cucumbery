package com.jho5245.cucumbery.commands.addon;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.ghostchu.quickshop.QuickShop;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.CommandTabUtil;
import com.jho5245.cucumbery.util.no_groups.CucumberyCommandExecutor;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.component.util.ItemNameUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Permission;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.ItemInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandQuickShopAddon implements CucumberyCommandExecutor
{
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		if (!Method.hasPermission(sender, Permission.CMD_QUICKSHOP_ADDON, true))
		{
			return true;
		}
		if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(args)))
		{
			return !(sender instanceof BlockCommandSender);
		}
		if (!Cucumbery.using_QuickShop)
		{
			MessageUtil.sendError(sender, "%s 플러그인을 사용하고 있지 않습니다", Constant.THE_COLOR_HEX + "QuickShop");
			return true;
		}
		if (args.length < 2)
		{
			MessageUtil.shortArg(sender, 2, args);
			MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
			return true;
		}
		else if (args.length <= 4)
		{
			String type = args[1];
			if (!Method.equals(type, "price", "amount", "type", "get", "info"))
			{
				MessageUtil.wrongArg(sender, 2, args);
				return true;
			}
			if (Method.equals(type, "price", "amount", "type") && args.length == 2)
			{
				MessageUtil.shortArg(sender, 3, args);
				MessageUtil.commandInfo(sender, label, "<상점 위치> <price|amount|type|get|info> <값> [명령어 출력 숨김 여부]");
				return true;
			}
			String[] argSplit = args[0].split(" ");
			if (argSplit.length != 4)
			{
				MessageUtil.sendError("상점 위치는 '월드 x y z' 형태이어야 합니다.");
				return true;
			}
			World world = Bukkit.getWorld(argSplit[0]);
			if (world == null)
			{
				MessageUtil.noArg(sender, Prefix.NO_WORLD, argSplit[0]);
				return true;
			}
			if (!MessageUtil.isInteger(sender, argSplit[1], true))
			{
				return true;
			}
			if (!MessageUtil.isInteger(sender, argSplit[2], true))
			{
				return true;
			}
			if (!MessageUtil.isInteger(sender, argSplit[3], true))
			{
				return true;
			}
			int x = Integer.parseInt(argSplit[1]), y = Integer.parseInt(argSplit[2]), z = Integer.parseInt(argSplit[3]);
			if (!MessageUtil.checkNumberSize(sender, x, -30000000, 30000000))
			{
				return true;
			}
			if (!MessageUtil.checkNumberSize(sender, y, -300, 3000))
			{
				return true;
			}
			if (!MessageUtil.checkNumberSize(sender, z, -30000000, 30000000))
			{
				return true;
			}
			if (!MessageUtil.isBoolean(sender, args, 4, true))
			{
				return true;
			}
			boolean hideOutput = args.length == 4 && args[3].equals("true");
			Location location = new Location(world, x, y, z);
			Shop shop = QuickShop.getInstance().getShopManager().getShop(location);
			if (shop == null)
			{
				MessageUtil.sendError(sender, "%s에 있는 상점을 찾을 수 없습니다", location);
				return true;
			}
			switch (type)
			{
				case "price" ->
				{
					if (!MessageUtil.isDouble(sender, args[2], true))
					{
						return true;
					}
					double price = Double.parseDouble(args[2]);
					if (!MessageUtil.checkNumberSize(sender, price, 0d, 10_000_000_000_000d))
					{
						return true;
					}
					if (shop.getPrice() == price)
					{
						if (!hideOutput)
						{
							MessageUtil.sendError(sender, "변경 사항이 없습니다. 이미 %s에 있는 상점(%s)의 거래 가격이 %s입니다", location, shop.getItem(),
									Constant.THE_COLOR_HEX + Constant.Sosu15.format(price) + "원");
						}
						return true;
					}
					shop.setPrice(price);
					if (!hideOutput)
					{
						MessageUtil.info(sender, "%s에 있는 상점(%s)의 거래 가격을 %s으로 설정했습니다", location, shop.getItem(),
								Constant.THE_COLOR_HEX + Constant.Sosu15.format(price) + "원");
					}
				}
				case "amount" ->
				{
					if (!MessageUtil.isInteger(sender, args[2], true))
					{
						return true;
					}
					int amount = Integer.parseInt(args[2]);
					ItemStack item = shop.getItem();
					if (item.getMaxStackSize() == 1)
					{
						MessageUtil.sendError(sender, "%s의 최대 거래 개수가 1개여서 변경할 수 없습니다", item);
						return true;
					}
					if (!MessageUtil.checkNumberSize(sender, amount, 1, item.getMaxStackSize()))
					{
						return true;
					}
					if (item.getAmount() == amount)
					{
						if (!hideOutput)
						{
							MessageUtil.sendError(sender, "변경 사항이 없습니다. 이미 %s에 있는 상점(%s)의 거래 개수가 %s입니다", location, shop.getItem(), Constant.THE_COLOR_HEX + amount + "개");
						}
						return true;
					}
					item = item.clone();
					item.setAmount(amount);
					shop.setItem(item);
					if (!hideOutput)
					{
						MessageUtil.info(sender, "%s에 있는 상점(%s)의 거래 개수를 %s로 설정했습니다", location, item, Constant.THE_COLOR_HEX + amount + "개");
					}
				}
				case "type" ->
				{
					com.ghostchu.quickshop.api.shop.ShopType shopType;
					try
					{
						shopType = com.ghostchu.quickshop.api.shop.ShopType.valueOf(args[2].toUpperCase());
					}
					catch (Exception e)
					{
						MessageUtil.wrongArg(sender, 3, args);
						return true;
					}
					String typeString = switch (args[2])
					{
						case "buying" -> "&b판매 상점&r(상점이 플레이어한테서 &c구매&r)";
						case "selling" -> "&c구매 상점&r(상점이 플레이어한테 &b판매&r)";
						default -> "";
					};
					if (shopType == shop.getShopType())
					{
						if (!hideOutput)
						{
							MessageUtil.sendError(sender, "변경 사항이 없습니다. 이미 %s에 있는 상점(%s)이 %s입니다", location, shop.getItem(), typeString);
						}
						return true;
					}
					shop.setShopType(shopType);
					if (!hideOutput)
					{
						MessageUtil.info(sender, "%s에 있는 상점(%s)을 %s(으)로 설정했습니다", location, shop.getItem(), typeString);
					}
				}
				case "get" ->
				{
					if (!(sender instanceof Player))
					{
						MessageUtil.sendError(sender, Prefix.ONLY_PLAYER);
						return true;
					}
					ItemStack item = shop.getItem().clone();
					int amount = item.getAmount();
					if (args.length == 3)
					{
						if (!MessageUtil.isInteger(sender, args[2], true))
						{
							return true;
						}
						amount = Integer.parseInt(args[2]);
						if (!MessageUtil.checkNumberSize(sender, amount, 1, 2304))
						{
							return true;
						}
						item.setAmount(amount);
					}
					((Player) sender).getInventory().addItem(item);
					MessageUtil.info(sender, "%s에 있는 상점의 아이템 %s을(를) %s 지급받았습니다.", location, item, Constant.THE_COLOR_HEX + amount + "개");
				}
				case "info" ->
				{
					if (args.length == 2)
					{
						ItemInfo.sendInfo(sender, shop.getItem());
					}
					else
					{
						MessageUtil.longArg(sender, 2, args);
						MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
						return true;
					}
				}
			}
		}
		else
		{
			MessageUtil.longArg(sender, 4, args);
			MessageUtil.commandInfo(sender, label, Method.getUsage(cmd));
			return true;
		}
		return true;
	}

	@Override
	public @NotNull List<Completion> completion(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args,
			@NotNull Location location)
	{
		if (!Cucumbery.using_QuickShop)
		{
			return Collections.singletonList(Completion.completion("QuickShop 플러그인을 사용하고 있지 않습니다"));
		}
		int length = args.length;
		if (QuickShopAPI.getInstance().getShopManager().getAllShops().isEmpty() && args.length >= 2 && args.length <= 6)
		{
			return Collections.singletonList(Completion.completion("유효한 QuickShop 상점이 존재하지 않습니다"));
		}
		List<String> list = new ArrayList<>();
		List<String> xList = new ArrayList<>();
		List<String> yList = new ArrayList<>();
		List<String> zList = new ArrayList<>();
		for (Shop shop : QuickShopAPI.getInstance().getShopManager().getAllShops())
		{
			getShopLocation(args, list, xList, yList, zList, shop);
		}
		if (sender instanceof Player player)
		{
			Block block = player.getTargetBlockExact(5);
			if (block != null)
			{
				switch (block.getType())
				{
					case ACACIA_WALL_SIGN, BIRCH_WALL_SIGN, CRIMSON_WALL_SIGN, DARK_OAK_WALL_SIGN, JUNGLE_WALL_SIGN, OAK_WALL_SIGN, SPRUCE_WALL_SIGN, WARPED_WALL_SIGN ->
					{
						BlockFace blockFace = player.getTargetBlockFace(5);
						Location loc = block.getLocation();
						int x = loc.getBlockX(), y = loc.getBlockY(), z = loc.getBlockZ();
						if (blockFace != null)
						{
							switch (blockFace)
							{
								case WEST -> block = player.getWorld().getBlockAt(x + 1, y, z);
								case EAST -> block = player.getWorld().getBlockAt(x - 1, y, z);
								case DOWN -> block = player.getWorld().getBlockAt(x, y + 1, z);
								case UP -> block = player.getWorld().getBlockAt(x, y - 1, z);
								case NORTH -> block = player.getWorld().getBlockAt(x, y, z + 1);
								case SOUTH -> block = player.getWorld().getBlockAt(x, y, z - 1);
							}
						}
					}
				}
			}
			Shop shop = null;
			if (block != null)
			{
				shop = QuickShop.getInstance().getShopManager().getShop(block.getLocation());
			}
			if (shop != null)
			{
				list.clear();
				xList.clear();
				yList.clear();
				zList.clear();
				getShopLocation(args, list, xList, yList, zList, shop);
			}
		}
		if (length == 1)
		{
			return CommandTabUtil.tabCompleterList(args, list, "<상점 위치>", true);
		}
		else if (length <= 3)
		{
			Shop shop;
			String[] argSplit = args[0].split(" ");
			if (argSplit.length != 4)
			{
				return Collections.singletonList(Completion.completion("상점 위치는 '월드 x y z' 형태이어야 합니다."));
			}
			try
			{
				shop = QuickShop.getInstance().getShopManager()
						.getShop(new Location(Bukkit.getWorld(argSplit[0]), Integer.parseInt(argSplit[1]), Integer.parseInt(argSplit[2]), Integer.parseInt(argSplit[3])));
				if (shop == null)
				{
					throw new NullPointerException();
				}
			}
			catch (Exception e)
			{
				return Collections.singletonList(Completion.completion(argSplit[0] + " " + argSplit[1] + " " + argSplit[2] + " " + argSplit[3] + "에 있는 상점을 찾을 수 없습니다"));
			}
			ItemStack shopItem = shop.getItem();
			if (args.length == 2)
			{
				if (sender instanceof Player)
				{
					return CommandTabUtil.tabCompleterList(args, "<인수>", false, "price", "amount" + (shopItem.getMaxStackSize() == 1 ? "(변경 불가)" : ""), "type", "info",
							"get");
				}
				return CommandTabUtil.tabCompleterList(args, "<인수>", false, "price", "amount" + (shopItem.getMaxStackSize() == 1 ? "(변경 불가)" : ""), "type", "info");
			}
			String display = MessageUtil.stripColor(ComponentUtil.serialize(ItemNameUtil.itemName(shopItem)));
			switch (args[1])
			{
				case "price" ->
				{
					List<Completion> list1 = CommandTabUtil.tabCompleterList(args, List.of(Constant.rawFormat.format(shop.getPrice())), "<" + display + "의 거래 가격>");
					List<Completion> list2 = CommandTabUtil.tabCompleterDoubleRadius(args, 0d, false, 10_000_000_000_000d, false, "<" + display + "의 거래 가격>");
					return CommandTabUtil.sortError(list1, list2);
				}
				case "amount" ->
				{
					if (shopItem.getMaxStackSize() == 1)
					{
						return Collections.singletonList(Completion.completion(display + "의 최대 거래 개수가 1개여서 변경할 수 없습니다"));
					}
					List<Completion> list1 = CommandTabUtil.tabCompleterList(args, List.of(shopItem.getAmount() + ""), "<" + display + "의 거래 개수>");
					List<Completion> list2 = CommandTabUtil.tabCompleterIntegerRadius(args, 1, shopItem.getMaxStackSize(), "<" + display + "의 거래 개수>");
					return CommandTabUtil.sortError(list1, list2);
				}
				case "type" ->
				{
					return CommandTabUtil.tabCompleterList(args, "<상점 타입>", false, "selling", "buying");
				}
				case "get" ->
				{
					return CommandTabUtil.tabCompleterIntegerRadius(args, 1, 2304, "[개수]");
				}
			}
		}
		else if (length == 4)
		{
			if (Method.equals(args[1], "price", "amount", "type"))
			{
				return CommandTabUtil.tabCompleterBoolean(args, "[명령어 출력 숨김 여부]");
			}
		}
		return CommandTabUtil.ARGS_LONG;
	}

	private void getShopLocation(@NotNull String[] args, List<String> list, List<String> xList, List<String> yList, List<String> zList, Shop shop)
	{
		Location shopLocation = shop.getLocation();
		String display = MessageUtil.stripColor(ComponentUtil.serialize(ItemNameUtil.itemName(shop.getItem())));
		if (shopLocation.getWorld() != null)
		{
			String worldName = shopLocation.getWorld().getName();
			int x = shopLocation.getBlockX(), y = shopLocation.getBlockY(), z = shopLocation.getBlockZ();
			list.add(worldName + " " + x + " " + y + " " + z);
			list.add(worldName + " " + x + " " + y + " " + z + "(" + display + ")");
			if (args.length >= 2 && args[0].equals(worldName))
			{
				xList.add(x + " " + y + " " + z);
				xList.add(x + " " + y + " " + z + "(" + display + ")");
				if (args.length >= 3 && args[1].equals(x + ""))
				{
					yList.add(y + " " + z);
					yList.add(y + " " + z + "(" + display + ")");
					if (args.length >= 4 && args[2].equals(y + ""))
					{
						zList.add(z + "");
						zList.add(z + "(" + display + ")");
					}
				}
			}
		}
	}
}
