package knightminer.ceramics.items;

import java.util.Locale;

import knightminer.ceramics.Ceramics;
import knightminer.ceramics.library.Config;
import knightminer.ceramics.library.Util;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class ItemClayUnfired extends Item {

	public ItemClayUnfired() {
		this.setCreativeTab(Ceramics.tab);
		this.setHasSubtypes(true);
		this.setNoRepair();
	}

	/**
	 * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
	 */
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
		if (this.isInCreativeTab(tab)) {
			for(UnfiredType type : UnfiredType.values()) {
				if(type.shouldDisplay()) {
					subItems.add(new ItemStack(this, 1, type.getMeta()));
				}
			}
		}
	}

	/**
	 * Gets the maximum number of items that this stack should be able to hold.
	 * This is a ItemStack (and thus NBT) sensitive version of Item.getItemStackLimit()
	 *
	 * @param stack The ItemStack
	 * @return The maximum number this item can be stacked to
	 */
	@Override
	public int getItemStackLimit(ItemStack stack) {
		return UnfiredType.fromMeta(stack.getItemDamage()).getStackSize();
	}

	/**
	 * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
	 * different names based on their damage or NBT.
	 */
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item." + Util.prefix("unfired." + UnfiredType.fromMeta(stack.getItemDamage()).getName());
	}

	public enum UnfiredType {
		BUCKET(16),
		SHEARS(1),
		BARREL,
		BARREL_EXTENSION,
		PORCELAIN,
		PORCELAIN_BRICK,
		FAUCET,
		CHANNEL,
		CLAY_PLATE_RAW,
		CLAY_PLATE;

		private int meta;
		private int stackSize;

		UnfiredType() {
			meta = this.ordinal();
			this.stackSize = 64;
		}

		UnfiredType(int stackSize) {
			meta = this.ordinal();
			this.stackSize = stackSize;
		}

		public int getMeta() {
			return meta;
		}

		/**
		 * Determines if the unfired item is enabled
		 */
		public boolean shouldDisplay() {
			// defined as a switch as we cannot pass along a boolean reference in a constructor easily
			switch(this) {
				case BUCKET:
					return Config.bucketEnabled && !Config.placeClayBucket;

				case SHEARS:
					return Config.shearsEnabled;

				case PORCELAIN:
				case PORCELAIN_BRICK:
					return Config.porcelainEnabled;

				case BARREL:
				case BARREL_EXTENSION:
					return false; // moved to clayBarrelUnfired, here until I add more items

				case CLAY_PLATE:
				case CLAY_PLATE_RAW:
					return Config.armorEnabled;

				case FAUCET:
				case CHANNEL:
					return Config.faucetEnabled;
			}
			// fallback incase it was missed
			return true;
		}

		public int getStackSize() {
			return stackSize;
		}

		public static UnfiredType fromMeta(int meta) {
			if(meta < 0 || meta >= values().length) {
				meta = 0;
			}

			return values()[meta];
		}

		public String getName() {
			return this.name().toLowerCase(Locale.US);
		}

		public String getTextureName() {
			String name = getName();
			if (!Config.porcelainEnabled && (this == FAUCET || this == CHANNEL)) {
				name += "_clay";
			}
			return name;
		}
	}
}
