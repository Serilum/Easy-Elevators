package com.natamus.easyelevators.config;

import com.natamus.collective.config.DuskConfig;
import com.natamus.easyelevators.util.Reference;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler extends DuskConfig {
	public static HashMap<String, List<String>> configMetaData = new HashMap<String, List<String>>();

	@Entry public static String elevatorBlocks = "minecraft:iron_block,minecraft:copper_block";
	@Entry public static String elevatorChains = "minecraft:chain,minecraft:end_rod";
	@Entry(min = 1, max = 33) public static int minimumElevatorSize = 3;
	@Entry(min = 1, max = 33) public static int maximumElevatorSize = 5;
	@Entry public static boolean elevatorMustBeConnectedViaChains = true;
	@Entry(min = 1, max = 1000) public static int maximumChainlessElevatorHeight = 20;

	public static void initConfig() {
		configMetaData.put("elevatorBlocks", Arrays.asList(
			"The resourcelocations of blocks that are considered to be an elevator block."
		));
		configMetaData.put("elevatorChains", Arrays.asList(
			"The resourcelocations of blocks that are considered to be an elevator chain."
		));
		configMetaData.put("minimumElevatorSize", Arrays.asList(
			"The minimum size an elevator has to be. By default 3, which means a 3x3 area."
		));
		configMetaData.put("maximumElevatorSize", Arrays.asList(
			"The maximum size an elevator can be. By default 5, which means a 5x5 area."
		));
		configMetaData.put("elevatorMustBeConnectedViaChains", Arrays.asList(
			"Whether two iron blocks must be connected via a chain in order to function as an elevator."
		));
		configMetaData.put("maximumChainlessElevatorHeight", Arrays.asList(
			"The maximum amount of blocks the elevator can go up or down in one go with 'elevatorMustBeConnectedViaChains' disabled. More specifically, how many blocks the mod checks up and down to find another elevator block."
		));
		DuskConfig.init(Reference.NAME, Reference.MOD_ID, ConfigHandler.class);
	}
}