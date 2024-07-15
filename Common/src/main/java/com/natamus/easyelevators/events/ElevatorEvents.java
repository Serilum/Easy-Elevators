package com.natamus.easyelevators.events;

import com.mojang.datafixers.util.Pair;
import com.natamus.easyelevators.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.RelativeMovement;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class ElevatorEvents {
	public static void onCrouch(Level level, Player player) {
		if (level.isClientSide) {
			return;
		}

		Pair<BlockPos, BlockPos> elevatorData = Util.getElevatorData(level, player, player.blockPosition());
		if (elevatorData != null) {
			BlockPos belowElevator = elevatorData.getSecond();
			if (belowElevator != null) {
				Vec3 finalElevatorVec = Util.getFinalElevatorVec(level, belowElevator, player.position());

				player.teleportTo((ServerLevel)level, finalElevatorVec.x, finalElevatorVec.y, finalElevatorVec.z, RelativeMovement.ROTATION, player.getYRot(), player.getXRot());
			}
		}
	}

	public static void onJump(Level level, Player player) {
		if (level.isClientSide) {
			return;
		}

		Pair<BlockPos, BlockPos> elevatorData = Util.getElevatorData(level, player, player.blockPosition());
		if (elevatorData != null) {
			BlockPos aboveElevator = elevatorData.getFirst();
			if (aboveElevator != null) {

				Vec3 finalElevatorVec = Util.getFinalElevatorVec(level, aboveElevator, player.position());

				player.teleportTo((ServerLevel)level, finalElevatorVec.x, finalElevatorVec.y, finalElevatorVec.z, RelativeMovement.ROTATION, player.getYRot(), player.getXRot());
			}
		}
	}
}
