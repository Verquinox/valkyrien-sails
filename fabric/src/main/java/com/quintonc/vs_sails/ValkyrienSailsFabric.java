package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.RedstoneHelmBlockEntity;
import com.quintonc.vs_sails.items.SailWand;
import com.quintonc.vs_sails.registration.SailsBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.fabricmc.fabric.api.registry.FlammableBlockRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValkyrienSailsFabric implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("vs_sails_fabric");

    public static final GameRules.Key<GameRules.IntegerValue> MAX_WIND_SPEED =
            GameRuleRegistry.register("maxWindSpeed", GameRules.Category.MISC, GameRuleFactory.createIntRule(32));

    public static final GameRules.Key<GameRules.BooleanValue> SAILS_USE_WIND =
            GameRuleRegistry.register("sailsUseWind", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

    @Override
    public void onInitialize() {

        ValkyrienSails.init();

        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.SAIL_BLOCK.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.ROPE_BLOCK.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BUOY_BLOCK.get(), 5, 20);

        //Yes, I know this is disgusting
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.WHITE_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.LIGHT_GRAY_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.GRAY_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BLACK_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BROWN_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.RED_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.ORANGE_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.YELLOW_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.LIME_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.GREEN_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.CYAN_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.LIGHT_BLUE_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BLUE_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.PURPLE_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.MAGENTA_SAIL.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.PINK_SAIL.get(), 5, 20);

        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.WHITE_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.LIGHT_GRAY_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.GRAY_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BLACK_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BROWN_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.RED_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.ORANGE_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.YELLOW_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.LIME_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.GREEN_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.CYAN_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.LIGHT_BLUE_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.BLUE_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.PURPLE_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.MAGENTA_BUOY.get(), 5, 20);
        FlammableBlockRegistry.getDefaultInstance().add(SailsBlocks.PINK_BUOY.get(), 5, 20);


        //registerItems();
        registerBlockEntities();
        registerParticles();
        ModSounds.registerSounds();
    }

    public static final SimpleParticleType WIND_PARTICLE = FabricParticleTypes.simple();

    //item registry stuff
    //public static final Item CANNONBALL = new Item(new Item.Properties());
    //public static final SailWand SAIL_WAND = new SailWand(new Item.Properties());

    //add new constants for items here ^

//    private void registerItems() {
//        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","cannonball"),CANNONBALL);
//        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","sail_wand"),SAIL_WAND);
//
//    }

    private void registerParticles() {
        ValkyrienSails.WIND_PARTICLE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(ValkyrienSails.MOD_ID, "wind_particle"), WIND_PARTICLE);
    }

    public static BlockEntityType<HelmBlockEntity> HELM_BLOCK_ENTITY;
    public static BlockEntityType<RedstoneHelmBlockEntity> REDSTONE_HELM_BLOCK_ENTITY;

    public static void registerBlockEntities() {
        //block entities go here
        HELM_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation("vs_sails", "helm_block_entity"),
                FabricBlockEntityTypeBuilder.create(
                        HelmBlockEntity::new,
                        SailsBlocks.HELM_BLOCK.get(),
                        SailsBlocks.OAK_HELM.get(),
                        SailsBlocks.SPRUCE_HELM.get(),
                        SailsBlocks.BIRCH_HELM.get(),
                        SailsBlocks.JUNGLE_HELM.get(),
                        SailsBlocks.DARK_OAK_HELM.get(),
                        SailsBlocks.ACACIA_HELM.get()).build()
        );
        ValkyrienSails.HELM_BLOCK_ENTITY = HELM_BLOCK_ENTITY;

        REDSTONE_HELM_BLOCK_ENTITY = Registry.register(
                BuiltInRegistries.BLOCK_ENTITY_TYPE,
                new ResourceLocation("vs_sails", "redstone_helm_block_entity"),
                FabricBlockEntityTypeBuilder.create(RedstoneHelmBlockEntity::new, SailsBlocks.REDSTONE_HELM_BLOCK.get()).build()
        );
        ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY = REDSTONE_HELM_BLOCK_ENTITY;
    }
}
