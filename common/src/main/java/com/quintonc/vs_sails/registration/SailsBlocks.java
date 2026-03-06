package com.quintonc.vs_sails.registration;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SailsBlocks {
    public static DeferredRegister<Block> BLOCKS = DeferredRegister.create(ValkyrienSails.MOD_ID, Registries.BLOCK);


    public static RegistrySupplier<SailBlock> SAIL_BLOCK;
    public static RegistrySupplier<SailBlock> WHITE_SAIL;
    public static RegistrySupplier<SailBlock> LIGHT_GRAY_SAIL;
    public static RegistrySupplier<SailBlock> GRAY_SAIL;
    public static RegistrySupplier<SailBlock> BLACK_SAIL;
    public static RegistrySupplier<SailBlock> BROWN_SAIL;
    public static RegistrySupplier<SailBlock> RED_SAIL;
    public static RegistrySupplier<SailBlock> ORANGE_SAIL;
    public static RegistrySupplier<SailBlock> YELLOW_SAIL;
    public static RegistrySupplier<SailBlock> LIME_SAIL;
    public static RegistrySupplier<SailBlock> GREEN_SAIL;
    public static RegistrySupplier<SailBlock> CYAN_SAIL;
    public static RegistrySupplier<SailBlock> LIGHT_BLUE_SAIL;
    public static RegistrySupplier<SailBlock> BLUE_SAIL;
    public static RegistrySupplier<SailBlock> PURPLE_SAIL;
    public static RegistrySupplier<SailBlock> MAGENTA_SAIL;
    public static RegistrySupplier<SailBlock> PINK_SAIL;

    public static RegistrySupplier<SailBlock> MAGMA_SAIL_BLOCK;
    public static RegistrySupplier<SailBlock> WHITE_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> LIGHT_GRAY_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> GRAY_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> BLACK_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> BROWN_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> RED_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> ORANGE_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> YELLOW_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> LIME_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> GREEN_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> CYAN_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> LIGHT_BLUE_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> BLUE_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> PURPLE_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> MAGENTA_MAGMA_SAIL;
    public static RegistrySupplier<SailBlock> PINK_MAGMA_SAIL;

    //public static RegistrySupplier<FurledSailBlock> FURLED_SAIL_BLOCK;

    public static RegistrySupplier<HelmBlock> OAK_HELM;
    public static RegistrySupplier<HelmBlock> SPRUCE_HELM;
    public static RegistrySupplier<HelmBlock> BIRCH_HELM;
    public static RegistrySupplier<HelmBlock> JUNGLE_HELM;
    public static RegistrySupplier<HelmBlock> DARK_OAK_HELM;
    public static RegistrySupplier<HelmBlock> ACACIA_HELM;
    public static RegistrySupplier<HelmBlock> MANGROVE_HELM;
    public static RegistrySupplier<HelmBlock> CHERRY_HELM;
    public static RegistrySupplier<HelmBlock> CRIMSON_HELM;
    public static RegistrySupplier<HelmBlock> WARPED_HELM;
    public static RegistrySupplier<HelmBlock> BAMBOO_HELM;

    public static RegistrySupplier<HelmWheel> OAK_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> SPRUCE_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> BIRCH_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> JUNGLE_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> DARK_OAK_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> ACACIA_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> MANGROVE_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> CHERRY_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> CRIMSON_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> WARPED_HELM_WHEEL;
    public static RegistrySupplier<HelmWheel> BAMBOO_HELM_WHEEL;

    public static RegistrySupplier<RopeBlock> ROPE_BLOCK;
    public static RegistrySupplier<RopeBlock> MAGMA_ROPE_BLOCK;

    public static RegistrySupplier<HelmBlock> HELM_BLOCK;
    public static RegistrySupplier<HelmWheel> HELM_WHEEL;
    public static RegistrySupplier<RedstoneHelmBlock> REDSTONE_HELM_BLOCK;
    public static RegistrySupplier<HelmWheel> REDSTONE_HELM_WHEEL;
    public static RegistrySupplier<RiggingBlock> RIGGING_BLOCK;
    public static RegistrySupplier<RiggingBlock> MAGMA_RIGGING_BLOCK;
    public static RegistrySupplier<BallastBlock> BALLAST_BLOCK;
    public static RegistrySupplier<MagicBallastBlock> MAGIC_BALLAST_BLOCK;

    public static RegistrySupplier<BuoyBlock> BUOY_BLOCK;
    public static RegistrySupplier<BuoyBlock> WHITE_BUOY;
    public static RegistrySupplier<BuoyBlock> LIGHT_GRAY_BUOY;
    public static RegistrySupplier<BuoyBlock> GRAY_BUOY;
    public static RegistrySupplier<BuoyBlock> BLACK_BUOY;
    public static RegistrySupplier<BuoyBlock> BROWN_BUOY;
    public static RegistrySupplier<BuoyBlock> RED_BUOY;
    public static RegistrySupplier<BuoyBlock> ORANGE_BUOY;
    public static RegistrySupplier<BuoyBlock> YELLOW_BUOY;
    public static RegistrySupplier<BuoyBlock> LIME_BUOY;
    public static RegistrySupplier<BuoyBlock> GREEN_BUOY;
    public static RegistrySupplier<BuoyBlock> CYAN_BUOY;
    public static RegistrySupplier<BuoyBlock> LIGHT_BLUE_BUOY;
    public static RegistrySupplier<BuoyBlock> BLUE_BUOY;
    public static RegistrySupplier<BuoyBlock> PURPLE_BUOY;
    public static RegistrySupplier<BuoyBlock> MAGENTA_BUOY;
    public static RegistrySupplier<BuoyBlock> PINK_BUOY;

    public static void register() {
        SAIL_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "sail_block"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        WHITE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        LIGHT_GRAY_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        GRAY_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        BLACK_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        BROWN_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        RED_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        ORANGE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        YELLOW_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        LIME_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        GREEN_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        CYAN_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        LIGHT_BLUE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL).ignitedByLava().noOcclusion().isValidSpawn(SailsBlocks::never)));
        BLUE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        PURPLE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        MAGENTA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));
        PINK_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL).noOcclusion().ignitedByLava().isValidSpawn(SailsBlocks::never)));

        MAGMA_SAIL_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magma_sail_block"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        WHITE_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        LIGHT_GRAY_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        GRAY_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        BLACK_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        BROWN_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        RED_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        ORANGE_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        YELLOW_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.YELLOW_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        LIME_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.LIME_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        GREEN_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.GREEN_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        CYAN_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.CYAN_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        LIGHT_BLUE_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_BLUE_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        BLUE_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.BLUE_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        PURPLE_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.PURPLE_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        MAGENTA_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.MAGENTA_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));
        PINK_MAGMA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_magma_sail"), () -> new MagmaSailBlock(BlockBehaviour.Properties.copy(Blocks.PINK_WOOL).noOcclusion().isValidSpawn(SailsBlocks::never)));

        OAK_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "oak_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
        SPRUCE_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "spruce_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        BIRCH_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "birch_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS).noOcclusion()));
        JUNGLE_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "jungle_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS).noOcclusion()));
        DARK_OAK_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "dark_oak_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).noOcclusion()));
        ACACIA_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "acacia_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS).noOcclusion()));
        MANGROVE_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "mangrove_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.MANGROVE_PLANKS).noOcclusion()));
        CHERRY_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cherry_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.CHERRY_PLANKS).noOcclusion()));
        CRIMSON_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "crimson_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS).noOcclusion()));
        WARPED_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "warped_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion()));
        BAMBOO_HELM = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "bamboo_helm"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.BAMBOO_PLANKS).noOcclusion()));

        OAK_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "oak_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));
        SPRUCE_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "spruce_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        BIRCH_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "birch_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.BIRCH_PLANKS).noOcclusion()));
        JUNGLE_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "jungle_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.JUNGLE_PLANKS).noOcclusion()));
        DARK_OAK_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "dark_oak_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_PLANKS).noOcclusion()));
        ACACIA_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "acacia_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.ACACIA_PLANKS).noOcclusion()));
        MANGROVE_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "mangrove_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.MANGROVE_PLANKS).noOcclusion()));
        CHERRY_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cherry_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.CHERRY_PLANKS).noOcclusion()));
        CRIMSON_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "crimson_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.CRIMSON_PLANKS).noOcclusion()));
        WARPED_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "warped_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.WARPED_PLANKS).noOcclusion()));
        BAMBOO_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "bamboo_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.BAMBOO_PLANKS).noOcclusion()));

        ROPE_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rope_block"), () -> new RopeBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL).noOcclusion().explosionResistance(0.0f).instabreak()));
        MAGMA_ROPE_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magma_rope_block"), () -> new MagmaRopeBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL).noOcclusion().explosionResistance(0.0f).instabreak()));

        HELM_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_block"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        REDSTONE_HELM_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "redstone_helm_block"), () -> new RedstoneHelmBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        REDSTONE_HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "redstone_helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        RIGGING_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rigging_block"), () -> new RiggingBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_FENCE)));
        MAGMA_RIGGING_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magma_rigging_block"), () -> new MagmaRiggingBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_FENCE)));
        BALLAST_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "ballast_block"), () -> new BallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f)));
        MAGIC_BALLAST_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magic_ballast_block"), () -> new MagicBallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f)));

        BUOY_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "buoy_block"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        WHITE_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        LIGHT_GRAY_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        GRAY_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        BLACK_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        BROWN_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        RED_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        ORANGE_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        YELLOW_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        LIME_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        GREEN_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        CYAN_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        LIGHT_BLUE_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        BLUE_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        PURPLE_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        MAGENTA_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));
        PINK_BUOY = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_buoy"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));

        BLOCKS.register();
    }
    public static void registerItems(DeferredRegister<Item> items) {
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "sail_block"), () -> new BlockItem(SAIL_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_sail"), () -> new BlockItem(WHITE_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_sail"), () -> new BlockItem(LIGHT_GRAY_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_sail"), () -> new BlockItem(GRAY_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_sail"), () -> new BlockItem(BLACK_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_sail"), () -> new BlockItem(BROWN_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_sail"), () -> new BlockItem(RED_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_sail"), () -> new BlockItem(ORANGE_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_sail"), () -> new BlockItem(YELLOW_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_sail"), () -> new BlockItem(LIME_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_sail"), () -> new BlockItem(GREEN_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_sail"), () -> new BlockItem(CYAN_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_sail"), () -> new BlockItem(LIGHT_BLUE_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_sail"), () -> new BlockItem(BLUE_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_sail"), () -> new BlockItem(PURPLE_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_sail"), () -> new BlockItem(MAGENTA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_sail"), () -> new BlockItem(PINK_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magma_sail_block"), () -> new BlockItem(MAGMA_SAIL_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_magma_sail"), () -> new BlockItem(WHITE_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_magma_sail"), () -> new BlockItem(LIGHT_GRAY_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_magma_sail"), () -> new BlockItem(GRAY_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_magma_sail"), () -> new BlockItem(BLACK_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_magma_sail"), () -> new BlockItem(BROWN_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_magma_sail"), () -> new BlockItem(RED_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_magma_sail"), () -> new BlockItem(ORANGE_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_magma_sail"), () -> new BlockItem(YELLOW_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_magma_sail"), () -> new BlockItem(LIME_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_magma_sail"), () -> new BlockItem(GREEN_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_magma_sail"), () -> new BlockItem(CYAN_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_magma_sail"), () -> new BlockItem(LIGHT_BLUE_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_magma_sail"), () -> new BlockItem(BLUE_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_magma_sail"), () -> new BlockItem(PURPLE_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_magma_sail"), () -> new BlockItem(MAGENTA_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_magma_sail"), () -> new BlockItem(PINK_MAGMA_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "oak_helm"), () -> new BlockItem(OAK_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "spruce_helm"), () -> new BlockItem(SPRUCE_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "birch_helm"), () -> new BlockItem(BIRCH_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "jungle_helm"), () -> new BlockItem(JUNGLE_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "dark_oak_helm"), () -> new BlockItem(DARK_OAK_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "acacia_helm"), () -> new BlockItem(ACACIA_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "mangrove_helm"), () -> new BlockItem(MANGROVE_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cherry_helm"), () -> new BlockItem(CHERRY_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "crimson_helm"), () -> new BlockItem(CRIMSON_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "warped_helm"), () -> new BlockItem(WARPED_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "bamboo_helm"), () -> new BlockItem(BAMBOO_HELM.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "oak_helm_wheel"), () -> new BlockItem(OAK_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "spruce_helm_wheel"), () -> new BlockItem(SPRUCE_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "birch_helm_wheel"), () -> new BlockItem(BIRCH_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "jungle_helm_wheel"), () -> new BlockItem(JUNGLE_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "dark_oak_helm_wheel"), () -> new BlockItem(DARK_OAK_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "acacia_helm_wheel"), () -> new BlockItem(ACACIA_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "mangrove_helm_wheel"), () -> new BlockItem(MANGROVE_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cherry_helm_wheel"), () -> new BlockItem(CHERRY_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "crimson_helm_wheel"), () -> new BlockItem(CRIMSON_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "warped_helm_wheel"), () -> new BlockItem(WARPED_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "bamboo_helm_wheel"), () -> new BlockItem(BAMBOO_HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rope"), () -> new BlockItem(ROPE_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magma_rope"), () -> new BlockItem(MAGMA_ROPE_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_block"), () -> new BlockItem(HELM_BLOCK.get(), new Item.Properties()));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_wheel"), () -> new BlockItem(HELM_WHEEL.get(), new Item.Properties()));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "redstone_helm_block"), () -> new BlockItem(REDSTONE_HELM_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "redstone_helm_wheel"), () -> new BlockItem(REDSTONE_HELM_WHEEL.get(), new Item.Properties()));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rigging_block"), () -> new BlockItem(RIGGING_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magma_rigging_block"), () -> new BlockItem(MAGMA_RIGGING_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "ballast_block"), () -> new BlockItem(BALLAST_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magic_ballast_block"), () -> new BlockItem(MAGIC_BALLAST_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "buoy_block"), () -> new BlockItem(BUOY_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_buoy"), () -> new BlockItem(WHITE_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_buoy"), () -> new BlockItem(LIGHT_GRAY_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_buoy"), () -> new BlockItem(GRAY_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_buoy"), () -> new BlockItem(BLACK_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_buoy"), () -> new BlockItem(BROWN_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_buoy"), () -> new BlockItem(RED_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_buoy"), () -> new BlockItem(ORANGE_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_buoy"), () -> new BlockItem(YELLOW_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_buoy"), () -> new BlockItem(LIME_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_buoy"), () -> new BlockItem(GREEN_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_buoy"), () -> new BlockItem(CYAN_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_buoy"), () -> new BlockItem(LIGHT_BLUE_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_buoy"), () -> new BlockItem(BLUE_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_buoy"), () -> new BlockItem(PURPLE_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_buoy"), () -> new BlockItem(MAGENTA_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_buoy"), () -> new BlockItem(PINK_BUOY.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_COLORS)));

    }

    public static Block getMagmaSailBlock(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (!ValkyrienSails.MOD_ID.equals(blockId.getNamespace())) {
            return null;
        }

        return switch (blockId.getPath()) {
            case "sail_block" -> MAGMA_SAIL_BLOCK.get();
            case "white_sail" -> WHITE_MAGMA_SAIL.get();
            case "light_gray_sail" -> LIGHT_GRAY_MAGMA_SAIL.get();
            case "gray_sail" -> GRAY_MAGMA_SAIL.get();
            case "black_sail" -> BLACK_MAGMA_SAIL.get();
            case "brown_sail" -> BROWN_MAGMA_SAIL.get();
            case "red_sail" -> RED_MAGMA_SAIL.get();
            case "orange_sail" -> ORANGE_MAGMA_SAIL.get();
            case "yellow_sail" -> YELLOW_MAGMA_SAIL.get();
            case "lime_sail" -> LIME_MAGMA_SAIL.get();
            case "green_sail" -> GREEN_MAGMA_SAIL.get();
            case "cyan_sail" -> CYAN_MAGMA_SAIL.get();
            case "light_blue_sail" -> LIGHT_BLUE_MAGMA_SAIL.get();
            case "blue_sail" -> BLUE_MAGMA_SAIL.get();
            case "purple_sail" -> PURPLE_MAGMA_SAIL.get();
            case "magenta_sail" -> MAGENTA_MAGMA_SAIL.get();
            case "pink_sail" -> PINK_MAGMA_SAIL.get();
            default -> null;
        };
    }

    public static Block getRegularSailBlock(Block block) {
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);
        if (!ValkyrienSails.MOD_ID.equals(blockId.getNamespace())) {
            return null;
        }

        return switch (blockId.getPath()) {
            case "magma_sail_block" -> SAIL_BLOCK.get();
            case "white_magma_sail" -> WHITE_SAIL.get();
            case "light_gray_magma_sail" -> LIGHT_GRAY_SAIL.get();
            case "gray_magma_sail" -> GRAY_SAIL.get();
            case "black_magma_sail" -> BLACK_SAIL.get();
            case "brown_magma_sail" -> BROWN_SAIL.get();
            case "red_magma_sail" -> RED_SAIL.get();
            case "orange_magma_sail" -> ORANGE_SAIL.get();
            case "yellow_magma_sail" -> YELLOW_SAIL.get();
            case "lime_magma_sail" -> LIME_SAIL.get();
            case "green_magma_sail" -> GREEN_SAIL.get();
            case "cyan_magma_sail" -> CYAN_SAIL.get();
            case "light_blue_magma_sail" -> LIGHT_BLUE_SAIL.get();
            case "blue_magma_sail" -> BLUE_SAIL.get();
            case "purple_magma_sail" -> PURPLE_SAIL.get();
            case "magenta_magma_sail" -> MAGENTA_SAIL.get();
            case "pink_magma_sail" -> PINK_SAIL.get();
            default -> null;
        };
    }

    private static Boolean never(BlockState state, BlockGetter blockGetter, BlockPos pos, EntityType<?> entity) {
        return false;
    }
}
