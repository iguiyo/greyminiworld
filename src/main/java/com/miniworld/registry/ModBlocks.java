package com.miniworld.registry;

import com.miniworld.Miniworld;
import com.miniworld.block.GrayBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

public final class ModBlocks {
	public static final Block GRAY_BLOCK = new GrayBlock(BlockBehaviour.Properties.of()
		.mapColor(DyeColor.GRAY)
		.strength(0.6F, 3_600_000.0F)
		.sound(SoundType.STONE)
		.lightLevel(state -> 15)
		.emissiveRendering(state -> true)
		.pushReaction(PushReaction.IMMOVEABLE)
		.setId(ResourceKey.create(Registries.BLOCK, Miniworld.id("gray_block"))));

	private ModBlocks() {
	}

	public static void register() {
		Registry.register(BuiltInRegistries.BLOCK, Miniworld.id("gray_block"), GRAY_BLOCK);
	}
}
