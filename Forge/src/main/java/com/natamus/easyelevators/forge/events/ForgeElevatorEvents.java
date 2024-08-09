package com.natamus.easyelevators.forge.events;

import com.natamus.collective.functions.WorldFunctions;
import com.natamus.easyelevators.util.Util;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class ForgeElevatorEvents {
    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load e) {
        Level level = WorldFunctions.getWorldIfInstanceOfAndNotRemote(e.getLevel());
        if (level == null) {
            return;
        }

        Util.processConfigBlocks(level);
    }
}
