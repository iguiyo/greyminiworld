package com.miniworld.world;

import com.miniworld.Miniworld;
import com.miniworld.registry.ModBlocks;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;

public final class MiniworldDimension {
	public static final ResourceKey<Level> LEVEL_KEY = ResourceKey.create(Registries.DIMENSION, Miniworld.id("miniworld"));
	private static final int SIZE = 15;
	private static final int FLOOR_Y = 60;
	private static final int ROOM_SPACING = 32;
	private static final int ROOM_RANGE = 4096;
	private static final String ROOM_ID = "miniworld_room_id";

	private MiniworldDimension() {
	}

	public static void enter(ServerPlayer player, ItemStack stack) {
		MinecraftServer server = player.level().getServer();
		ServerLevel world = server.getLevel(LEVEL_KEY);
		if (world == null) return;

		RoomOrigin origin = roomOrigin(ensureRoomId(stack));
		ensureRoom(world, origin);
		teleport(
			player,
			world,
			new Vec3(origin.x() + 7.5D, FLOOR_Y + 1.0D, origin.z() + 7.5D),
			player.getYRot(),
			player.getXRot()
		);
	}

	public static void leave(ServerPlayer player, ItemStack stack) {
		ServerLevel overworld = player.level().getServer().overworld();
		LevelData.RespawnData spawn = overworld.getRespawnData();
		BlockPos pos = spawn.pos();
		teleport(player, overworld, new Vec3(pos.getX() + 0.5D, pos.getY() + 0.1D, pos.getZ() + 0.5D), spawn.yaw(), spawn.pitch());
	}

	public static boolean isSpaceForPlayer(ServerPlayer player, ItemStack stack) {
		if (!player.level().dimension().equals(LEVEL_KEY)) return false;

		String roomId = readRoomId(stack);
		if (roomId == null) return false;

		RoomOrigin origin = roomOrigin(roomId);
		return player.getX() >= origin.x()
			&& player.getX() < origin.x() + SIZE
			&& player.getZ() >= origin.z()
			&& player.getZ() < origin.z() + SIZE;
	}

	private static String ensureRoomId(ItemStack stack) {
		String roomId = readRoomId(stack);
		if (roomId != null) return roomId;

		String newRoomId = UUID.randomUUID().toString();
		CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putString(ROOM_ID, newRoomId));
		return newRoomId;
	}

	private static String readRoomId(ItemStack stack) {
		CustomData data = stack.get(DataComponents.CUSTOM_DATA);
		if (data == null) return null;

		String roomId = data.copyTag().getStringOr(ROOM_ID, "");
		return roomId.isEmpty() ? null : roomId;
	}

	private static RoomOrigin roomOrigin(String roomId) {
		return roomOrigin(UUID.fromString(roomId));
	}

	private static RoomOrigin roomOrigin(UUID uuid) {
		long hash = uuid.getMostSignificantBits() ^ Long.rotateLeft(uuid.getLeastSignificantBits(), 32);
		int x = (int) Math.floorMod(hash, ROOM_RANGE) - ROOM_RANGE / 2;
		int z = (int) Math.floorMod(hash >>> 32, ROOM_RANGE) - ROOM_RANGE / 2;
		return new RoomOrigin(x * ROOM_SPACING, z * ROOM_SPACING);
	}

	private static void teleport(ServerPlayer player, ServerLevel world, Vec3 pos, float yaw, float pitch) {
		player.teleport(new TeleportTransition(world, pos, Vec3.ZERO, yaw, pitch, TeleportTransition.DO_NOTHING));
		player.resetFallDistance();
		player.setDeltaMovement(Vec3.ZERO);
	}

	private static void ensureRoom(ServerLevel level, RoomOrigin origin) {
		BlockState gray = ModBlocks.GRAY_BLOCK.defaultBlockState();
		if (isIntact(level, gray, origin)) return;

		for (int x = 0; x < SIZE; x++) {
			for (int y = 0; y < SIZE; y++) {
				for (int z = 0; z < SIZE; z++) {
					boolean edge = x == 0 || x == SIZE - 1 || y == 0 || y == SIZE - 1 || z == 0 || z == SIZE - 1;
					if (edge) {
						level.setBlock(
							new BlockPos(origin.x() + x, FLOOR_Y + y, origin.z() + z),
							gray,
							Block.UPDATE_CLIENTS
						);
					}
				}
			}
		}
	}

	private static boolean isIntact(ServerLevel level, BlockState gray, RoomOrigin origin) {
		int mid = SIZE / 2;
		BlockPos[] checks = {
			new BlockPos(origin.x() + mid, FLOOR_Y, origin.z() + mid),
			new BlockPos(origin.x() + mid, FLOOR_Y + SIZE - 1, origin.z() + mid),
			new BlockPos(origin.x(), FLOOR_Y + mid, origin.z() + mid),
			new BlockPos(origin.x() + SIZE - 1, FLOOR_Y + mid, origin.z() + mid),
			new BlockPos(origin.x() + mid, FLOOR_Y + mid, origin.z()),
			new BlockPos(origin.x() + mid, FLOOR_Y + mid, origin.z() + SIZE - 1)
		};
		for (BlockPos pos : checks) {
			if (!level.getBlockState(pos).is(gray.getBlock())) return false;
		}
		return true;
	}

	private record RoomOrigin(int x, int z) {
	}
}
