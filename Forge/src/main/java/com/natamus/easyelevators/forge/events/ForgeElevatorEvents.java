package com.natamus.easyelevators.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.easyelevators.util.Util;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;

import java.lang.invoke.MethodHandles;

public class ForgeElevatorEvents {
	public static void registerEventsInBus() {
		// BusGroup.DEFAULT.register(MethodHandles.lookup(), ForgeElevatorEvents.class);

		LevelEvent.Load.BUS.addListener(ForgeElevatorEvents::onWorldLoad);
	}

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load e) {
        Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
        if (level == null) {
            return;
        }

        Util.processConfigBlocks(level);
    }
}
