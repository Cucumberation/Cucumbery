package com.jho5245.cucumbery.custom.customeffect.custom_mining;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.custom.customeffect.CustomEffectManager;
import com.jho5245.cucumbery.custom.customeffect.children.group.LocationCustomEffect;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectType;
import com.jho5245.cucumbery.custom.customeffect.type.CustomEffectTypeCustomMining;
import com.jho5245.cucumbery.events.block.CustomBlockBreakEvent;
import com.jho5245.cucumbery.util.additemmanager.AddItemUtil;
import com.jho5245.cucumbery.util.blockplacedata.BlockPlaceDataConfig;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.TPSMeter;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.CustomMaterial;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.data.custom_enchant.CustomEnchant;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import com.jho5245.cucumbery.util.storage.no_groups.SoundPlay;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.block.data.type.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MiningScheduler
{
	public static final HashMap<UUID, Integer> blockBreakKey = new HashMap<>();

	private static final Set<UUID> LIGHT_PENALTY_ALERT_SET = new HashSet<>();

	public static void customMining()
	{
		customMining(null, false);
	}

	public static void resetCooldowns(@NotNull World world)
	{
		HashMap<Location, Long> map = Variable.customMiningCooldown;
		map.keySet().removeIf(location ->
		{
			if (location.getWorld().getName().equals(world.getName()))
			{
				Variable.customMiningExtraBlocks.remove(location);
				if (Variable.customMiningExtraBlocksTask.containsKey(location))
				{
					Variable.customMiningExtraBlocksTask.get(location).cancel();
				}
				if (!Variable.fakeBlocks.containsKey(location))
				{
					location.getBlock().getState().update();
					BlockPlaceDataConfig.spawnItemDisplay(location);
				}
				else
				{
					Collection<? extends Player> players = Bukkit.getOnlinePlayers();
					for (Player player : players)
					{
						if (player.getWorld().getName().equals(location.getWorld().getName()) && location.distance(player.getLocation()) <= Cucumbery.config.getDouble(
								"custom-mining.maximum-block-packet-distance"))
						{
							player.sendBlockChange(location, Variable.fakeBlocks.get(location));
						}
					}
				}
				return true;
			}
			return false;
		});

		// 채광 모드 2 쿨타임 초기화
		HashMap<Location, BlockData> blockDataHashMap = Variable.customMiningMode2BlockData;
		blockDataHashMap.keySet().removeIf(location ->
		{
			if (location.getWorld().getName().equals(world.getName()))
			{
				location.getBlock().setBlockData(Variable.customMiningMode2BlockData.get(location), false);
				BlockPlaceDataConfig.spawnItemDisplay(location);
				return true;
			}
			return false;
		});
		HashMap<Location, BukkitTask> bukkitTaskHashMap = Variable.customMiningMode2BlockDataTask;
		bukkitTaskHashMap.keySet().removeIf(location ->
		{
			if (location.getWorld().getName().equals(world.getName()))
			{
				Variable.customMiningMode2BlockDataTask.get(location).cancel();
				BlockPlaceDataConfig.spawnItemDisplay(location);
				return true;
			}
			return false;
		});
	}

	public static void resetCooldowns(@NotNull Location from, Location to)
	{
		HashMap<Location, Long> map = Variable.customMiningCooldown;
		map.keySet().removeIf(location ->
		{
			int fromX = from.getBlockX(), fromY = from.getBlockY(), fromZ = from.getBlockZ();
			int toX = to.getBlockX(), toY = to.getBlockY(), toZ = to.getBlockZ();
			int minX = Math.min(fromX, toX), minY = Math.min(fromY, toY), minZ = Math.min(fromZ, toZ);
			int maxX = Math.max(fromX, toX), maxY = Math.max(fromY, toY), maxZ = Math.max(fromZ, toZ);
			int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
			if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
			{
				Variable.customMiningExtraBlocks.remove(location);
				if (Variable.customMiningExtraBlocksTask.containsKey(location))
				{
					Variable.customMiningExtraBlocksTask.get(location).cancel();
				}
				if (!Variable.fakeBlocks.containsKey(location))
				{
					location.getBlock().getState().update();
					BlockPlaceDataConfig.spawnItemDisplay(location);
				}
				else
				{
					Collection<? extends Player> players = Bukkit.getOnlinePlayers();
					for (Player player : players)
					{
						if (player.getWorld().getName().equals(location.getWorld().getName()) && location.distance(player.getLocation()) <= Cucumbery.config.getDouble(
								"custom-mining.maximum-block-packet-distance"))
						{
							player.sendBlockChange(location, Variable.fakeBlocks.get(location));
						}
					}
				}
				return true;
			}
			return false;
		});

		// 채광 모드 2 쿨타임 초기화
		HashMap<Location, BlockData> blockDataHashMap = Variable.customMiningMode2BlockData;
		blockDataHashMap.keySet().removeIf(location ->
		{
			int fromX = from.getBlockX(), fromY = from.getBlockY(), fromZ = from.getBlockZ();
			int toX = to.getBlockX(), toY = to.getBlockY(), toZ = to.getBlockZ();
			int minX = Math.min(fromX, toX), minY = Math.min(fromY, toY), minZ = Math.min(fromZ, toZ);
			int maxX = Math.max(fromX, toX), maxY = Math.max(fromY, toY), maxZ = Math.max(fromZ, toZ);
			int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
			if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
			{
				location.getBlock().setBlockData(Variable.customMiningMode2BlockData.get(location), false);
				BlockPlaceDataConfig.spawnItemDisplay(location);
				return true;
			}
			return false;
		});
		HashMap<Location, BukkitTask> bukkitTaskHashMap = Variable.customMiningMode2BlockDataTask;
		bukkitTaskHashMap.keySet().removeIf(location ->
		{
			int fromX = from.getBlockX(), fromY = from.getBlockY(), fromZ = from.getBlockZ();
			int toX = to.getBlockX(), toY = to.getBlockY(), toZ = to.getBlockZ();
			int minX = Math.min(fromX, toX), minY = Math.min(fromY, toY), minZ = Math.min(fromZ, toZ);
			int maxX = Math.max(fromX, toX), maxY = Math.max(fromY, toY), maxZ = Math.max(fromZ, toZ);
			int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
			if (x >= minX && x <= maxX && y >= minY && y <= maxY && z >= minZ && z <= maxZ)
			{
				Variable.customMiningMode2BlockDataTask.get(location).cancel();
				BlockPlaceDataConfig.spawnItemDisplay(location);
				return true;
			}
			return false;
		});
	}

	public static void customMiningTickAsync()
	{
		HashMap<Location, Long> map = Variable.customMiningCooldown;
		for (Location location : new ArrayList<>(map.keySet()))
		{
			if (map.containsKey(location))
			{
				long value = map.getOrDefault(location, 0L);
				map.put(location, value - 1);
			}
		}
		map.keySet().removeIf(location ->
		{
			if (map.get(location) <= 0 && !Variable.customMiningExtraBlocks.containsKey(location))
			{
				if (!Variable.fakeBlocks.containsKey(location))
				{
					Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
					{
						location.getBlock().getState().update();
						BlockPlaceDataConfig.spawnItemDisplay(location);
					}, 0L);
				}
				else
				{
					Collection<? extends Player> players = Bukkit.getOnlinePlayers();
					for (Player player : players)
					{
						if (player.getWorld().getName().equals(location.getWorld().getName()) && location.distance(player.getLocation()) <= Cucumbery.config.getDouble(
								"custom-mining.maximum-block-packet-distance"))
						{
							Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> player.sendBlockChange(location, Variable.fakeBlocks.get(location)), 0L);
						}
					}
				}
				return true;
			}
			return false;
		});
	}

	public static void customMining(@Nullable Player target, boolean distanceLimit)
	{
		HashMap<Location, Long> map = Variable.customMiningCooldown;
		for (Location location : new ArrayList<>(map.keySet()))
		{
			if (map.containsKey(location))
			{
				if (map.get(location) <= 0)
				{
					continue;
				}
				Collection<? extends Player> players = target == null ? Bukkit.getOnlinePlayers() : Collections.singletonList(target);
				players.forEach(p ->
				{
					if (p.getWorld().getName().equals(location.getWorld().getName()) && (!distanceLimit
							|| p.getLocation().distance(location) <= Cucumbery.config.getDouble("custom-mining.maximum-block-packet-distance")))
					{
						Block block = location.getBlock();
						Material blockType = block.getType();
						BlockData blockData = Bukkit.createBlockData(Material.AIR);
						if (blockType.isCollidable())
						{
							Material displayBlock = UserData.CUSTOM_MINING_COOLDOWN_DISPLAY_BLOCK.getMaterial(p);
							if (displayBlock == null)
							{
								displayBlock = Material.BEDROCK;
							}
							blockData = Bukkit.createBlockData(blockType == displayBlock ? Material.BEDROCK : displayBlock);
							if (Tag.STAIRS.isTagged(blockType) && blockType != Material.BLACKSTONE_STAIRS)
							{
								final BlockData finalBlockData = block.getBlockData();
								blockData = Bukkit.createBlockData(Material.BLACKSTONE_STAIRS, data ->
								{
									Stairs stairs = (Stairs) data;
									stairs.setShape(((Stairs) finalBlockData).getShape());
									stairs.setHalf(((Stairs) finalBlockData).getHalf());
									stairs.setFacing(((Stairs) finalBlockData).getFacing());
								});
							}
							if (Tag.SLABS.isTagged(blockType) && blockType != Material.BLACKSTONE_SLAB)
							{
								final BlockData finalBlockData = block.getBlockData();
								blockData = Bukkit.createBlockData(Material.BLACKSTONE_SLAB, data ->
								{
									Slab slab = (Slab) data;
									slab.setType(((Slab) finalBlockData).getType());
								});
							}
							if (Tag.WALLS.isTagged(blockType) && blockType != Material.BLACKSTONE_WALL)
							{
								final BlockData finalBlockData = block.getBlockData();
								blockData = Bukkit.createBlockData(Material.BLACKSTONE_WALL, data ->
								{
									Wall wall = (Wall) data;
									wall.setUp(((Wall) finalBlockData).isUp());
									for (BlockFace blockFace : BlockFace.values())
									{
										try
										{
											wall.setHeight(blockFace, ((Wall) finalBlockData).getHeight(blockFace));
										}
										catch (Exception ignored)
										{

										}
									}
								});
							}
							if (block.getBlockData() instanceof MultipleFacing multipleFacing)
							{
								if (multipleFacing instanceof Fence || multipleFacing instanceof GlassPane)
								{
									blockData = Bukkit.createBlockData(Material.BLACKSTONE_WALL);
								}
								else
								{
									blockData = Bukkit.createBlockData(Material.BEDROCK);
								}
							}
							if ((Tag.WALLS.isTagged(blockType) || Tag.STAIRS.isTagged(blockType) || Tag.SLABS.isTagged(blockType)
									|| block.getBlockData() instanceof MultipleFacing) && block.getBlockData() instanceof Waterlogged waterlogged)
							{
								((Waterlogged) blockData).setWaterlogged(waterlogged.isWaterlogged());
							}
						}
						if (Variable.customMiningExtraBlocks.containsKey(location))
						{
							blockData = Variable.customMiningExtraBlocks.get(location);
						}
						p.sendBlockChange(location, blockData);
					}
				});
			}
		}
		Collection<? extends Player> players = target == null ? Bukkit.getOnlinePlayers() : Collections.singletonList(target);
		players.forEach(p ->
		{
			for (Location location : Variable.customMiningExtraBlocks.keySet())
			{
				if (p.getWorld().getName().equals(location.getWorld().getName()) && (!distanceLimit || p.getLocation().distance(location) <= Cucumbery.config.getDouble(
						"custom-mining.maximum-block-packet-distance")))
				{
					p.sendBlockChange(location, Variable.customMiningExtraBlocks.get(location));
				}
			}
		});
	}

	public static void customMiningPre(@NotNull Player player)
	{
		String miner1Tag = Cucumbery.config.getString("custom-mining.tag", "cucumbery_miner");
		String miner2Tag = Cucumbery.config.getString("custom-mining.tag-2", "cucumbery_miner_2");
		String miner3Tag = Cucumbery.config.getString("custom-mining.tag-3", "cucumbery_miner_3");
		boolean wildMode = Cucumbery.config.getBoolean("custom-mining.enable-wild-mode");
		if (wildMode)
		{
			player.addScoreboardTag(miner1Tag);
			player.addScoreboardTag(miner3Tag);
		}
		if (Cucumbery.config.getBoolean("custom-mining.enable-by-tag"))
		{
			if (player.getScoreboardTags().contains(miner1Tag) && !CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE);
			}
			if (!player.getScoreboardTags().contains(miner1Tag) && CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE))
			{
				CustomEffectManager.removeEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE);
			}
			if (player.getScoreboardTags().contains(miner2Tag) && !CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2);
			}
			if (!player.getScoreboardTags().contains(miner2Tag) && CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2))
			{
				CustomEffectManager.removeEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2);
			}
			if (player.getScoreboardTags().contains(miner3Tag) && !CustomEffectManager.hasEffect(player,
					CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2_NO_RESTORE))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2_NO_RESTORE);
			}
			if (!player.getScoreboardTags().contains(miner3Tag) && CustomEffectManager.hasEffect(player,
					CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2_NO_RESTORE))
			{
				CustomEffectManager.removeEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2_NO_RESTORE);
			}
			if (player.getScoreboardTags().contains(Cucumbery.config.getString("custom-mining.predefined-ores-tag.dwarven-gold", "cucumbery_miner_dwarven_gold")))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PREDEFINED_CUSTOM_ORE_DWARVEN_GOLDS);
			}
			if (player.getScoreboardTags().contains(Cucumbery.config.getString("custom-mining.predefined-ores-tag.mithril", "cucumbery_miner_mithril")))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PREDEFINED_CUSTOM_ORE_MITHRILS);
			}
			if (player.getScoreboardTags().contains(Cucumbery.config.getString("custom-mining.predefined-ores-tag.titanium", "cucumbery_miner_titanium")))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PREDEFINED_CUSTOM_ORE_TITANIUMS);
			}
			if (player.getScoreboardTags().contains(Cucumbery.config.getString("custom-mining.predefined-ores-tag.gemstone", "cucumbery_miner_gemstone")))
			{
				CustomEffectManager.addEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PREDEFINED_CUSTOM_ORE_GEMSTONES);
			}
		}
	}

	public static void customMining(@NotNull Player player)
	{
		if (CustomEffectManager.getEffectNullable(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PROGRESS) instanceof LocationCustomEffect effect)
		{
			final Location location = effect.getLocation();
			// 블록 채굴 시 아이템을 드롭할 위치
			final Location dropLocation = new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 0.5, location.getBlockZ() + 0.5);
			UUID uuid = player.getUniqueId();
			Block block = location.getBlock();
			final Material blockType = block.getType();
			if (blockType.isAir())
			{
				MiningManager.quitCustomMining(player);
				return;
			}
			final BlockData originData = block.getBlockData().clone();
			final Location locationClone = location.clone();
			ItemStack toolItemStack = player.getInventory().getItemInMainHand().clone();
			CustomMaterial toolCustomMaterial = CustomMaterial.itemStackOf(toolItemStack);
			boolean toolIsDrill = toolCustomMaterial != null && toolCustomMaterial.isDrill();
			ItemMeta itemMeta = toolItemStack.getItemMeta();
			// 드릴 연료 0 이하일 시 사용 불가 처리
			if (toolIsDrill && itemMeta instanceof Damageable damageable)
			{
				int currentDurability = damageable.hasDamage() ? damageable.getDamage() : 0;
				int maxDurability = damageable.hasMaxDamage() ? damageable.getMaxDamage() : 0;
				if (currentDurability >= maxDurability)
				{
					MessageUtil.sendWarn(player, "%s의 연료가 다 떨어져서 사용할 수 없습니다. 모루에서 드릴과 드릴 연료를 사용하여 연료를 충전할 수 있습니다.", toolItemStack);
					MiningManager.quitCustomMining(player);
					return;
				}
			}
			MiningResult miningResult = MiningManager.getMiningInfo(player, location);
			// 채굴 정보가 없거나(쿨타임, 지역 보호 등) 블록을 캘 수 없는 상태(해당 도구의 해당 블록 채광 미지원, 블록의 강도가 -1) 처리
			if (miningResult == null || !miningResult.canMine() || miningResult.blockHardness() == -1)
			{
				MiningManager.quitCustomMining(player);
				return;
			}
			// 블록 채굴 대기시간 동안 채굴 불가
			if (Variable.customMiningBlockBreakCooldown.contains(uuid))
			{
				MiningManager.quitCustomMining(player);
				return;
			}
			List<ItemStack> drops = miningResult.drops();
			CustomMaterial customMaterial = drops.isEmpty() ? null : CustomMaterial.itemStackOf(drops.getFirst());
			// sus
			boolean isSUS = !drops.isEmpty() && CustomMaterial.itemStackOf(drops.getFirst()) == CustomMaterial.SUS;
			if (miningResult.blockTier() > miningResult.miningTier())
			{
				if (miningResult.miningTier() > 0 && !Variable.customMiningTierAlertCooldown.contains(uuid))
				{
					Object blockInfo = null;
					if (!drops.isEmpty())
					{
						blockInfo = drops.getFirst();
					}
					else if (BlockPlaceDataConfig.getItem(location) != null)
					{
						blockInfo = BlockPlaceDataConfig.getItem(location);
					}
					if (blockInfo == null)
					{
						blockInfo = block.getType();
					}
					MessageUtil.sendWarn(player, "%s을(를) 캐기 위해 더 높은 등급의 도구가 필요합니다!", blockInfo);
					Variable.customMiningTierAlertCooldown.add(uuid);
					Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.customMiningTierAlertCooldown.remove(uuid), 20L);
				}
				MiningManager.quitCustomMining(player);
				return;
			}
			// 채굴 진행도 처리
			double origin = Variable.customMiningProgress.getOrDefault(uuid, 0d);
			double damage = miningResult.miningSpeed() / 20f / miningResult.blockHardness();
			float tps = (float) Math.max(1, Math.min(20, TPSMeter.getTPS()));
			float serverTickRate = Bukkit.getServerTickManager().getTickRate();
			float lagMultiplier = 20f / tps * (serverTickRate / 20f);
			Variable.customMiningProgress.put(uuid, origin + damage * (tps > 16f ? 1 : lagMultiplier));
			double progress = Variable.customMiningProgress.getOrDefault(uuid, 0d);
			progress = Math.max(0f, Math.min(1f, progress));
			// 블록이 캐짐
			boolean instaBreak = miningResult.blockHardness() == Double.MIN_VALUE;
			if (progress >= 1 || instaBreak) // insta break
			{
				// 이벤트 호출
				CustomBlockBreakEvent customBlockBreakEvent = new CustomBlockBreakEvent(block, player);
				Bukkit.getPluginManager().callEvent(customBlockBreakEvent);
				boolean mode2 = CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2) || miningResult.overrideMode2();
				boolean mode3 = CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_2_NO_RESTORE);
				// 블록을 캤을 때 소리 재생 및 채광 모드 2/미복구일 경우 블록 파괴 파티클 처리
				{
					SoundGroup soundGroup = block.getBlockSoundGroup();
					if (customMaterial != null)
					{
						switch (customMaterial)
						{
							case RUBY, AMBER, JADE, SAPPHIRE, TOPAZ, MORGANITE -> soundGroup = Material.AMETHYST_CLUSTER.createBlockData().getSoundGroup();
						}
					}
					if (Variable.customMiningExtraBlocks.containsKey(location))
					{
						soundGroup = Variable.customMiningExtraBlocks.get(location).getSoundGroup();
					}
					if (Variable.fakeBlocks.containsKey(location))
					{
						soundGroup = Variable.fakeBlocks.get(location).getSoundGroup();
					}
					Sound breakSound = miningResult.breakSound();
					String breakCustomSound = miningResult.breakCustomSound();
					float volume = 1f, pitch = soundGroup.getPitch() * 0.8f;
					if (miningResult.breakSoundVolume() != 0f)
					{
						volume = miningResult.breakSoundVolume();
					}
					if (miningResult.breakSoundPitch() != 0f)
					{
						pitch = miningResult.breakSoundPitch();
					}
					Sound sound = breakSound != null ? breakSound : soundGroup.getBreakSound();
					// 불 블록은 전용 소리 재생
					if (block.getType() == Material.FIRE && BlockPlaceDataConfig.getItem(location) == null)
					{
						sound = Sound.BLOCK_FIRE_EXTINGUISH;
						pitch = 2f;
					}
					for (Player online : Bukkit.getOnlinePlayers())
					{
						if (online.getWorld().getName().equals(player.getWorld().getName()))
						{
							if (breakCustomSound != null && UserData.SERVER_RESOURCEPACK.getBoolean(online))
							{
								online.playSound(locationClone, breakCustomSound, SoundCategory.BLOCKS, volume, pitch);
							}
							else
							{
								online.playSound(locationClone, sound, SoundCategory.BLOCKS, volume, pitch);
							}
						}
					}
					// 채굴 모드 2, 3은 실제로 블록이 부숴졌으므로 블록이 부서지는 입자 출력
					if (mode2 || mode3)
					{
						@Nullable BlockData blockData = block.getBlockData();
						if (Variable.fakeBlocks.containsKey(location))
						{
							blockData = Variable.fakeBlocks.get(location);
						}
						String breakParticle = miningResult.breakParticle();
						ItemStack breakParticleItem = new ItemStack(Material.STONE);
						if (breakParticle != null)
						{
							if (breakParticle.startsWith("block:"))
							{
								try
								{
									blockData = Bukkit.createBlockData(breakParticle.substring(6));
								}
								catch (Exception e)
								{
									MessageUtil.sendWarn(Bukkit.getConsoleSender(), "커스텀 채광 처리 도중 잘못된 블록 데이터 감지! " + breakParticle);
								}
							}
							else if (breakParticle.startsWith("item:"))
							{
								blockData = null;
								try
								{
									breakParticleItem = Bukkit.getItemFactory().createItemStack(breakParticle.substring(5));
								}
								catch (Exception e)
								{
									MessageUtil.sendWarn(Bukkit.getConsoleSender(), "커스텀 채광 처리 도중 잘못된 아이템 감지! " + breakParticle);
								}
							}
						}
						VoxelShape voxelShape = block.getCollisionShape();
						Collection<BoundingBox> boundingBoxes = voxelShape.getBoundingBoxes();
						if (boundingBoxes.isEmpty())
						{
							switch (blockType)
							{
								case REDSTONE_WIRE -> boundingBoxes.add(new BoundingBox(0d, 0d, 0d, 1d, 0.0625d, 1d));
								case SNOW ->
								{
									Snow snow = (Snow) block.getBlockData();
									boundingBoxes.add(new BoundingBox(0d, 0d, 0d, 1d, 0.0625d * snow.getLayers(), 1d));
								}
							}
							if (Tag.FLOWERS.isTagged(blockType))
							{
								boundingBoxes.add(new BoundingBox(0.3d, 0.3d, 0.3d, 1d, 0.0625d, 1d));
							}
						}
						if (boundingBoxes.isEmpty())
						{
							boundingBoxes.add(block.getBoundingBox());
							boundingBoxes.add(new BoundingBox(0d, 0d, 0d, 1d, 1d, 1d));
						}
						// 불 블록은 파괴 입자 처리 안함
						if (block.getType() == Material.FIRE && BlockPlaceDataConfig.getItem(location) == null)
							boundingBoxes.clear();
						for (Player online : Bukkit.getOnlinePlayers())
						{
							if (UserData.SHOW_BLOCK_BREAK_PARTICLE_ON_CUSTOM_MINING.getBoolean(online) && online.getWorld().getName().equals(player.getWorld().getName()))
							{
								for (BoundingBox boundingBox : boundingBoxes)
								{
									double minX = boundingBox.getMinX(), maxX = boundingBox.getMaxX();
									double minY = boundingBox.getMinY(), maxY = Math.min(1d, boundingBox.getMaxY());
									double minZ = boundingBox.getMinZ(), maxZ = boundingBox.getMaxZ();
									double diffX = (maxX - minX) * 8d / 30d, diffY = (maxY - minY) * 8d / 30d, diffZ = (maxZ - minZ) * 8d / 30d;
									minX += diffX * 0.1;
									minY += diffY * 0.1;
									minZ += diffZ * 0.1;

									Particle particle = blockData == null ? Particle.ITEM : Particle.BLOCK;
									Object data = blockData == null ? breakParticleItem : blockData;

									for (int a = 0; a < 4 * Math.max(0.5, maxX - minX); a++)
									{
										for (int b = 0; b < 4 * Math.max(0.5, maxY - minY); b++)
										{
											for (int c = 0; c < 4 * Math.max(0.5, maxZ - minZ); c++)
											{
												online.spawnParticle(particle, location.clone().add(minX + diffX * a, minY + diffY * b, minZ + diffZ * c), 1, 0, 0, 0, 0.2, data);
											}
										}
									}
								}
							}
						}
					}
				}
				// 블록을 캤을 때 블록 통계 처리
				{
					player.incrementStatistic(Statistic.MINE_BLOCK, blockType);
				}
				// 블록을 캤을 때 배고픔 감소 처리
				{
					player.setExhaustion(player.getExhaustion() + 0.005f);
				}
				// 블록 드롭 처리(염력 인챈트나 효과가 있으면 인벤토리에 지급 혹은 블록 위치에 아이템 떨굼
				{
					// 블록 드롭 컬렉션이 비어있지 않을 경우에만 드롭 아이템 지급 작업 수행
					if (!drops.isEmpty())
					{
						boolean hasTelekinesis =
								CustomEnchant.isEnabled() && itemMeta != null && itemMeta.hasEnchant(CustomEnchant.TELEKINESIS) || CustomEffectManager.hasEffect(player,
										CustomEffectType.TELEKINESIS);
						if (hasTelekinesis)
						{
							AddItemUtil.addItem(player, drops);
						}
						else
						{
							for (ItemStack item : drops)
							{
								player.getWorld().dropItemNaturally(dropLocation, item);
							}
						}
					}
					if (mode3 && block.getState() instanceof BlockInventoryHolder inventoryHolder && !(inventoryHolder instanceof ShulkerBox))
					{
						Inventory inventory = inventoryHolder.getInventory();
						if (inventory instanceof DoubleChestInventory doubleChestInventory && block.getBlockData() instanceof Chest chest)
						{
							switch (chest.getType())
							{
								case LEFT -> inventory = doubleChestInventory.getRightSide();
								case RIGHT -> inventory = doubleChestInventory.getLeftSide();
							}
						}
						for (ItemStack content : inventory.getContents())
						{
							if (ItemStackUtil.itemExists(content))
							{
								//								if (hasTelekinesis)
								//								{
								//									AddItemUtil.addItem(player, content);
								//								}
								//								else
								{
									player.getWorld().dropItemNaturally(dropLocation, content);
								}
							}
						}
					}
				}
				// 경험치 드롭 처리
				{
					double exp = miningResult.expToDrop();
					int intSide = (int) exp;
					double doubleSide = exp - intSide;
					if (Math.random() < doubleSide)
					{
						intSide++;
					}
					int finalIntSide = intSide;
					if ((!drops.isEmpty() || Arrays.asList(Material.SPAWNER, Material.SCULK, Material.SCULK_CATALYST, Material.SCULK_SENSOR, Material.SCULK_SHRIEKER)
							.contains(block.getType())) && finalIntSide > 0)
					{
						player.getWorld()
								.spawnEntity(location, EntityType.EXPERIENCE_ORB, SpawnReason.CUSTOM, (entity -> ((ExperienceOrb) entity).setExperience(finalIntSide)));
					}
				}
				// 채굴 모드 처리
				{
					// 채굴 모드 2, 3 처리
					if (mode2 || mode3)
					{
						if (mode3)
						{
							Block block1 = locationClone.add(0, -1, 0).getBlock();
							Material block1Type = block1.getType();
							if ((block.getType() == Material.ICE && (block1Type.isSolid() || block1Type == Material.WATER) && drops.isEmpty() && customMaterial == null) || (
									block.getBlockData() instanceof Waterlogged waterlogged && waterlogged.isWaterlogged()))
							{

								block.setType(Material.WATER);
							}
							else
							{
								block.setType(Material.AIR);
							}
							BlockPlaceDataConfig.removeData(location);
							Variable.fakeBlocks.remove(location);
							//              Scheduler.fakeBlocksAsync(null, location, true); commented since 2022.11.08 due to no reason for calling it
						}
						else
						{
							if (Variable.customMiningMode2BlockDataTask.containsKey(locationClone))
							{
								Variable.customMiningMode2BlockDataTask.get(locationClone).cancel();
							}
							Variable.customMiningMode2BlockData.put(locationClone, originData);
							BlockPlaceDataConfig.despawnItemDisplay(locationClone);
							boolean isWater = originData instanceof Waterlogged waterlogged && waterlogged.isWaterlogged();
							block.setBlockData(Bukkit.createBlockData(isWater ? Material.WATER : Material.AIR), false);
							Variable.customMiningMode2BlockDataTask.put(locationClone, Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
							{
								Variable.customMiningMode2BlockData.remove(locationClone);
								customMining();
								if (isWater && block.getType() == Material.WATER || !isWater && block.getType().isAir())
								{
									block.setBlockData(originData, false);
								}
								BlockPlaceDataConfig.spawnItemDisplay(locationClone);
								// 채광 모드 2에서는 재생 속도가 느려짐
							}, (long) (miningResult.regenCooldown() * Cucumbery.config.getDouble("custom-mining.mining-mode-2-regen-multiplier"))));
						}
					}
					// 채굴 모드 처리
					else
					{
						BlockPlaceDataConfig.despawnItemDisplay(locationClone);
						boolean extraBlockIgnoreCooldown = false;
						if (Variable.customMiningExtraBlocks.containsKey(locationClone))
						{
							extraBlockIgnoreCooldown = true;
							if (Variable.customMiningExtraBlocksTask.containsKey(locationClone))
							{
								Variable.customMiningExtraBlocksTask.remove(locationClone).cancel();
							}
							Variable.customMiningExtraBlocks.remove(locationClone);
							customMining();
						}
						else if (!drops.isEmpty() && !drops.getFirst().getType().isAir())
						{
							if (customMaterial != null)
							{
								switch (customMaterial)
								{
									case TUNGSTEN_INGOT, TUNGSTEN_ORE ->
									{
										if (CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_PREDEFINED_CUSTOM_ORE_CUCUMBERITES))
										{
											if (Math.random() > 0.7)
											{
												Variable.customMiningExtraBlocks.put(locationClone, Bukkit.createBlockData(Material.GREEN_WOOL));
												if (Variable.customMiningExtraBlocksTask.containsKey(locationClone))
												{
													Variable.customMiningExtraBlocksTask.remove(locationClone).cancel();
												}
												customMining();
											}
										}
									}
								}
							}
							else
							{
								boolean hasAllowedBlocks = CustomEffectManager.hasEffect(player, CustomEffectTypeCustomMining.CUSTOM_MINING_SPEED_MODE_ALLOWED_BLOCKS);
								if (hasAllowedBlocks)
								{
									final BlockData blockData = block.getBlockData();
									switch (block.getType())
									{
										case STONE_STAIRS ->
										{
											Variable.customMiningExtraBlocks.put(locationClone, Bukkit.createBlockData(Material.COBBLESTONE_STAIRS, data ->
											{
												Stairs stairs = (Stairs) data;
												stairs.setFacing(((Stairs) blockData).getFacing());
												stairs.setHalf(((Stairs) blockData).getHalf());
												stairs.setShape(((Stairs) blockData).getShape());
											}));
											if (Variable.customMiningExtraBlocksTask.containsKey(locationClone))
											{
												Variable.customMiningExtraBlocksTask.remove(locationClone).cancel();
											}
											customMining();
										}
										case STONE_SLAB ->
										{
											Variable.customMiningExtraBlocks.put(locationClone, Bukkit.createBlockData(Material.COBBLESTONE_SLAB, data ->
											{
												Slab slab = (Slab) data;
												slab.setType(((Slab) blockData).getType());
											}));
											if (Variable.customMiningExtraBlocksTask.containsKey(locationClone))
											{
												Variable.customMiningExtraBlocksTask.remove(locationClone).cancel();
											}
											customMining();
										}
									}
								}
								switch (block.getType())
								{
									case STONE ->
									{
										Variable.customMiningExtraBlocks.put(locationClone, Bukkit.createBlockData(Material.COBBLESTONE));
										if (Variable.customMiningExtraBlocksTask.containsKey(locationClone))
										{
											Variable.customMiningExtraBlocksTask.remove(locationClone).cancel();
										}
										customMining();
									}
									case DEEPSLATE ->
									{
										Variable.customMiningExtraBlocks.put(locationClone, Bukkit.createBlockData(Material.COBBLED_DEEPSLATE));
										if (Variable.customMiningExtraBlocksTask.containsKey(locationClone))
										{
											Variable.customMiningExtraBlocksTask.remove(locationClone).cancel();
										}
										customMining();
									}
								}
							}
						}
						if (Variable.customMiningExtraBlocks.containsKey(locationClone))
						{
							int cooldown = Cucumbery.config.getInt("custom-mining.cooldown-extra-block-removal");
							if (cooldown > 0)
							{
								Variable.customMiningExtraBlocksTask.put(locationClone, Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () ->
								{
									Variable.customMiningExtraBlocks.remove(locationClone);
									customMining();
								}, cooldown));
							}
						}
						if (!extraBlockIgnoreCooldown)
						{
							Variable.customMiningCooldown.put(locationClone, (long) miningResult.regenCooldown());
							customMining();
						}
					}
				}
				// 블록 파괴 진행도 초기화와 채굴 진행도 효과 제거
				MiningManager.quitCustomMining(player);
				// 채굴에 사용된 도구 내구도 처리 및 드릴의 연료 경고 처리(즉시 부숴지는 블록은 내구도가 깎이지 않음)
				boolean dropDura = false;
				if (!instaBreak && ItemStackUtil.itemExists(toolItemStack) && !toolItemStack.getItemMeta().isUnbreakable())
				{
					int currentDurability = ((Damageable) toolItemStack.getItemMeta()).getDamage();
					int maxDurability = toolItemStack.getType().getMaxDurability();
					if (toolItemStack.getItemMeta() instanceof Damageable damageable && damageable.hasMaxDamage())
					{
						maxDurability = damageable.getMaxDamage();
					}
					if (maxDurability > 0)
					{
						int unbreakingLevel = toolItemStack.getEnchantmentLevel(Enchantment.UNBREAKING);
						if (Math.random() >= 1d * unbreakingLevel / (unbreakingLevel + 1))
						{
							currentDurability++;
							dropDura = true;
						}
						if (currentDurability >= maxDurability && !toolIsDrill)
						{
							NBT.getComponents(toolItemStack, nbt ->
							{
								Sound sound = Sound.ENTITY_ITEM_BREAK;
								if (nbt.hasTag("break_sound"))
								{
									String s = nbt.getString("break_sound");
									if (s != null)
									{
										sound = Registry.SOUNDS.getOrThrow(NamespacedKey.minecraft(s));
									}
									ReadableNBT compound = nbt.getCompound("break_sound");
									if (compound != null)
									{
										String soundId = compound.getString("sound_id");
										sound = Registry.SOUNDS.getOrThrow(NamespacedKey.minecraft(soundId));
									}
								}
								player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, 1F, 1F);
							});
							player.incrementStatistic(Statistic.BREAK_ITEM, toolItemStack.getType());
							player.spawnParticle(Particle.ITEM, player.getEyeLocation().add(0, -0.5, 0), 30, 0, 0, 0, 0.1, toolItemStack);
							PlayerItemBreakEvent playerItemBreakEvent = new PlayerItemBreakEvent(player, toolItemStack);
							Bukkit.getPluginManager().callEvent(playerItemBreakEvent);
							toolItemStack.setAmount(toolItemStack.getAmount() - 1);
						}
						else
						{
							Damageable damageable = (Damageable) itemMeta;
							damageable.setDamage(currentDurability);
							toolItemStack.setItemMeta(damageable);
						}
						player.getInventory().setItemInMainHand(toolItemStack);
						if (dropDura && toolIsDrill)
						{
							double ratio = (maxDurability - currentDurability) * 1d / maxDurability;
							if (ratio == 0.5 || ratio == 0.2 || ratio == 0.1 || ratio == 0.05 || ratio == 0.01)
							{
								MessageUtil.info(player, "%s의 연료가 %s%% 남았습니다. 모루에서 드릴과 드릴 연료를 사용하여 연료를 충전할 수 있습니다.", toolItemStack, Constant.Jeongsu.format(ratio * 100));
								SoundPlay.playSound(player, Sound.UI_BUTTON_CLICK);
							}
							if (ratio == 0)
							{
								MessageUtil.info(player, "%s의 연료가 다 떨어졌습니다. 모루에서 드릴과 드릴 연료를 사용하여 연료를 충전할 수 있습니다.", toolItemStack);
								SoundPlay.playSound(player, Sound.UI_BUTTON_CLICK);
							}
						}
					}
				}
				// 채굴에 사용된 도구 통계 처리
				if (ItemStackUtil.itemExists(toolItemStack))
				{
					player.incrementStatistic(Statistic.USE_ITEM, toolItemStack.getType());
				}
				// sus
				if (isSUS)
				{
					location.clone().getWorld().spawnParticle(Particle.EXPLOSION, location.clone().add(0.5, 0.5, 0.5), 1);
				}
				// 즉시 부서지는 블록이 아닐 경우 다음 블록을 부수기까지 짧은 쿨타임 추가
				if (miningResult.miningSpeed() < miningResult.blockHardness() * 20d)
				{
					Variable.customMiningBlockBreakCooldown.add(uuid);
					Bukkit.getScheduler().runTaskLater(Cucumbery.getPlugin(), () -> Variable.customMiningBlockBreakCooldown.remove(uuid), 6L);
				}
				// 만약 블록이 캐졌을 때 플레이어 위치가 어두울 경우 다음 블록 채광부터 채광 속도 감소 페널티 적용
				{
					int penaltyRatio = UserData.MINING_SPEED_RATIO_MODIFIER_LIGHT_PENALTY.getInt(player);
					Block eyeLocationBlock = player.getEyeLocation().getBlock();
					// 야간투시가 없고 어두울 경우 페널티 수치 증가
					if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION) && eyeLocationBlock.getLightFromSky() == 0 && eyeLocationBlock.getLightFromBlocks() == 0
							&& eyeLocationBlock.getLightLevel() == 0)
					{
						// 첫 페널티 적용일 경우 메시지 표시
						if (!LIGHT_PENALTY_ALERT_SET.contains(uuid))
						{
							MessageUtil.sendWarn(player, "어두운 곳에서 채광을 지속할 경우 채광 속도가 감소합니다! 주변을 밝게 해주세요!");
							LIGHT_PENALTY_ALERT_SET.add(uuid);
							if (UserData.IGNORE_MINING_SPEED_RATIO_MODIFIER_LIGHT_PENALTY.getBoolean(player))
							{
								MessageUtil.info(player, "%s 옵션이 켜져 있어 실제로 감소하지 않습니다", UserData.IGNORE_MINING_SPEED_RATIO_MODIFIER_LIGHT_PENALTY);
							}
						}
						UserData.MINING_SPEED_RATIO_MODIFIER_LIGHT_PENALTY.set(player, Math.min(80, penaltyRatio + 5));
					}
					// 아닐 경우 페널티 수치 감소
					else
					{
						UserData.MINING_SPEED_RATIO_MODIFIER_LIGHT_PENALTY.set(player, Math.max(0, penaltyRatio - 10));
					}

				}
				return;
			}
			// 블록이 캐지는 중
			if (progress > 0.01)
			{
				if (!MiningScheduler.blockBreakKey.containsKey(uuid))
				{
					int random;
					do
					{
						random = (int) (Math.random() * Integer.MAX_VALUE);
					}
					while (MiningScheduler.blockBreakKey.containsValue(random));
					MiningScheduler.blockBreakKey.put(uuid, random);
				}
				if (isSUS && progress > 0.5f)
				{
					progress = 1f - progress;
				}
				double finalProgress = progress;
				player.getWorld().getPlayers().forEach(p -> p.sendBlockDamage(location, (float) finalProgress, blockBreakKey.get(uuid)));
			}
		}
	}
}
