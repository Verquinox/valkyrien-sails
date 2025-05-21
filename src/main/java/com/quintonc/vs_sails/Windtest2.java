//package com.quintonc.vs_sails;
//
//import com.quintonc.vs_sails.networking.WindModNetworking;
//import net.fabricmc.api.ModInitializer;
//
//import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
//import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
//import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
//import net.minecraft.server.MinecraftServer;
//import net.minecraft.world.GameRules;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class Windtest2 implements ModInitializer {
//	// This logger is used to write text to the console and the log file.
//	// It is considered best practice to use your mod id as the logger's name.
//	// That way, it's clear which mod wrote info, warnings, and errors.
//	public static final String MOD_ID = "wind-test-2";
//    public static final Logger LOGGER = LoggerFactory.getLogger("wind-test-2");
//
//	public static final GameRules.Key<GameRules.IntRule> MAX_WIND_SPEED =
//			GameRuleRegistry.register("maxWindSpeed", GameRules.Category.MISC, GameRuleFactory.createIntRule(32));
//
//	@Override
//	public void onInitialize() {
//		// This code runs as soon as Minecraft is in a mod-load-ready state.
//		// However, some things (like resources) may still be uninitialized.
//		// Proceed with mild caution.
//		ModSounds.registerSounds();
//		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
//
//	}
//
//	private void onServerStarted(MinecraftServer server) {
//		ServerWindManager.InitializeWind(server.getOverworld());
//		ValkyrienSails.InitializeVSWind(server.getOverworld());
//		WindModNetworking.networkingInit();
//	}
//
//
//}