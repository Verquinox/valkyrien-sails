package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.*;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.networking.WindModNetworking;
import com.quintonc.vs_sails.ship.SailsShipControl;
import dev.architectury.registry.registries.DeferredRegister;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

import static java.lang.Math.pow;

public class ValkyrienSails {
    public static final String MOD_ID = "vs_sails";
    public static final Logger LOGGER = LoggerFactory.getLogger("vs_sails_common");
    private static int tickCount = 0;
    private static final int refreshRate = 4;
    public static final double EULERS_NUMBER = 2.71828182846;

//    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(MOD_ID, Registries.BLOCK);
//    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);
//    private static DeferredRegister<Item> ITEMS = DeferredRegister.create(MOD_ID, Registries.ITEM);
//
//    private static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);
//
//    public static DeferredRegister<ParticleType> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);

    //Blocks
    public static SailBlock SAIL_BLOCK;
    public static SailBlock WHITE_SAIL;
    public static SailBlock RED_SAIL;

    public static HelmBlock HELM_BLOCK;
    public static HelmWheel HELM_WHEEL;
    public static RiggingBlock RIGGING_BLOCK;
    public static BallastBlock BALLAST_BLOCK;
    public static MagicBallastBlock MAGIC_BALLAST_BLOCK;
    public static BuoyBlock BUOY_BLOCK;

    //Block Entities
    public static BlockEntityType<HelmBlockEntity> HELM_BLOCK_ENTITY;

    //Items
    public static Item DEDICATION_BOTTLE;
    public static Item ROPE;

    //Particles
    public static SimpleParticleType WIND_PARTICLE;

    public static void init() {
        LOGGER.info("Common Init");
        ConfigUtils.checkConfigs();

        LOGGER.info("Sailing time.");
    }

    public static void InitializeVSWind(ServerLevel world) {
        LOGGER.info("The wind is blowing.");
        ServerTickEvents.START_WORLD_TICK.register(ValkyrienSails::onWorldTick);
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void onWorldTick(ServerLevel world) {
        if(tickCount == refreshRate) {
            tickCount = 0;

            //Spawn wind particles for all players being dragged by ships with a SailsShipControl attachment
            world.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                if (serverPlayerEntity instanceof IEntityDraggingInformationProvider player) {
                    if (player.getDraggingInformation().getLastShipStoodOn() != null) {
                        long shipId = player.getDraggingInformation().getLastShipStoodOn();
                        ServerShip ship = (ServerShip) VSGameUtilsKt.getAllShips(world).getById(shipId);
                        if (ship != null) {
                            SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                            if (controller != null) {
                                //serverPlayerEntity.sendMessage(ship.getAttachment(SailsShipControl.class).message, true);
                                if (player.getDraggingInformation().getTicksSinceStoodOnShip() < 100) {
                                    double windDir = Math.toRadians(ServerWindManager.getWindDirection()+180);
                                    world.sendParticles(serverPlayerEntity, ValkyrienSails.WIND_PARTICLE, false, serverPlayerEntity.getX()+15*Math.sin(windDir), serverPlayerEntity.getY()+25, serverPlayerEntity.getZ()+15*Math.sin(windDir), 10, 20, 10, 20, 0);
                                    //fixme use single particle spawning, or transfer to client?
                                }
                            }
                        }
                    }
                }
            });



        } else {
            tickCount++;
        }
    }

    private static Vec3 project(Vec3 vec1, Vec3 vec2) {
        return vec1.scale(vec1.dot(vec2) / pow(vec1.length(), 2));
    }

    public static void onServerStarted(MinecraftServer server) {
        if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("enable-wind","true"))) {
            ServerWindManager.InitializeWind(server.overworld());
            ValkyrienSails.InitializeVSWind(server.overworld());
            WindModNetworking.networkingInit();
        }
    }
}
