package com.miniworld.item;

import com.miniworld.world.MiniworldDimension;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GrayStickItem extends Item {
	public GrayStickItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		if (level.isClientSide()) {
			return InteractionResult.PASS;
		}
		if (player instanceof ServerPlayer serverPlayer) {
			ItemStack stack = player.getItemInHand(hand);
			if (MiniworldDimension.isSpaceForPlayer(serverPlayer, stack)) {
				MiniworldDimension.leave(serverPlayer, stack);
			} else {
				MiniworldDimension.enter(serverPlayer, stack);
			}
			return InteractionResult.SUCCESS_SERVER;
		}
		return InteractionResult.PASS;
	}
}