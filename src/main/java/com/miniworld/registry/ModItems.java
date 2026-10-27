package com.miniworld.registry;

import com.miniworld.Miniworld;
import com.miniworld.item.GrayStickItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

public final class ModItems {
	public static final Item GRAY_BLOCK = new BlockItem(ModBlocks.GRAY_BLOCK, new Item.Properties()
		.setId(ResourceKey.create(Registries.ITEM, Miniworld.id("gray_block")))
		.useBlockDescriptionPrefix());

	public static final Item GRAY_STICK = new GrayStickItem(new Item.Properties()
		.stacksTo(1)
		.rarity(Rarity.RARE)
		.fireResistant()
		.setId(ResourceKey.create(Registries.ITEM, Miniworld.id("gray_stick"))));

	private ModItems() {
	}

	public static void register() {
		Registry.register(BuiltInRegistries.ITEM, Miniworld.id("gray_block"), GRAY_BLOCK);
		Registry.register(BuiltInRegistries.ITEM, Miniworld.id("gray_stick"), GRAY_STICK);
	}
}