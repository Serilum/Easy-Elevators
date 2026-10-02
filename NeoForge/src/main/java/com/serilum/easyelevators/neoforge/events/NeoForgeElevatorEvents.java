package com.serilum.easyelevators.neoforge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.serilum.easyelevators.util.Util;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.level.LevelEvent;

public class NeoForgeElevatorEvents {
	@SubscribeEvent
	public static void onWorldLoad(LevelEvent.Load e) {
		Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
		if (level == null) {
			return;
		}

		Util.processConfigBlocks(level);
	}
}
