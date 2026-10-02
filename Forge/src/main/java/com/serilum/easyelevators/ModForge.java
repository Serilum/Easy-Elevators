package com.serilum.easyelevators;

import com.natamus.collective.check.RegisterMod;
import com.natamus.collective.check.ShouldLoadCheck;
import com.serilum.easyelevators.forge.config.IntegrateForgeConfig;
import com.serilum.easyelevators.forge.events.ForgeElevatorEvents;
import com.serilum.easyelevators.util.Reference;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Reference.MOD_ID)
public class ModForge {
	
	public ModForge(FMLJavaModLoadingContext modLoadingContext) {
		if (!ShouldLoadCheck.shouldLoad(Reference.MOD_ID)) {
			return;
		}

		BusGroup busGroup = modLoadingContext.getModBusGroup();
		FMLLoadCompleteEvent.getBus(busGroup).addListener(this::loadComplete);

		setGlobalConstants();
		ModCommon.init();

		IntegrateForgeConfig.registerScreen(modLoadingContext);

		RegisterMod.register(Reference.NAME, Reference.MOD_ID, Reference.VERSION, Reference.ACCEPTED_VERSIONS);
	}

	private void loadComplete(final FMLLoadCompleteEvent event) {
		ForgeElevatorEvents.registerEventsInBus();
	}

	private static void setGlobalConstants() {

	}
}