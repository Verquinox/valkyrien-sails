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
    public static RegistrySupplier<SailBlock> RED_SAIL;

    public static RegistrySupplier<HelmBlock> HELM_BLOCK;
    public static RegistrySupplier<HelmWheel> HELM_WHEEL;
    public static RegistrySupplier<RiggingBlock> RIGGING_BLOCK;
    public static RegistrySupplier<BallastBlock> BALLAST_BLOCK;
    public static RegistrySupplier<MagicBallastBlock> MAGIC_BALLAST_BLOCK;
    public static RegistrySupplier<BuoyBlock> BUOY_BLOCK;

    public static void register() {
        SAIL_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "sail_block"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        WHITE_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));
        RED_SAIL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_sail"), () -> new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion()));

        HELM_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_block"), () -> new HelmBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        HELM_WHEEL = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_wheel"), () -> new HelmWheel(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion()));
        RIGGING_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rigging_block"), () -> new RiggingBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_FENCE)));
        BALLAST_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "ballast_block"), () -> new BallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f)));
        MAGIC_BALLAST_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magic_ballast_block"), () -> new MagicBallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f)));
        BUOY_BLOCK = BLOCKS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "buoy_block"), () -> new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f)));

        BLOCKS.register();
    }
    public static void registerItems(DeferredRegister<Item> items) {
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "sail_block"), () -> new BlockItem(SAIL_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "white_sail"), () -> new BlockItem(WHITE_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "red_sail"), () -> new BlockItem(RED_SAIL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));

        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_block"), () -> new BlockItem(HELM_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "helm_wheel"), () -> new BlockItem(HELM_WHEEL.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rigging_block"), () -> new BlockItem(RIGGING_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "ballast_block"), () -> new BlockItem(BALLAST_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "magic_ballast_block"), () -> new BlockItem(MAGIC_BALLAST_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));
        items.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "buoy_block"), () -> new BlockItem(BUOY_BLOCK.get(), new Item.Properties().arch$tab(ValkyrienSails.SAILS_TAB)));

    }
}
