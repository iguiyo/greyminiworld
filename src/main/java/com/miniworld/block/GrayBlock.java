package com.miniworld.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class GrayBlock extends Block {
	public GrayBlock(Properties properties) {
		super(properties);
	}

	@Override
	protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
		return player.isCreative() ? super.getDestroyProgress(state, player, level, pos) : 0.0F;
	}
}