package com.jho5245.cucumbery.util.blockplacedata;

import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.utility.MinecraftReflection;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.google.common.collect.Lists;
import com.google.errorprone.annotations.Var;
import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.*;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Variable;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.*;

public class BlockPlaceDataConfig extends ChunkConfig
{
	protected static final HashMap<String, BlockPlaceDataConfig> MAP = new HashMap<>();

	protected BlockPlaceDataConfig(@NotNull Chunk chunk, boolean createNew)
	{
		super(chunk, "block-place-data", createNew);
		MAP.put(getKey(), this);
	}

	/**
	 * 이거 1분마다 호출해야함 파일 IO임
	 */
	public static void saveAll()
	{
		List<String> keySet = new ArrayList<>(MAP.keySet());
		keySet.forEach(s ->
		{
			BlockPlaceDataConfig config = MAP.get(s);
			if (config != null)
			{
				config.save();
			}
		});
		MAP.keySet().removeIf(s -> !MAP.get(s).getChunk().isLoaded());
	}

	public void save()
	{
		saveConfig();
		MAP.remove(getKey());
	}

	public static void save(@NotNull Chunk chunk)
	{
		if (!MAP.containsKey(getKey(chunk)))
		{
			return;
		}
		MAP.get(getKey(chunk)).save();
	}

	public static BlockPlaceDataConfig getInstance(@NotNull Chunk chunk)
	{
		return getInstance(chunk, false);
	}

	public static BlockPlaceDataConfig getInstance(@NotNull Chunk chunk, boolean createNew)
	{
		String key = getKey(chunk);
		if (!MAP.containsKey(key))
		{
			new BlockPlaceDataConfig(chunk, createNew);
		}
		return MAP.get(key);
	}

	public static void removeData(@NotNull Location location)
	{
		BlockPlaceDataConfig blockPlaceDataConfig = BlockPlaceDataConfig.getInstance(location.getChunk());
		if (blockPlaceDataConfig != null)
		{
			blockPlaceDataConfig.set(location, null);
		}
		despawnItemDisplay(new ArrayList<>(Bukkit.getOnlinePlayers()), location);
	}

	@Nullable
	public static ItemStack getItem(@NotNull Location location)
	{
		return getItem(location, null);
	}

	@Nullable
	public static ItemStack getItem(@NotNull Location location, @Nullable CommandSender sender)
	{
		BlockPlaceDataConfig blockPlaceDataConfig = BlockPlaceDataConfig.getInstance(location.getChunk());
		if (blockPlaceDataConfig != null)
		{
			return blockPlaceDataConfig.getItemStack(location, sender);
		}
		return null;
	}

	public static void setItem(@NotNull Location location, @NotNull ItemStack itemStack)
	{
		getInstance(location.getChunk(), true).set(location, itemStack);
	}

	/**
	 * 해당 위치에 저장된 값의 문자열 형태를 반환
	 *
	 * @param location
	 * 		참조할 위치
	 * @return 저장된 값 혹은 없으면 <code>null</code>
	 */
	@Nullable
	public String getRawData(@NotNull Location location)
	{
		return getConfig().getString(locationToString(location));
	}

	/**
	 * 해당 위치에 저장된 값의 아이템 형태를 반환
	 * <p>만약에 아이템이 없거나 아이템에 플레이스 홀더가 지정되어 있을 경우 공기가 반환될 수 있음
	 * <p>따라서 항상 아이템을 반환함
	 *
	 * @param location
	 * 		참조할 위치
	 * @return 저장된 아이템
	 */
	@Nullable
	public ItemStack getItemStack(@NotNull Location location)
	{
		return getItemStack(location, null);
	}

	/**
	 * 해당 위치에 저장된 값의 아이템 형태를 반환
	 * <p>만약에 아이템이 없거나 아이템에 플레이스 홀더가 지정되어 있을 경우 공기가 반환될 수 있음
	 * <p>따라서 항상 아이템을 반환함
	 *
	 * @param location
	 * 		참조할 위치
	 * @param sender
	 * 		플레이스 홀더를 분석할 대상({@link Player} 혹은 {@link Bukkit#getConsoleSender()} 사용 가능), null을 입력하여 분석하지 않음
	 * @return 저장된 아이템 혹은 null
	 */
	@Nullable
	public ItemStack getItemStack(@NotNull Location location, @Nullable CommandSender sender)
	{
		String data = getConfig().getString(locationToString(location));
		if (sender != null && data != null && data.length() - data.replace("%", "").length() >= 2)
		{
			data = PlaceHolderUtil.placeholder(sender, data, null);
		}
		ItemStack itemStack = ItemSerializer.deserialize(data);
		return itemStack.getType().isAir() ? null : itemStack;
	}

	/**
	 * 해당 위치에 아이템을 저장함
	 *
	 * @param location
	 * 		저장할 위치
	 * @param o
	 * 		문자열 혹은 아이템 또는 <code>null</code>을 입력하여 삭제
	 */
	public void set(@NotNull Location location, @Nullable Object o)
	{
		String value = o instanceof ItemStack itemStack ? ItemSerializer.serialize(itemStack) : o == null ? null : o.toString();
		getConfig().set(locationToString(location), value);
	}

	public static final HashMap<String, Set<Integer>> ITEM_DISPLAY_MAP = new HashMap<>();

	public static void spawnItemDisplay(@NotNull Location location)
	{
		spawnItemDisplay(new ArrayList<>(Bukkit.getOnlinePlayers()), location);
	}

	public static void spawnItemDisplay(@NotNull Player player, @NotNull Location location)
	{
		spawnItemDisplay(Collections.singletonList(player), location);
	}

	private static final float[][] offsets = {
			{
					0f,
					0f,
					0f
			},
			{
					-0.25f,
					0.5f,
					-0.25f
			},
			{
					-0.25f,
					0.5f,
					0.25f
			},
			{
					0.25f,
					0.5f,
					-0.25f
			},
			{
					0.25f,
					0.5f,
					0.25f
			},
			{
					-0.25f,
					0f,
					-0.25f
			},
			{
					-0.25f,
					0f,
					0.25f
			},
			{
					0.25f,
					0f,
					-0.25f
			},
			{
					0.25f,
					0f,
					0.25f
			}
	};

/*  {0.25f, 0.5f, 0.25f},
  {0.25f, 0.5f, -0.25f},
  {-0.25f, 0.5f, 0.25f},
  {-0.25f, 0.5f, -0.25f},
  {0.25f, 0f, 0.25f},
  {0.25f, 0f, -0.25f},
  {-0.25f, 0f, 0.25f},
  {-0.25f, 0f, -0.25f}*/

	private static void _spawnItemDisplay(@NotNull Collection<Player> players, @NotNull Location location, @NotNull NBTCompound nbtCompound)
	{
		if (!nbtCompound.hasTag("type"))
		{
			return;
		}
		if (!nbtCompound.hasTag("value"))
		{
			return;
		}
		String type = nbtCompound.getString("type");
		if (type == null)
		{
			return;
		}
		EntityType entityType = switch (type)
		{
			case "item", "player_head", "player_heads" -> EntityType.ITEM_DISPLAY;
			case "block" -> EntityType.BLOCK_DISPLAY;
			case "text" -> EntityType.TEXT_DISPLAY;
			default -> null;
		};
		if (entityType == null)
		{
			return;
		}
		if ("player_heads".equals(type) && nbtCompound.getType("value") != NBTType.NBTTagList)
		{
			return;
		}
		else if (!"player_heads".equals(type) && nbtCompound.getType("value") != NBTType.NBTTagString)
		{
			return;
		}
		if ("player_heads".equals(type))
		{
			NBTList<String> values = nbtCompound.getStringList("value");
			if (values.size() != 8)
			{
				return;
			}
			for (int i = 0; i < 8; i++)
			{
				String value = values.get(i);
				_spawnItemDisplay(players, location, entityType, nbtCompound, type, value, i);
			}
		}
		else
		{
			String value = nbtCompound.getString("value");
			_spawnItemDisplay(players, location, entityType, nbtCompound, type, value, -1);
		}
	}

	private static void _spawnItemDisplay(@NotNull Collection<Player> players, Location location, EntityType entityType, NBTCompound nbtItem, String type,
			String value, int modifier)
	{
		float[] offset;
		if (modifier == -1)
		{
			offset = offsets[0];
		}
		else
		{
			offset = offsets[modifier + 1];
		}

		Integer interpolation_delay =
				nbtItem.hasTag("interpolation_delay") && nbtItem.getType("interpolation_delay") == NBTType.NBTTagInt ? nbtItem.getInteger("interpolation_delay") : null;
		Integer transformation_interpolation_duration =
				nbtItem.hasTag("transformation_interpolation_duration") && nbtItem.getType("transformation_interpolation_duration") == NBTType.NBTTagInt
						? nbtItem.getInteger("transformation_interpolation_duration")
						: null;
		Integer position_rotation_interpolation_duration =
				nbtItem.hasTag("position_rotation_interpolation_duration") && nbtItem.getType("position_rotation_interpolation_duration") == NBTType.NBTTagInt
						? nbtItem.getInteger("position_rotation_interpolation_duration")
						: null;

		NBTList<Float> rotation = nbtItem.getFloatList("rotation");
		float rotationX = rotation != null && rotation.size() == 2 ? rotation.get(0) : 0f, rotationY =
				rotation != null && rotation.size() == 2 ? rotation.get(1) : 0f;

		NBTCompound transformation = nbtItem.getOrCreateCompound("transformation");

		NBTList<Float> translation = transformation.getFloatList("translation");
		float translationX = translation != null && translation.size() == 3 ? translation.get(0) : 0f, translationY =
				translation != null && translation.size() == 3 ? translation.get(1) : 0f, translationZ =
				translation != null && translation.size() == 3 ? translation.get(2) : 0f;

		NBTList<Float> scale = transformation.getFloatList("scale");
		float scaleX = scale != null && scale.size() == 3 ? scale.get(0) : 1f, scaleY = scale != null && scale.size() == 3 ? scale.get(1) : 1f, scaleZ =
				scale != null && scale.size() == 3 ? scale.get(2) : 1f;

		if ("player_head".equals(type))
		{
			scaleX *= 2.0001f;
			scaleY *= 2.0002f;
			scaleZ *= 2.0001f;
			translationY += 0.5001f * (scaleY - 1.0002f);
		}

		if ("player_heads".equals(type))
		{
			scaleX *= 1.0001f;
			scaleY *= 1.0001f;
			scaleZ *= 1.0001f;
		}

		NBTList<Float> leftRotation = transformation.getFloatList("left_rotation");
		float leftRotationX = leftRotation != null && leftRotation.size() == 4 ? leftRotation.get(0) : 0f, leftRotationY =
				leftRotation != null && leftRotation.size() == 4 ? leftRotation.get(1) : 0f, leftRotationZ =
				leftRotation != null && leftRotation.size() == 4 ? leftRotation.get(2) : 0f, leftRotationW =
				leftRotation != null && leftRotation.size() == 4 ? leftRotation.get(3) : 1f;

		NBTList<Float> rightRotation = transformation.getFloatList("right_rotation");
		float rightRotationX = rightRotation != null && rightRotation.size() == 4 ? rightRotation.get(0) : 0f, rightRotationY =
				rightRotation != null && rightRotation.size() == 4 ? rightRotation.get(1) : 0f, rightRotationZ =
				rightRotation != null && rightRotation.size() == 4 ? rightRotation.get(2) : 0f, rightRotationW =
				rightRotation != null && rightRotation.size() == 4 ? rightRotation.get(3) : 1f;

		byte billBoard = 0;
		if (nbtItem.hasTag("billboard") && nbtItem.getType("billboard") == NBTType.NBTTagString)
		{
			billBoard = switch (nbtItem.getString("billboard"))
			{
				case "vertical" -> 1;
				case "horizontal" -> 2;
				case "center" -> 3;
				default -> 0;
			};
		}

		NBTCompound brightness = nbtItem.getOrCreateCompound("brightness");
		int brightnessBlock = brightness.hasTag("block") && brightness.getType("block") == NBTType.NBTTagInt ? brightness.getInteger("block") : -1, brightnessSky =
				brightness.hasTag("sky") && brightness.getType("sky") == NBTType.NBTTagInt
						? brightness.getInteger("sky")
						: -1;

		float viewRange = nbtItem.hasTag("view_range") && nbtItem.getType("view_range") == NBTType.NBTTagFloat ? nbtItem.getFloat("view_range") : 0.5f;
		float shadowRadius = nbtItem.hasTag("shadow_radius") && nbtItem.getType("shadow_radius") == NBTType.NBTTagFloat ? nbtItem.getFloat("shadow_radius") : 0f;
		float shadowStrength =
				nbtItem.hasTag("shadow_strength") && nbtItem.getType("shadow_strength") == NBTType.NBTTagFloat ? nbtItem.getFloat("shadow_strength") : 1f;
		float width = nbtItem.hasTag("width") && nbtItem.getType("width") == NBTType.NBTTagFloat ? nbtItem.getFloat("width") : 0f;
		float height = nbtItem.hasTag("height") && nbtItem.getType("height") == NBTType.NBTTagFloat ? nbtItem.getFloat("height") : 0f;

		int glowColorOverride =
				nbtItem.hasTag("glow_color_override") && nbtItem.getType("glow_color_override") == NBTType.NBTTagInt ? nbtItem.getInteger("glow_color_override") : -1;

		Boolean glowing = nbtItem.hasTag("Glowing") && nbtItem.getType("Glowing") == NBTType.NBTTagByte ? nbtItem.getBoolean("Glowing") : null;

		int entityId = Method.random(1, Integer.MAX_VALUE);
		String key = location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ();
		Set<Integer> integerSet = ITEM_DISPLAY_MAP.getOrDefault(key, new HashSet<>());
		integerSet.add(entityId);
		ITEM_DISPLAY_MAP.put(key, integerSet);

		ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
		PacketContainer packet = protocolManager.createPacket(Server.SPAWN_ENTITY);
		packet.getIntegers().write(0, entityId);
		packet.getEntityTypeModifier().write(0, entityType);
		double adjustLocation = "block".equals(type) ? 0d : 0.5d;
		// Set location
		packet.getDoubles().write(0, location.getX() + adjustLocation);
		packet.getDoubles().write(1, location.getY() + adjustLocation);
		packet.getDoubles().write(2, location.getZ() + adjustLocation);
		// Set yaw/pitch
		packet.getBytes().write(0, (byte) (rotationY * 256f / 360f));
		packet.getBytes().write(1, (byte) (rotationX * 256f / 360f));
		// Set UUID
		packet.getUUIDs().write(0, UUID.randomUUID());

		PacketContainer edit = protocolManager.createPacket(Server.ENTITY_METADATA);
		StructureModifier<List<WrappedDataValue>> watchableAccessor = edit.getDataValueCollectionModifier();

		List<WrappedDataValue> values = Lists.newArrayList(new WrappedDataValue(11, Registry.get(Vector3f.class),
						new Vector3f(translationX + offset[0] * scaleX, translationY + offset[1] * scaleY, translationZ + offset[2] * scaleZ)),
				new WrappedDataValue(12, Registry.get(Vector3f.class), new Vector3f(1.0001f * scaleX, 1.0001f * scaleY, 1.0001f * scaleZ)),
				new WrappedDataValue(13, Registry.get(Quaternionf.class), new Quaternionf(leftRotationX, leftRotationY, leftRotationZ, leftRotationW)),
				new WrappedDataValue(14, Registry.get(Quaternionf.class), new Quaternionf(rightRotationX, rightRotationY, rightRotationZ, rightRotationW)),
				new WrappedDataValue(15, Registry.get(Byte.class), billBoard),
				new WrappedDataValue(17, Registry.get(Float.class), viewRange * ("player_heads".equals(type) ? 0.5f : 1f)),
				new WrappedDataValue(18, Registry.get(Float.class), shadowRadius), new WrappedDataValue(19, Registry.get(Float.class), shadowStrength),
				new WrappedDataValue(20, Registry.get(Float.class), width), new WrappedDataValue(21, Registry.get(Float.class), height),
				new WrappedDataValue(22, Registry.get(Integer.class), glowColorOverride));
		if (glowing != null)
		{
			values.add(new WrappedDataValue(0, Registry.get(Byte.class), (byte) 0x40));
		}
		if (interpolation_delay != null)
		{
			values.add(new WrappedDataValue(8, Registry.get(Integer.class), interpolation_delay));
		}
		if (transformation_interpolation_duration != null)
		{
			values.add(new WrappedDataValue(9, Registry.get(Integer.class), transformation_interpolation_duration));
		}
		if (position_rotation_interpolation_duration != null)
		{
			values.add(new WrappedDataValue(10, Registry.get(Integer.class), position_rotation_interpolation_duration));
		}
		if (brightnessBlock != -1 && brightnessSky != -1)
		{
			values.add(new WrappedDataValue(16, Registry.get(Integer.class), brightnessBlock << 4 | brightnessSky << 20));
		}
		int itemDisplay = 0;
		if (nbtItem.hasTag("item_display") && nbtItem.getType("item_display") == NBTType.NBTTagString)
		{
			itemDisplay = switch (nbtItem.getString("item_display"))
			{
				case "third_person_left_hand" -> 1;
				case "third_person_right_hand" -> 2;
				case "first_person_left_hand" -> 3;
				case "first_person_right_hand" -> 4;
				case "head" -> 5;
				case "gui" -> 6;
				case "ground" -> 7;
				case "fixed" -> 8;
				default -> 0;
			};
		}

		switch (type)
		{
			case "block" ->
			{
				BlockData blockData;
				try
				{
					blockData = Bukkit.createBlockData(value);
				}
				catch (IllegalArgumentException exception)
				{
					MessageUtil.broadcastDebug("잘못된 블록 데이터: " + value);
					blockData = Material.AIR.createBlockData();
				}
				values.add(new WrappedDataValue(23, Registry.getBlockDataSerializer(false), WrappedBlockData.createData(blockData).getHandle()));
			}
			case "player_head", "player_heads" ->
			{
				ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
				SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
				ItemStackUtil.setTexture(skullMeta, value);
				itemStack.setItemMeta(skullMeta);
				Object minecraftItemStack = MinecraftReflection.getMinecraftItemStack(itemStack);
				values.add(new WrappedDataValue(23, Registry.getItemStackSerializer(false), minecraftItemStack));
				if (itemDisplay != 0)
				{
					values.add(new WrappedDataValue(24, Registry.get(Integer.class), itemDisplay));
				}
			}
			case "item" ->
			{
				ItemStack itemStack = ItemStackUtil.createItemStack(Bukkit.getConsoleSender(), value);
				if (itemStack == null)
				{
					MessageUtil.broadcastDebug("잘못된 아이템 데이터: " + value);
					itemStack = new ItemStack(Material.AIR);
				}
				Object minecraftItemStack = MinecraftReflection.getMinecraftItemStack(itemStack);
				values.add(new WrappedDataValue(23, Registry.getItemStackSerializer(false), minecraftItemStack));
				if (itemDisplay != 0)
				{
					values.add(new WrappedDataValue(24, Registry.get(Integer.class), itemDisplay));
				}
			}
			case "text" ->
			{
				WrappedChatComponent wrappedChatComponent = WrappedChatComponent.fromJson(ComponentUtil.serializeAsJson(ComponentUtil.create(value)));
				values.add(new WrappedDataValue(23, Registry.getChatComponentSerializer(), wrappedChatComponent.getHandle()));
				int lineWidth = nbtItem.hasTag("line_width") && nbtItem.getType("line_width") == NBTType.NBTTagInt ? nbtItem.getInteger("line_width") : 200;
				int background = nbtItem.hasTag("background") && nbtItem.getType("background") == NBTType.NBTTagInt ? nbtItem.getInteger("background") : 0x40_00_00_00;
				byte textOpacity = nbtItem.hasTag("text_opacity") && nbtItem.getType("text_opacity") == NBTType.NBTTagByte ? nbtItem.getByte("text_opacity") : -1;
				byte bitMask = 0;
				if (nbtItem.hasTag("shadow") && nbtItem.getType("shadow") == NBTType.NBTTagByte && nbtItem.getBoolean("shadow"))
				{
					bitMask |= 0x01;
				}
				if (nbtItem.hasTag("see_through") && nbtItem.getType("see_through") == NBTType.NBTTagByte && nbtItem.getBoolean("see_through"))
				{
					bitMask |= 0x02;
				}
				if (nbtItem.hasTag("default_background") && nbtItem.getType("default_background") == NBTType.NBTTagByte && nbtItem.getBoolean("default_background"))
				{
					bitMask |= 0x04;
				}
				values.add(new WrappedDataValue(24, Registry.get(Integer.class), lineWidth));
				values.add(new WrappedDataValue(25, Registry.get(Integer.class), background));
				values.add(new WrappedDataValue(26, Registry.get(Byte.class), textOpacity));
				values.add(new WrappedDataValue(27, Registry.get(Byte.class), bitMask));
			}
		}

		watchableAccessor.write(0, values);
		edit.getIntegers().write(0, entityId);
		for (Player player : players)
		{
			protocolManager.sendServerPacket(player, packet);
			protocolManager.sendServerPacket(player, edit);
		}
	}

	public static void spawnItemDisplay(@NotNull Collection<Player> players, @NotNull Location location)
	{
		if (!Cucumbery.using_ProtocolLib)
		{
			return;
		}
		players = new ArrayList<>(players);
		players.removeIf(player ->
		{
			double distance = Method2.distance(location, player.getLocation());
			return distance == -1 || distance > 1000d;
		});
		if (players.isEmpty())
		{
			return;
		}
		ItemStack itemStack = getItem(location);
		if (!ItemStackUtil.itemExists(itemStack))
		{
			return;
		}
		NBTItem nbtItem = new NBTItem(itemStack);
		if (!nbtItem.getKeys().contains("displays"))
		{
			return;
		}
		NBTCompound displays = nbtItem.getCompound("displays");
		if (nbtItem.getType("displays") == NBTType.NBTTagCompound && displays != null)
		{
			_spawnItemDisplay(players, location, displays);
		}
		else if (nbtItem.getType("displays") == NBTType.NBTTagList)
		{
			NBTCompoundList nbtCompoundList = nbtItem.getCompoundList("displays");
			for (ReadWriteNBT readWriteNBT : nbtCompoundList)
			{
				if (readWriteNBT instanceof NBTCompound nbtCompound)
				{
					_spawnItemDisplay(players, location, nbtCompound);
				}
			}
		}
	}

	public static void despawnItemDisplay(@NotNull Location location)
	{
		despawnItemDisplay(new ArrayList<>(Bukkit.getOnlinePlayers()), location);
	}

	public static void despawnItemDisplay(@NotNull Collection<Player> players, @NotNull Location location)
	{
		if (!Cucumbery.using_ProtocolLib)
		{
			return;
		}
		players = new ArrayList<>(players);
		players.removeIf(player ->
		{
			double distance = Method2.distance(location, player.getLocation());
			return distance == -1 || distance > 1000d;
		});
		if (players.isEmpty())
		{
			return;
		}
		try
		{
			Set<Integer> integerSet = ITEM_DISPLAY_MAP.getOrDefault(
					location.getWorld().getName() + "|" + location.getBlockX() + "|" + location.getBlockY() + "|" + location.getBlockZ(), new HashSet<>());
			for (int id : integerSet)
			{
				ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
				PacketContainer remove = protocolManager.createPacket(Server.ENTITY_DESTROY);
				remove.getIntLists().write(0, List.of(id));
				for (Player player : players)
				{
					protocolManager.sendServerPacket(player, remove);
				}
			}
		}
		catch (ConcurrentModificationException e)
		{
			MessageUtil.consoleSendMessage("아야");
		}
	}

	public static final HashMap<UUID, List<Chunk>> CHUNK_MAP = new HashMap<>();

	public static void display(@NotNull Player player, @NotNull Location currentLocation)
	{
		UUID uuid = player.getUniqueId();
		World currentWorld = currentLocation.getWorld();
		Chunk currentChunk = currentLocation.getChunk();
		int currentChunkX = currentChunk.getX(), currentChunkZ = currentChunk.getZ();
		List<Chunk> nearbyChunks = new ArrayList<>();
		for (int x = -3; x <= 3; x++)
		{
			for (int z = -3; z <= 3; z++)
			{
				nearbyChunks.add(currentWorld.getChunkAt(currentChunkX + x, currentChunkZ + z));
			}
		}
		List<Chunk> map = CHUNK_MAP.getOrDefault(uuid, new ArrayList<>());
		List<Location> locations = new ArrayList<>();
		for (Chunk chunk : nearbyChunks)
		{
			if (map.contains(chunk))
			{
				continue;
			}
			map.add(chunk);
			BlockPlaceDataConfig blockPlaceDataConfig = BlockPlaceDataConfig.getInstance(chunk);
			YamlConfiguration cfg = blockPlaceDataConfig.getConfig();
			ConfigurationSection root = cfg.getRoot();
			if (root != null)
			{
				for (String key : root.getKeys(false))
				{
					Location location = ChunkConfig.stringToLocation(player.getWorld(), key);
					if (location != null)
					{
						// 만약 해당 위치에 블록 채광 쿨타임이 있을 경우 추가하지 않는다
						// 채광 모드 1, 채광 모드 2
						if (Variable.customMiningCooldown.containsKey(location) || Variable.customMiningMode2BlockData.containsKey(location))
						{
							continue;
						}
						locations.add(location);
					}
				}
			}
		}
		if (!locations.isEmpty())
		{
			Timer timer = new Timer();
			TimerTask task = new TimerTask()
			{
				@Override
				public void run()
				{
					if (locations.isEmpty())
					{
						timer.cancel();
						return;
					}
					spawnItemDisplay(player, locations.get(0));
					locations.remove(0);
				}
			};
			timer.schedule(task, 0, 20);
			TIMERS.add(timer);
		}
		CHUNK_MAP.put(uuid, map);
	}

	public static final List<Timer> TIMERS = new ArrayList<>();

	public static void onPlayerMove(PlayerMoveEvent event)
	{
		if (!event.hasChangedBlock())
		{
			return;
		}
		Location from = event.getFrom(), to = event.getTo();
		Chunk fromChunk = from.getChunk(), toChunk = to.getChunk();
		if (from.getBlockY() / 16 == to.getBlockY() / 16 && fromChunk.getX() == toChunk.getX() && fromChunk.getZ() == toChunk.getZ())
		{
			return;
		}
		display(event.getPlayer(), to);
	}

	public static void onPlayerChangedWorld(PlayerChangedWorldEvent event)
	{
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		CHUNK_MAP.remove(uuid);
	}
}
