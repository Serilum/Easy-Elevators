package com.natamus.easyelevators.mixin;

import com.natamus.easyelevators.events.ElevatorEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerPlayer.class, priority = 1001)
public class ServerPlayerMixin {
	@Inject(method = "jumpFromGround()V", at = @At(value = "TAIL"))
	public void jumpFromGround(CallbackInfo ci) {
		Player player = (Player)(Object)this;
		ElevatorEvents.onJump(player.level(), player);
	}
}
