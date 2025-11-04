package com.jho5245.cucumbery.listeners.itemlore;

import com.jho5245.cucumbery.custom.customeffect.custom_mining.MiningManager;
import com.jho5245.cucumbery.custom.custommaterial.CustomMaterial;
import com.jho5245.cucumbery.events.itemlore.ItemLoreCustomMaterialEvent;
import com.jho5245.cucumbery.util.nbt.CucumberyTag;
import com.jho5245.cucumbery.util.storage.data.Constant.ExtraTag;
import com.jho5245.cucumbery.util.storage.no_groups.ItemStackUtil;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@SuppressWarnings("unused")
public class ItemLoreCustomMaterial implements Listener
{
	@EventHandler
	public void onItemLoreCustomMaterial(ItemLoreCustomMaterialEvent event)
	{
		ItemStack itemStack = event.getItemStack();
		CustomMaterial customMaterial = CustomMaterial.itemStackOf(itemStack);
		// NBT
		{
			if (customMaterial == CustomMaterial.WNYNYA_ORE)
			{
				NBT.modifyComponents(itemStack, nbt -> {
					var profile = nbt.getOrCreateCompound("minecraft:profile");
					profile.setIntArray("id", new int[] {0, 0, 0 ,0});
					var properties = profile.getCompoundList("properties");
					properties.clear();
					ReadWriteNBT compound = NBT.createNBTObject();
					compound.setString("name", "textures");
					compound.setString("value", ItemStackUtil.getTextureBase64("3fff5379e980aca266d7d1a3a4939ecafd42e2028d436ffa41ae96b076937d09"));
					properties.addCompound(compound);
				});

				NBT.modify(itemStack, nbt -> {
					var itemTag = nbt.getOrCreateCompound(CucumberyTag.KEY_MAIN);
					var extraTags = itemTag.getStringList(CucumberyTag.EXTRA_TAGS_KEY);
					extraTags.clear();
					extraTags.add(ExtraTag.PRESERVE_BLOCK_NBT.toString());
					// 커스텀 블록은 기존 마크의 블록 드롭 테이블 규칙을 무시하고 자기 자신을 드롭
					nbt.setBoolean(MiningManager.IGNORE_VANILLA_MODIFICATION, true);

					// 블록 정보
					nbt.setString("change_material", Material.GRAY_STAINED_GLASS.toString());
					nbt.setInteger("BlockTier", 2);
					nbt.setDouble("BlockHardness", 500d);
					nbt.setString("BreakSound", "block.stone.break");
					nbt.setString("BreakParticle", "block:stone[]");
					var displays = nbt.getOrCreateCompound("displays");
					displays.setString("type", "player_heads");
					var urls = displays.getStringList("value");
					urls.clear();
					{
						urls.add("b892df651cc7aa1b2a29b0dbda49ce4faae9fd49292612751aa997ebf0dc3dac");
						urls.add("7eb1ca6f34d04a2bf7379594a4c61a48f0b2053947b366cc14947086f309f315");
						urls.add("6b09a7f9b9df42cf881a71d23bb92938e32ba68e1e8da5a22678ec8a024497c2");
						urls.add("2771d58bcd60a56ac0bbf306acb8eef399308d1ee9f0bc444b91cd8cfa54397");
						urls.add("d88ac94a21975bea9e2e43ef670566b04629164f171f2a041e476270e0e6a5ed");
						urls.add("ff176fd84f05c0b78a85d744fdae5279e3d818489394c215d357cd3b7a1a9757");
						urls.add("76bad50fe9757edd2ce2823b3af26d1f59c9d7ea31238aac92374fada2494ec1");
						urls.add("1a2643647c3f26f94c62dd2ae20cfd24ba3f8236f84d6563773ff5e8a64708c1");
					}
				});
			}
		}

		// ItemMeta
		{
			ItemMeta itemMeta = itemStack.getItemMeta();
			itemStack.setItemMeta(itemMeta);
		}
		event.setItemStack(itemStack);
	}
}
