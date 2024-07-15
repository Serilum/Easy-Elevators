package com.natamus.easyelevators.util;

import com.mojang.datafixers.util.Pair;
import com.natamus.easyelevators.config.ConfigHandler;
import com.natamus.easyelevators.data.Variables;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;

public class Util {
	public static final List<Block> elevatorBlocks = new ArrayList<>();
	public static final List<Block> elevatorChains = new ArrayList<>();

	public static void processConfigBlocks(Level level) {
		if (Variables.processedConfigBlocks) {
			return;
		}
		Variables.processedConfigBlocks = true;

		Registry<Block> blockRegistry = level.registryAccess().registryOrThrow(Registries.BLOCK);

		for (String rawElevatorBlock : ConfigHandler.elevatorBlocks.split(",")) {
			ResourceLocation rawElevatorBlockRL = ResourceLocation.tryParse(rawElevatorBlock.strip());

			if (blockRegistry.containsKey(rawElevatorBlockRL)) {
				Block elevatorBlock = blockRegistry.get(rawElevatorBlockRL);
				if (!elevatorBlocks.contains(elevatorBlock)) {
					elevatorBlocks.add(elevatorBlock);
				}
			}
		}

		for (String rawElevatorChain : ConfigHandler.elevatorChains.split(",")) {
			ResourceLocation rawElevatorChainRL = ResourceLocation.tryParse(rawElevatorChain.strip());

			if (blockRegistry.containsKey(rawElevatorChainRL)) {
				Block elevatorChain = blockRegistry.get(rawElevatorChainRL);
				if (!elevatorChains.contains(elevatorChain)) {
					elevatorChains.add(elevatorChain);
				}
			}
		}
	}

	public static boolean isElevatorBlock(Level level, BlockPos blockPos) {
		return isElevatorBlock(level.getBlockState(blockPos).getBlock());
	}
	public static boolean isElevatorBlock(Block block) {
		return elevatorBlocks.contains(block);
	}
	public static boolean isElevatorChain(Level level, BlockPos blockPos) {
		return isElevatorChain(level.getBlockState(blockPos).getBlock());
	}
	public static boolean isElevatorChain(Block block) {
		return elevatorChains.contains(block);
	}

	public static Pair<BlockPos, BlockPos> getElevatorData(Level level, Player player, BlockPos blockPos) {
		Block insideBlock = level.getBlockState(blockPos).getBlock();
		if (!insideBlock.equals(Blocks.AIR) && !isElevatorChain(insideBlock)) {
			return null;
		}

		if (!isElevatorBlock(level, blockPos.below())) {
			return null;
		}

		BlockPos currentElevatorPos = blockPos.below().immutable();

		int elevatorSize = calculateElevatorSize(level, currentElevatorPos);
		if (elevatorSize < ConfigHandler.minimumElevatorSize) {
			return null;
		}

		Pair<BlockPos, BlockPos> elevatorData = null;
		if (!ConfigHandler.elevatorMustBeConnectedViaChains) {
			elevatorData = getChainlessElevatorAboveAndBelow(level, currentElevatorPos);
		}
		else {
			elevatorData = getChainElevatorAboveAndBelow(level, findChainPositionAbove(level, currentElevatorPos), findChainPositionBelow(level, currentElevatorPos));
		}

		if (elevatorData != null) {
			int aboveSize = elevatorData.getFirst() != null ? calculateElevatorSize(level, elevatorData.getFirst()) : 0;
			int belowSize = elevatorData.getSecond() != null ? calculateElevatorSize(level, elevatorData.getSecond()) : 0;

			if (aboveSize < ConfigHandler.minimumElevatorSize && belowSize < ConfigHandler.minimumElevatorSize) {
				return null;
			}

			elevatorData = Pair.of(aboveSize < ConfigHandler.minimumElevatorSize ? null : elevatorData.getFirst(),
									belowSize < ConfigHandler.minimumElevatorSize ? null : elevatorData.getSecond());
		}

		return elevatorData;
	}

	private static Pair<BlockPos, BlockPos> getChainElevatorAboveAndBelow(Level level, @Nullable BlockPos aboveChain, @Nullable BlockPos belowChain) {
		return Pair.of(findChainElevator(level, aboveChain, true), findChainElevator(level, belowChain, false));
	}

	private static Pair<BlockPos, BlockPos> getChainlessElevatorAboveAndBelow(Level level, BlockPos elevatorPos) {
		return Pair.of(findChainlessElevator(level, elevatorPos, true), findChainlessElevator(level, elevatorPos, false));
	}

	public static Vec3 getFinalElevatorVec(Level level, BlockPos elevatorPos, Vec3 playerVec) {
		double x = playerVec.x;
		double y = elevatorPos.getY();
		double z = playerVec.z;

		if (!isElevatorBlock(level, BlockPos.containing(x, y, z))) {
			return new Vec3(elevatorPos.getX(), y+1, elevatorPos.getZ());
		}

		return new Vec3(x, y+1, z);
	}


	private static @Nullable BlockPos findChainPositionAbove(Level level, BlockPos blockPos) {
		return findChainPosition(level, blockPos, 0, 1);
	}

	private static @Nullable BlockPos findChainPositionBelow(Level level, BlockPos blockPos) {
		return findChainPosition(level, blockPos, 0, -1);
	}

	private static @Nullable BlockPos findChainPosition(Level level, BlockPos blockPos, int... yOffsets) {
		int size = ConfigHandler.maximumElevatorSize;
		int range = (size % 2 == 0) ? size / 2 : (size - 1) / 2;

		for (int yOffset : yOffsets) {
			for (BlockPos aroundPos : BlockPos.betweenClosed(
					blockPos.getX() - 2, blockPos.getY() + yOffset, blockPos.getZ() - 2,
					blockPos.getX() + 2, blockPos.getY() + yOffset, blockPos.getZ() + 2)) {
				if (isElevatorChain(level, aroundPos)) {
					return aroundPos.immutable();
				}
			}
		}
		return null;
	}

	private static @Nullable BlockPos findChainElevator(Level level, @Nullable BlockPos chainPos, boolean searchAbove) {
		if (chainPos == null) {
			return null;
		}

		BlockPos currentChainPos = chainPos.immutable();
		while (isElevatorChain(level, searchAbove ? currentChainPos.above() : currentChainPos.below())) {
			currentChainPos = (searchAbove ? currentChainPos.above() : currentChainPos.below()).immutable();
		}

		BlockPos possibleElevatorPos = searchAbove ? currentChainPos.above() : currentChainPos.below();
		if (isElevatorBlock(level, possibleElevatorPos)) {
			return possibleElevatorPos.immutable();
		}

		return null;
	}


	private static @Nullable BlockPos findChainlessElevator(Level level, BlockPos elevatorPos, boolean searchAbove) {
		BlockPos currentPos = elevatorPos.immutable();
		int direction = searchAbove ? 1 : -1;

		for (int i = 1; i <= ConfigHandler.maximumChainlessElevatorHeight; i++) {
			BlockPos nextPos = currentPos.offset(0, i * direction, 0);
			if (isElevatorBlock(level, nextPos)) {
				return nextPos.immutable();
			}
		}

		return null;
	}


	public static int calculateElevatorSize(Level level, BlockPos startPos) {
		Block targetBlock = level.getBlockState(startPos).getBlock();
		if (!isElevatorBlock(level, startPos)) {
			return 0;
		}

		Set<BlockPos> visited = new HashSet<>();
		Queue<BlockPos> queue = new LinkedList<>();
		queue.add(startPos);
		visited.add(startPos);

		while (!queue.isEmpty()) {
			BlockPos current = queue.poll();

			for (BlockPos neighbor : new BlockPos[]{
					current.north(), current.south(),
					current.east(), current.west()}) {

				if (!visited.contains(neighbor) && isElevatorBlock(level, neighbor)) {
					visited.add(neighbor);
					queue.add(neighbor);
				}
			}
		}

		return findLargestSquare(visited);
	}

	private static int findLargestSquare(Set<BlockPos> visited) {
		int maxSize = 0;

		for (BlockPos pos : visited) {
			int size = 1;
			boolean squareFound;

			do {
				squareFound = true;

				for (int x = 0; x < size; x++) {
					for (int y = 0; y < size; y++) {
						BlockPos checkPos = pos.offset(x, 0, y);
						if (!visited.contains(checkPos)) {
							squareFound = false;
							break;
						}
					}
					if (!squareFound) {
						break;
					}
				}

				if (squareFound) {
					maxSize = Math.max(maxSize, size);
					size++;
				}

			} while (squareFound);
		}

		return maxSize;
	}
}
