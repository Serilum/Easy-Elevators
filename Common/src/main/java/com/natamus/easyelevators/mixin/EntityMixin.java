package com.natamus.easyelevators.mixin;

import com.natamus.easyelevators.events.ElevatorEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class, priority = 1001)
public class EntityMixin {
	@Inject(method = "setShiftKeyDown(Z)V", at = @At(value = "TAIL"))
	public void setShiftKeyDown(boolean isShiftDown, CallbackInfo ci) {
		if (!isShiftDown) {
			return;
		}

		Entity entity = (Entity)(Object)this;
		if (entity instanceof Player) {
			ElevatorEvents.onCrouch(entity.level(), (Player)entity);
		}
	}
}
