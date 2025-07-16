package com.quintonc.vs_sails.registration;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.*;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

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

    public static RegistrySupplier<HelmBlock> HELM_BLOCK;
    public static RegistrySupplier<HelmWheel> HELM_WHEEL;
    public static RegistrySupplier<RiggingBlock> RIGGING_BLOCK;
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
        SAIL_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "sail_block"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        WHITE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        LIGHT_GRAY_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_gray_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.LIGHT_GRAY_WOOL).noOcclusion()));
        GRAY_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "gray_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.GRAY_WOOL).noOcclusion()));
        BLACK_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "black_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.BLACK_WOOL).noOcclusion()));
        BROWN_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "brown_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_WOOL).noOcclusion()));
        RED_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.RED_WOOL).noOcclusion()));
        ORANGE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "orange_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.ORANGE_WOOL).noOcclusion()));
        YELLOW_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "yellow_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        LIME_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "lime_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        GREEN_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "green_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        CYAN_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "cyan_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion().ignitedByLava()));
        LIGHT_BLUE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "light_blue_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        BLUE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "blue_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        PURPLE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "purple_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        MAGENTA_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magenta_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        PINK_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "pink_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));

        HELM_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_block"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        RIGGING_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rigging_block"), () -> new RiggingBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_FENCE)));
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

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_block"), () -> new BlockItem(HELM_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_wheel"), () -> new BlockItem(HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rigging_block"), () -> new BlockItem(RIGGING_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
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
}
