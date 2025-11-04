package com.jho5245.cucumbery.util.no_groups;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent.Completion;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.ghostchu.quickshop.api.QuickShopAPI;
import com.ghostchu.quickshop.api.shop.Shop;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.custommaterial.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import net.coreprotect.config.ConfigHandler;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("all")
public class TestCommand implements CucumberyCommandExecutor
{
	private static final NamespacedKey test = new NamespacedKey("cucumbery", "test_recipe");

	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		args = MessageUtil.wrapWithQuote(args);
		if (!Method.hasPermission(sender, "asdf", true))
		{
			return true;
		}
		try
		{
			switch (args[0])
			{
				case "biome" ->
				{
					Player player = (Player) sender;
					MessageUtil.info(player, player.getLocation().getBlock().getBiome().getKey());
				}
				case "entity-id" ->
				{
					sender.sendMessage(Bukkit.getUnsafe().nextEntityId() + "");
					List<Integer> integers = new ArrayList<>();
					for (World world : Bukkit.getWorlds())
					{
						for (Entity entity : world.getEntities())
						{
							integers.add(entity.getEntityId());
						}
					}
					sender.sendMessage(Arrays.toString(integers.toArray()));
				}
				case "coreproect" ->
				{
					Player player = (Player) sender;
					player.sendMessage(Cucumbery.using_CoreProtect + "");
					player.sendMessage(ConfigHandler.inspecting.getOrDefault(player.getName(), false) + "");
				}
				case "quickshop" ->
				{
					QuickShopAPI quickShopAPI = QuickShopAPI.getInstance();
					List<Shop> shops = quickShopAPI.getShopManager().getAllShops();
					for (Shop shop : shops)
					{
						Location location = shop.getLocation();
						ItemStack itemStack = shop.getItem();
						MessageUtil.sendMessage(sender, "location: %s, item: %s", location, itemStack);
						shop.setPrice(Math.random());
					}
				}
				case "custom-material" ->
				{
					Player player = (Player) sender;
					switch (args[1])
					{
						case "list" ->
						{
							List<CustomMaterial> customMaterials = new ArrayList<>(CustomMaterial.values());
							int page = args.length >= 3 ? Integer.parseInt(args[2]) : 1;
							int maxPage = customMaterials.size() / 54 + 1;
							Inventory inventory = Bukkit.createInventory(null, 54, Component.text("Custom Materials"));
							for (int i = (page - 1) * 54; i < Math.min(maxPage * 54 - 1, customMaterials.size()); i++)
							{
								inventory.addItem(customMaterials.get(i).create());
							}
							player.openInventory(inventory);
						}
						case "info" ->
						{
							String input = args.length >= 3 ? args[2] : "cucumbery:test_item";
							String[] split = input.split(":");
							NamespacedKey namespacedKey = new NamespacedKey(split[0], split[1]);
							CustomMaterial customMaterial = CustomMaterial.getByKey(namespacedKey);
							MessageUtil.sendMessage(player, "item is : %s", customMaterial);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			Cucumbery.getPlugin().getLogger().warning(e.getMessage());
		}
		return true;
	}

	public static void setPlayerNameTag(Player player, String name)
	{
		PlayerProfile playerProfile = player.getPlayerProfile();
		playerProfile.setName(name);
		player.setPlayerProfile(playerProfile);
	}

	public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args)
	{
		if (!MessageUtil.checkQuoteIsValidInArgs(sender, args = MessageUtil.wrapWithQuote(true, args), true))
		{
			return Collections.singletonList(args[0]);
		}
		int length = args.length;
		if (length == 1)
		{
			return Method.tabCompleterList(args, "<뭐 왜 왓>", "entities", "entity", "players", "player", "custom-material");
		}
		else if (length == 2)
		{
			switch (args[0])
			{
				case "entities" ->
				{
					return Method.tabCompleterEntity(sender, args, "<개체>", true);
				}
				case "entity" ->
				{
					return Method.tabCompleterEntity(sender, args, "<개체>");
				}
				case "players" ->
				{
					return Method.tabCompleterPlayer(sender, args, "<플레이어>", true);
				}
				case "player" ->
				{
					return Method.tabCompleterPlayer(sender, args, "<플레이어>");
				}
			}
			return Collections.EMPTY_LIST;
		}
		return Collections.singletonList(Prefix.ARGS_LONG.toString());
	}

	@NotNull
	public List<Completion> completion(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args,
			@NotNull Location location)
	{
		int length = args.length;
		if (length == 1)
		{
			//			List<Completion> list1 = CommandTabUtil.tabCompleterEntity(sender, args, "<개체>"), list2 = CommandTabUtil.worldArgument(sender, args,
			//					"<월드>"), list3 = CommandTabUtil.itemStackArgument(sender, args, "<아이템>"), list4 = CommandTabUtil.locationArgument(sender, args, "<위치>", null,
			//					false), list5 = CommandTabUtil.rotationArgument(sender, args, "<방향>", null), list6 = CommandTabUtil.tabCompleterList(args, "<아아무거아무거나>", false,
			//					"햄버거와 뚱이", "푸아그라탕 샌즈", "와니 필통에 있는 볼펜심 안에 있는 초록색 잉크", "지은지 70년 이상 지나서 철거한 건물 콘크리트 가루", "밀랓비탈헝 갹간 국방적으로 깍ㄲ인 구리 세로 반 브록", " <뭐> ", " <나닛>", " ㅇㅅㅇ", " 오",
			//					" 오이", "오이 베드서버에 있는 줄 알았는데", "키보드가 있는 것으로 알려졌다 보니 정말 죄송합니다 오후 서울", " 난", " 알아요", "자기 서버에 있는것 같구만", " ㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋㅋ",
			//					"밀랍칠한 약간 국방적으로 깎인 구리 세로 반 블록 — 오늘 오후 1:23\n" + "베이스 스트링 아 참고로 난 영화 보고 있다 ㅋㅋㄹㅃㅃ\n" + "난 영화 속에 나오는 우주 정거장은 항상 웃기다고 생각했었어",
			//					"gusen1116 — 오늘 오후 1:24\n" + "봐라, 않는 그리하였는가? 그들은 오아이스도 인간은 그들에게 이것은 그와 아니다. 그들은 같이 피어나기 온갖 대중을 풀밭에 생명을 우리의 사막이다.\n" + "아니 근데\n" + "군포가 뭐하는 곳이기래\n"
			//							+ "내 택배가 4일째 거기에 있냐\n" + "밀랍칠한 약간 국방적으로 깎인 구리 세로 반 블록 — 오늘 오후 1:26\n" + "군머포장\n" + "특징:늦음\n" + "gusen1116 — 오늘 오후 1:27\n" + "아", "", "");
			//			List<Completion> list = CommandTabUtil.tabCompleterList(args, CustomEffectType.getEffectTypeMap(), "<effect>");
			return Collections.singletonList(Completion.completion("custom-material"));
			//			return CommandTabUtil.sortError(list1, list2, list3, list4, list5, list);
		}

		if (length >= 2)
		{
			switch (args[0])
			{
				case "custom-material" ->
				{
					switch (length)
					{
						case 2 ->
						{
							return CommandTabUtil.tabCompleterList(args, "<인수>", false, "list", "info");
						}
						case 3 ->
						{
							if (args[1].equals("info"))
							{
								return CommandTabUtil.tabCompleterList(args, CustomMaterial.keySet(), "<아이템>");
							}
						}
					}
				}
			}
		}

		if (length == 2)
		{
			if (CommandArgumentUtil.world(sender, args[0], false) != null)
			{
				return CommandTabUtil.locationArgument(sender, args, "<위치>", location, false);
			}
		}
		if (length == 3)
		{
			if (CommandArgumentUtil.location(sender, args[1], false, false) != null)
			{
				return CommandTabUtil.rotationArgument(sender, args, "<방향>", location);
			}
		}
		return CommandTabUtil.ARGS_LONG;
	}
}

