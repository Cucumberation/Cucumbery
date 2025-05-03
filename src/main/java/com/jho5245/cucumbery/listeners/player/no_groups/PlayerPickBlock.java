package com.jho5245.cucumbery.listeners.player.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.listeners.inventory.InventoryClick;
import com.jho5245.cucumbery.util.blockplacedata.BlockPlaceDataConfig;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.storage.no_groups.BlockDataInfo;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBTCompound;
import de.tr7zw.changeme.nbtapi.NBTItem;
import io.papermc.paper.event.player.PlayerPickBlockEvent;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PlayerPickBlock implements Listener
{
	@EventHandler
	public void onPlayerPickBlock(PlayerPickBlockEvent event)
	{
		Player player = event.getPlayer();
		Block block = event.getBlock();
		boolean includeData = event.isIncludeData();
		int targetSlot = event.getTargetSlot();
		int sourceSlot = event.getSourceSlot();
		boolean isCancelled = event.isCancelled();

		// 커스텀 블록 픽블록
		ItemStack itemStackFromBlock = BlockPlaceDataConfig.getItem(block.getLocation());
		if (itemStackFromBlock != null)
		{
/*				player.sendMessage(
						"type: %s, include: %s, targetSlot: %s, sourceSlot: %s, isCancelled: %s".formatted(block.getType(), includeData, targetSlot, sourceSlot,
								isCancelled));*/
			PlayerInventory inventory = player.getInventory();

			// itemStackFromBlock과 동일한 아이템이 인벤토리에 있는지 확인
			boolean hasItem = false;
			// 손에 들고 있는 아이템이 동일한 아이템일 경우
			if (ItemStackUtil.itemEquals(inventory.getItemInMainHand(), itemStackFromBlock))
			{
				event.setCancelled(true);
			}
			// 아닐 경우 인벤토리 탐색
			else
			{
				for (int i = 0; i < inventory.getSize(); i++)
				{
					ItemStack inventoryItemStack = inventory.getItem(i);
					if (ItemStackUtil.itemEquals(itemStackFromBlock, inventoryItemStack))
					{
						event.setCancelled(true);
						// 찾은 아이템이 핫바에 있을 경우(0~8번 슬롯) 손에 든 아이템 슬롯을 해당 슬롯으로 지정
						if (i < 9)
						{
							inventory.setHeldItemSlot(i);
						}
						// 아닐 경우 인벤토리에서 아이템을 가져와 mainHand에 지정. 기존 슬롯에 있는 아이템 제거
						else
						{
							inventory.setItemInMainHand(inventoryItemStack);
							inventory.setItem(i, null);
						}
						hasItem = true;
						break;
					}
				}
			}
			// 크리에이티브일 경우 itemStackFromBlock과 동일한 아이템이 인벤토리에 없으면 mainHand에 바로 지정
			if (!hasItem && player.getGameMode() == GameMode.CREATIVE)
			{
				event.setCancelled(true);
				inventory.setItemInMainHand(itemStackFromBlock);
				return;
			}
		}

		// 크리에이티브에서 픽블록 시 블록 데이터 복사 기능/소리 블록의 악기, 음높이 복사 기능
		if (player.getGameMode() == GameMode.CREATIVE) // 소리 블록을 픽블록 했을 때 해당 블록의 음높이와 악기를 아이템 설명에 복사하는 기능
		{
			UUID uuid = player.getUniqueId();
			boolean copyBlockData = UserData.COPY_BLOCK_DATA.getBoolean(uuid);
			boolean copyBlockDataWhenSneaking = !UserData.COPY_BLOCK_DATA_WHEN_SNEAKING.getBoolean(uuid) || player.isSneaking();

			if (copyBlockData && copyBlockDataWhenSneaking)
			{
				Material blockType = block.getType();
				if (block.getType() != Material.NOTE_BLOCK)
				{
					if (BlockDataInfo.getBlockDataKeys(blockType) != null)
					{
						try
						{
							String blockDataString = block.getBlockData().getAsString();
							blockDataString = blockDataString.split("\\[")[1].replace("]", "");
							String[] dataArray = blockDataString.split(",");
							NBTItem nbtItem = new NBTItem(block.getType().asItemType().createItemStack());
							NBTCompound blockStateTag = nbtItem.addCompound(CucumberyTag.MINECRAFT_BLOCK_STATE_TAG_KEY);
							for (String data : dataArray)
							{
								String[] split = data.split("=");
								String key = split[0];
								switch (key)
								{
									case "facing":
										if (!UserData.COPY_BLOCK_DATA_FACING.getBoolean(uuid))
										{
											continue;
										}
									case "waterlogged":
										if (!UserData.COPY_BLOCK_DATA_WATERLOGGED.getBoolean(uuid))
										{
											continue;
										}
								}
								String value = split[1];
								blockStateTag.setString(key, value);
							}
							if (!blockStateTag.getKeys().isEmpty())
							{
								event.setCancelled(true);
								player.getInventory().setItemInMainHand(nbtItem.getItem());
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}

			boolean copyPitch = UserData.COPY_NOTE_BLOCK_PITCH.getBoolean(uuid);
			boolean copyInstrument = UserData.COPY_NOTE_BLOCK_INSTRUMENT.getBoolean(uuid);
			boolean copyWhenSneaking = !UserData.COPY_NOTE_BLOCK_VALUE_WHEN_SNEAKING.getBoolean(uuid) || player.isSneaking();
			if (copyWhenSneaking && (copyPitch || copyInstrument)) // 음높이 또는 악기 복사 기능을 하나 이상 켠 경우
			{
				Set<Material> transparent = new HashSet<>();
				for (Material material : Material.values())
				{
					if (!material.isOccluding())
					{
						transparent.add(material);
					}
				}
				if (block.getType() == Material.NOTE_BLOCK)
				{
					try
					{
						String blockDataString = block.getBlockData().getAsString();
						blockDataString = blockDataString.split("\\[")[1].replace("]", "");
						String[] dataArray = blockDataString.split(",");
						NBTItem nbtItem = new NBTItem(new ItemStack(block.getType()));
						NBTCompound blockStateTag = nbtItem.addCompound(CucumberyTag.MINECRAFT_BLOCK_STATE_TAG_KEY);
						for (String data : dataArray)
						{
							String[] split = data.split("=");
							String key = split[0];
							String value = split[1];
							if (key.equals("instrument") && copyInstrument)
							{
								blockStateTag.setString(key, value);
							}
							if (key.equals("note") && copyPitch)
							{
								blockStateTag.setString(key, value);
							}
						}
						event.setCancelled(true);
						player.getInventory().setItemInMainHand(nbtItem.getItem());
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}
