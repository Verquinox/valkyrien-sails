package com.quintonc.vs_sails;


import com.quintonc.vs_sails.blocks.*;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.items.DedicationBottle;
import com.quintonc.vs_sails.items.SailWand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.Component;
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

    public static final ResourceKey<CreativeModeTab> SAILS_ITEM_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "item_group"));
    public static final CreativeModeTab SAILS_ITEM_GROUP = FabricItemGroup.builder()
            .icon(() -> new ItemStack(ValkyrienSailsFabric.HELM_BLOCK.asItem()))
            .title(Component.literal("Valkyrien Sails"))
            .build();


    @Override
    public void onInitialize() {

        //registerEntityThings();
        ValkyrienSails.init();

        ServerLifecycleEvents.SERVER_STARTED.register(ValkyrienSails::onServerStarted);
        ServerTickEvents.START_WORLD_TICK.register(ValkyrienSails::onWorldTick);

        registerBlocks();
        ValkyrienSails.HELM_BLOCK_ENTITY = HELM_BLOCK_ENTITY;
        registerItems();
        registerParticles();
        registerBrewingRecipes();

        //PatternProcessor.setupBasicPatterns();
        ModSounds.registerSounds();

        //register item group
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, SAILS_ITEM_GROUP_KEY, SAILS_ITEM_GROUP);

        //add items to item group
        ItemGroupEvents.modifyEntriesEvent(SAILS_ITEM_GROUP_KEY).register(itemGroup -> {
            itemGroup.accept(ValkyrienSailsFabric.SAIL_BLOCK.asItem());
            itemGroup.accept(ValkyrienSailsFabric.HELM_BLOCK.asItem());
            itemGroup.accept(ValkyrienSailsFabric.HELM_WHEEL.asItem());
            itemGroup.accept(ValkyrienSailsFabric.RIGGING_BLOCK.asItem());
            itemGroup.accept(ValkyrienSailsFabric.BALLAST_BLOCK.asItem());
            itemGroup.accept(ValkyrienSailsFabric.MAGIC_BALLAST_BLOCK.asItem());
            itemGroup.accept(ValkyrienSailsFabric.BUOY_BLOCK.asItem());
            //itemGroup.accept(ValkyrienSailsFabric.CANNONBALL);
            itemGroup.accept(ValkyrienSailsFabric.DEDICATION_BOTTLE);
            itemGroup.accept(ValkyrienSailsFabric.ROPE);

            //new items go here ^
        });
    }

    //entity registry stuff
    private void registerEntityThings() {}


    //block registry stuff
    public static final SailBlock SAIL_BLOCK = new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).noOcclusion());
    //public static final SailBlock SAIL_BLOCK = new SailBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).nonOpaque());
    public static final HelmBlock HELM_BLOCK = new HelmBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_PLANKS).noOcclusion());
    public static final RiggingBlock RIGGING_BLOCK = new RiggingBlock(BlockBehaviour.Properties.copy(Blocks.DARK_OAK_FENCE));
    public static final BallastBlock BALLAST_BLOCK = new BallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f));
    public static final MagicBallastBlock MAGIC_BALLAST_BLOCK = new MagicBallastBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).explosionResistance(0.0f));
    public static final BuoyBlock BUOY_BLOCK = new BuoyBlock(BlockBehaviour.Properties.copy(Blocks.WHITE_WOOL).explosionResistance(0.0f));

    public static final SimpleParticleType WIND_PARTICLE = FabricParticleTypes.simple();

    //add new constants for blocks here ^
    private void registerBlocks() {
        ValkyrienSails.SAIL_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("vs_sails", "sail_block"), SAIL_BLOCK);
        ValkyrienSails.HELM_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("vs_sails", "helm_block"), HELM_BLOCK);
        ValkyrienSails.RIGGING_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("vs_sails","rigging_block"),RIGGING_BLOCK);
        ValkyrienSails.BALLAST_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("vs_sails","ballast_block"),BALLAST_BLOCK);
        ValkyrienSails.MAGIC_BALLAST_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("vs_sails","magic_ballast_block"),MAGIC_BALLAST_BLOCK);
        ValkyrienSails.BUOY_BLOCK = Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("vs_sails","buoy_block"),BUOY_BLOCK);

        //register new blocks here ^
    }

    //item registry stuff
    public static final Item ROPE = new Item(new Item.Properties());
    public static final Item CANNONBALL = new Item(new Item.Properties());
    public static final Item HELM_WHEEL = new Item(new Item.Properties());
    public static final DedicationBottle DEDICATION_BOTTLE = new DedicationBottle(new Item.Properties());
    public static final SailWand SAIL_WAND = new SailWand(new Item.Properties());

    //add new constants for items here ^
    private void registerItems() {
        ValkyrienSails.ROPE = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","rope"), ROPE);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","cannonball"),CANNONBALL);
        ValkyrienSails.HELM_WHEEL = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","helm_wheel"),HELM_WHEEL);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","sail_wand"),SAIL_WAND);
        ValkyrienSails.DEDICATION_BOTTLE = Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","dedication_bottle"),DEDICATION_BOTTLE);

        //register new items here ^

        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","sail_block"), new BlockItem(SAIL_BLOCK, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","helm_block"), new BlockItem(HELM_BLOCK, new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","rigging_block"),new BlockItem(RIGGING_BLOCK,new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","ballast_block"),new BlockItem(BALLAST_BLOCK,new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","magic_ballast_block"),new BlockItem(MAGIC_BALLAST_BLOCK,new Item.Properties()));
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation("vs_sails","buoy_block"),new BlockItem(BUOY_BLOCK,new Item.Properties()));

        //register new block items here ^
    }

    private void registerParticles() {
        ValkyrienSails.WIND_PARTICLE = Registry.register(BuiltInRegistries.PARTICLE_TYPE, new ResourceLocation(ValkyrienSails.MOD_ID, "wind_particle"), WIND_PARTICLE);
    }

    private void registerBrewingRecipes() {
        //BrewingRecipeRegistryMixin.invokeRegisterItemRecipe(Items.POTION, Items.DIAMOND, DEDICATION_BOTTLE);
    }

    //block entities go here
    public static final BlockEntityType<HelmBlockEntity> HELM_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation("vs_sails", "helm_block_entity"),
            FabricBlockEntityTypeBuilder.create(HelmBlockEntity::new, HELM_BLOCK).build()
    );

    //entities would go here


}
