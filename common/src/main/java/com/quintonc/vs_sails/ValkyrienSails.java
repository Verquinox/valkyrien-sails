package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.RedstoneHelmBlockEntity;
import com.quintonc.vs_sails.config.ConfigUtils;
import com.quintonc.vs_sails.registration.SailsBlocks;
import com.quintonc.vs_sails.registration.SailsItems;
import com.quintonc.vs_sails.ship.SailsShipControl;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3dc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.api.ValkyrienSkies;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

import static java.lang.Math.pow;

public class ValkyrienSails {
    public static final String MOD_ID = "vs_sails";
    public static final Logger LOGGER = LoggerFactory.getLogger("vs_sails_common");
    private static int tickCount = 0;
    private static final int refreshRate = 4;
    public static boolean weather2 = Platform.isModLoaded("weather2");
    public static boolean sailsWind = false;
    public static final double EULERS_NUMBER = 2.71828182846;

    public static final ResourceLocation WIND_PARTICLE_PACKET = ResourceLocation.tryBuild(MOD_ID, "wind_particle_packet");
    //public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    private static DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(MOD_ID, Registries.PARTICLE_TYPE);

    //Block Entities
    public static BlockEntityType<HelmBlockEntity> HELM_BLOCK_ENTITY;
    public static BlockEntityType<RedstoneHelmBlockEntity> REDSTONE_HELM_BLOCK_ENTITY;

    public static RegistrySupplier<CreativeModeTab> SAILS_MAIN;
    public static RegistrySupplier<CreativeModeTab> SAILS_COLORS;

    //Particles
    public static SimpleParticleType WIND_PARTICLE;

    public static void init() {
        LOGGER.info("Common Init");
        ConfigUtils.checkConfigs();

        ValkyrienSkies.api().registerAttachment(ValkyrienSkies.api()
                .newAttachmentRegistrationBuilder(SailsShipControl.class)
                .useLegacySerializer()
                .build()
        );

        ValkyrienSkies.api().getShipLoadEvent().on(ship -> {
            SailsShipControl.getOrCreate(ship.getShip());
        });

        SAILS_MAIN = TABS.register("sails_main", () -> CreativeTabRegistry.create(Component.translatable("category.sails_main"), () -> new ItemStack(SailsBlocks.HELM_BLOCK.get().asItem())));
        SAILS_COLORS = TABS.register("sails_colors", () -> CreativeTabRegistry.create(Component.translatable("category.sails_colors"), () -> new ItemStack(SailsBlocks.CYAN_BUOY.get().asItem())));

        TABS.register();

        SailsBlocks.register();
        SailsItems.register();

        LifecycleEvent.SERVER_STARTED.register(ValkyrienSails::onServerStarted);
        TickEvent.SERVER_LEVEL_PRE.register(ValkyrienSails::onWorldTick);


        LOGGER.info("Sailing time.");
    }

    public static void InitializeVSWind() {
        LOGGER.info("The wind is blowing.");
        sailsWind = Boolean.parseBoolean(ConfigUtils.config.getOrDefault("wind-shows-no-sails","true")); //fixme unfinished
    }

    @SuppressWarnings("UnstableApiUsage")
    public static void onWorldTick(ServerLevel world) {
        if(tickCount == refreshRate) {
            tickCount = 0;

            VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().forEach(ship -> {
                if (ship != null) {
                    SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                    if (controller != null) {
                        controller.world = world;
                    }
                }
            });

            if (sailsWind) {
                //Spawn wind particles for all players being dragged by ships with a SailsShipControl attachment
                world.getServer().getPlayerList().getPlayers().forEach(serverPlayerEntity -> {
                    if (serverPlayerEntity instanceof IEntityDraggingInformationProvider player) {
                        if (player.getDraggingInformation().getLastShipStoodOn() != null) {
                            long shipId = player.getDraggingInformation().getLastShipStoodOn();
                            LoadedServerShip ship = (LoadedServerShip) VSGameUtilsKt.getShipObjectWorld(world).getLoadedShips().getById(shipId);
                            if (ship != null) {
                                SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
                                if (controller != null) {
                                    //serverPlayerEntity.displayClientMessage(ship.getAttachment(SailsShipControl.class).message, true);
                                    if (controller.numSails > 0) {
                                        if (player.getDraggingInformation().getTicksSinceStoodOnShip() < 100) {
                                            Vector3dc shipPos = ship.getTransform().getPositionInWorld(); //fixme make sure this is the world pos of the ship
                                            double windDir = Math.toRadians(ServerWindManager.getWindDirection(world, new Vec3(shipPos.x(), shipPos.y(), shipPos.z()))+180);
                                            world.sendParticles(serverPlayerEntity, ValkyrienSails.WIND_PARTICLE, false, serverPlayerEntity.getX()+15*Math.sin(windDir), serverPlayerEntity.getY()+25, serverPlayerEntity.getZ()+15*Math.sin(windDir), 10, 20, 10, 20, 0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
            }

        } else {
            tickCount++;
        }
    }

    private static Vec3 project(Vec3 vec1, Vec3 vec2) {
        return vec1.scale(vec1.dot(vec2) / pow(vec1.length(), 2));
    }

    public static void onServerStarted(MinecraftServer server) {
        WindDataReloadListener.loadFromServer(server);
        if (Boolean.parseBoolean(ConfigUtils.config.getOrDefault("enable-wind","true"))) {
            ServerWindManager.InitializeWind();
            ValkyrienSails.InitializeVSWind();
        }
    }
}
