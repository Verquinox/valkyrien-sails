package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.*;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.items.DedicationBottle;
import com.quintonc.vs_sails.items.SailWand;
import com.quintonc.vs_sails.mixin.BrewingRecipeRegistryMixin;
import com.quintonc.vs_sails.networking.WindModNetworking;
import com.quintonc.vs_sails.ship.SailsShipControl;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.*;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.potion.Potions;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.IEntityDraggingInformationProvider;

import static java.lang.Math.*;

public class ValkyrienSailsJava implements ModInitializer {

    private static int tickCount = 0;
    private static float maxWindSpeed;
    private static final int refreshRate = 4;
    public static final double EULERS_NUMBER = 2.71828182846;

    public static final String MOD_ID = "vs_sails";
    public static final Logger LOGGER = LoggerFactory.getLogger("vs_sails");

    public static final GameRules.Key<GameRules.IntRule> MAX_WIND_SPEED =
            GameRuleRegistry.register("maxWindSpeed", GameRules.Category.MISC, GameRuleFactory.createIntRule(32));

    public static final GameRules.Key<GameRules.BooleanRule> SAILS_USE_WIND =
            GameRuleRegistry.register("sailsUseWind", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    public static final RegistryKey<ItemGroup> SAILS_ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(MOD_ID, "item_group"));
    public static final ItemGroup SAILS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ValkyrienSailsJava.HELM_BLOCK.asItem()))
            .displayName(Text.of("Valkyrien Sails"))
            .build();


    @Override
    public void onInitialize() {
        //ConfigUtils.checkConfigs();
        //registerEntityThings();

        registerBlocks();
        registerItems();
        registerParticles();
        registerBrewingRecipes();

        //PatternProcessor.setupBasicPatterns();
        ModSounds.registerSounds();
        ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
        ServerTickEvents.START_WORLD_TICK.register(ValkyrienSailsJava::onWorldTick);
        LOGGER.info("The wind is blowing.");

        LOGGER.info("Sailing time.");

        //register item group
        Registry.register(Registries.ITEM_GROUP, SAILS_ITEM_GROUP_KEY, SAILS_ITEM_GROUP);

        //add items to item group
        ItemGroupEvents.modifyEntriesEvent(SAILS_ITEM_GROUP_KEY).register(itemGroup -> {
            itemGroup.add(ValkyrienSailsJava.SAIL_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.HELM_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.HELM_WHEEL.asItem());
            itemGroup.add(ValkyrienSailsJava.RIGGING_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.BALLAST_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.MAGIC_BALLAST_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.BUOY_BLOCK.asItem());
            //itemGroup.add(ValkyrienSailsJava.CANNONBALL);
            itemGroup.add(ValkyrienSailsJava.ROPE);

            //new items go here ^
        });
    }

    //entity registry stuff
    private void registerEntityThings() {}


    //block registry stuff
    public static final SailBlock SAIL_BLOCK = new SailBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL).nonOpaque());
    //public static final SailBlock SAIL_BLOCK = new SailBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL).nonOpaque());
    public static final HelmBlock HELM_BLOCK = new HelmBlock(AbstractBlock.Settings.copy(Blocks.SPRUCE_PLANKS).nonOpaque());
    public static final RiggingBlock RIGGING_BLOCK = new RiggingBlock(AbstractBlock.Settings.copy(Blocks.DARK_OAK_FENCE));
    public static final BallastBlock BALLAST_BLOCK = new BallastBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).resistance(0.0f));
    public static final MagicBallastBlock MAGIC_BALLAST_BLOCK = new MagicBallastBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).resistance(0.0f));
    public static final BuoyBlock BUOY_BLOCK = new BuoyBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL).resistance(0.0f));

    public static final DefaultParticleType WIND_PARTICLE = FabricParticleTypes.simple();

    //add new constants for blocks here ^
    private void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("vs_sails", "sail_block"), SAIL_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("vs_sails", "helm_block"), HELM_BLOCK);
        Registry.register(Registries.BLOCK,new Identifier("vs_sails","rigging_block"),RIGGING_BLOCK);
        Registry.register(Registries.BLOCK,new Identifier("vs_sails","ballast_block"),BALLAST_BLOCK);
        Registry.register(Registries.BLOCK,new Identifier("vs_sails","magic_ballast_block"),MAGIC_BALLAST_BLOCK);
        Registry.register(Registries.BLOCK,new Identifier("vs_sails","buoy_block"),BUOY_BLOCK);

        //register new blocks here ^
    }

    //item registry stuff
    public static final Item ROPE = new Item(new Item.Settings());
    public static final Item CANNONBALL = new Item(new Item.Settings());
    public static final Item HELM_WHEEL = new Item(new Item.Settings());
    public static final DedicationBottle DEDICATION_BOTTLE = new DedicationBottle(new Item.Settings());
    public static final SailWand SAIL_WAND = new SailWand(new Item.Settings());

    //add new constants for items here ^
    private void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("vs_sails","rope"), ROPE);
        Registry.register(Registries.ITEM, new Identifier("vs_sails","cannonball"),CANNONBALL);
        Registry.register(Registries.ITEM, new Identifier("vs_sails","helm_wheel"),HELM_WHEEL);
        Registry.register(Registries.ITEM, new Identifier("vs_sails","sail_wand"),SAIL_WAND);
        Registry.register(Registries.ITEM, new Identifier("vs_sails","dedication_bottle"),DEDICATION_BOTTLE);

        //register new items here ^

        Registry.register(Registries.ITEM, new Identifier("vs_sails","sail_block"), new BlockItem(SAIL_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("vs_sails","helm_block"), new BlockItem(HELM_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("vs_sails","rigging_block"),new BlockItem(RIGGING_BLOCK,new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("vs_sails","ballast_block"),new BlockItem(BALLAST_BLOCK,new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("vs_sails","magic_ballast_block"),new BlockItem(MAGIC_BALLAST_BLOCK,new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("vs_sails","buoy_block"),new BlockItem(BUOY_BLOCK,new Item.Settings()));

        //register new block items here ^
    }

    private void registerParticles() {
        Registry.register(Registries.PARTICLE_TYPE, new Identifier(ValkyrienSailsJava.MOD_ID, "wind_particle"), WIND_PARTICLE);
    }

    private void registerBrewingRecipes() {
        //BrewingRecipeRegistryMixin.invokeRegisterItemRecipe(Items.POTION, Items.DIAMOND, DEDICATION_BOTTLE);
    }

    //block entities go here
    public static final BlockEntityType<HelmBlockEntity> HELM_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("vs_sails", "helm_block_entity"),
            FabricBlockEntityTypeBuilder.create(HelmBlockEntity::new, HELM_BLOCK).build()
    );

    //entities would go here

    public static void InitializeVSWind(ServerWorld world) {
        System.out.println("VSWind Init");
        ServerTickEvents.START_WORLD_TICK.register(ValkyrienSailsJava::onWorldTick);
    }

    @SuppressWarnings("UnstableApiUsage")
    private static void onWorldTick(ServerWorld world) {
        if(tickCount == refreshRate) {
            tickCount = 0;

            //ServerShipWorldCore shipServer = VSGameUtilsKt.getShipObjectWorld(world);
            //QueryableShipData<LoadedServerShip> loadedShips = shipServer.getLoadedShips();

            maxWindSpeed = world.getGameRules().getInt(MAX_WIND_SPEED) / 10f;

//            double theta = Math.toRadians(ServerWindManager.getWindDirection());
//            double windStrength = ServerWindManager.getWindStrength();
//
//            if (windStrength < 0) {
//                windStrength *= -1;
//                theta += PI;
//            }
//
//
//            double finalWindStrength = windStrength;
//            double finalTheta = theta;

            //behavior for ships to be pushed by wind (not wanted atm)
//            loadedShips.forEach(ship -> {
//                if (Objects.equals(ship.getSlug(), "the-queer-quebecois")){
//                    if (ship.getAttachment(GameTickForceApplier.class) == null) {
//                        ship.saveAttachment(GameTickForceApplier.class, new GameTickForceApplier());
//                    }
//                    GameTickForceApplier forceApplier = ship.getAttachment(GameTickForceApplier.class);
//
//                    double shipMass = ship.getInertiaData().getMass();
//                    Vec3d shipVelocity = new Vec3d(ship.getVelocity().x(), 0, ship.getVelocity().z());
//
//                    if (shipVelocity.length() > 2) {
//
//                        Vec3d force = shipVelocity.multiply(shipMass/(shipVelocity.length()*2));
//
//                        assert forceApplier != null;
//                        forceApplier.applyInvariantForce(VectorConversionsMCKt.toJOML(
//                                force.multiply(refreshRate)));
//                    }
//                } else if (ship.getVelocity().length() < 2.5) {
//
//                    if (maxWindSpeed < 0.05f || finalWindStrength < 0.05) return;
//                    if (ship.getAttachment(GameTickForceApplier.class) == null) {
//                        ship.saveAttachment(GameTickForceApplier.class, new GameTickForceApplier());
//                    }
//                    GameTickForceApplier forceApplier = ship.getAttachment(GameTickForceApplier.class);
//
//                    double shipMass = ship.getInertiaData().getMass();
//                    Vec3d shipVelocity = new Vec3d(ship.getVelocity().x(), 0, ship.getVelocity().z());
//
//
//                    Vec3d wind = new Vec3d(Math.cos(finalTheta), 0, Math.sin(finalTheta))
//                            .multiply(finalWindStrength * maxWindSpeed);
//
//                    Vec3d relativeWind = wind.subtract(project(wind, shipVelocity));
//
//                    if (relativeWind.lengthSquared() > wind.lengthSquared()) {
//                        relativeWind = wind.multiply(min(1 / (finalWindStrength * 4), 1));
//                    }
//
//                    if (wind.add(relativeWind).lengthSquared() + 0.01 < wind.lengthSquared() && shipVelocity.length() > 0.25)
//                        return;
//
//                    Vec3d force = relativeWind.multiply(shipMass);
//
////                world.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
////                    serverPlayerEntity.sendMessage(Text.of("force: " + force.length()));
////                });
//
//                    assert forceApplier != null;
//                    forceApplier.applyInvariantForce(VectorConversionsMCKt.toJOML(
//                            force.multiply(refreshRate)));
//                }
//            });

            //Spawn wind particles for all players being dragged by ships with a SailsShipControl attachment
            world.getServer().getPlayerManager().getPlayerList().forEach(serverPlayerEntity -> {
                if (serverPlayerEntity instanceof IEntityDraggingInformationProvider player) {
                    if (player.getDraggingInformation().getLastShipStoodOn() != null) {
                        long shipId = player.getDraggingInformation().getLastShipStoodOn();
                        ServerShip ship = (ServerShip)VSGameUtilsKt.getAllShips(world).getById(shipId);
                        if (ship != null) {
                            if (ship.getAttachment(SailsShipControl.class) != null) {
                                if (player.getDraggingInformation().getTicksSinceStoodOnShip() < 100) {
                                    world.spawnParticles(serverPlayerEntity, ValkyrienSailsJava.WIND_PARTICLE, false, serverPlayerEntity.getX(), serverPlayerEntity.getY()+20, serverPlayerEntity.getZ(), 10, 20, 10, 20, 0);

                                }
                            }
                        }
                    }
                }
                //serverPlayerEntity.sendMessage(Text.of("force: "));
            });



        } else {
            tickCount++;
        }
    }

    private static Vec3d project(Vec3d vec1, Vec3d vec2) {
        return vec1.multiply(vec1.dotProduct(vec2) / pow(vec1.length(), 2));
    }

    private void onServerStarted(MinecraftServer server) {
        ServerWindManager.InitializeWind(server.getOverworld());
        ValkyrienSailsJava.InitializeVSWind(server.getOverworld());
        WindModNetworking.networkingInit();
    }
}
