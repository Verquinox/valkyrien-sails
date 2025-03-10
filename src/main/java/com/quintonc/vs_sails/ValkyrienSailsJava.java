package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.*;
import com.quintonc.vs_sails.blocks.entity.BallastBlockEntity;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.SailBlockEntity;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValkyrienSailsJava implements ModInitializer {



    public static final String MOD_ID = "vs_sails";
    public static final Logger LOGGER = LoggerFactory.getLogger("vs_sails");

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

        //PatternProcessor.setupBasicPatterns();
        //ModSounds.registerSounds();
        LOGGER.info("Sailing time.");

        //register item group
        Registry.register(Registries.ITEM_GROUP, SAILS_ITEM_GROUP_KEY, SAILS_ITEM_GROUP);

        //add items to item group
        ItemGroupEvents.modifyEntriesEvent(SAILS_ITEM_GROUP_KEY).register(itemGroup -> {
            itemGroup.add(ValkyrienSailsJava.SAIL_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.HELM_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.RIGGING_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.BALLAST_BLOCK.asItem());
            itemGroup.add(ValkyrienSailsJava.CANNONBALL);

            //new items go here ^
        });
    }

    //entity registry stuff
    private void registerEntityThings() {}


    //block registry stuff
    public static final SailBlock SAIL_BLOCK = new SailBlock(AbstractBlock.Settings.copy(Blocks.WHITE_WOOL).nonOpaque());
    public static final HelmBlock HELM_BLOCK = new HelmBlock(AbstractBlock.Settings.copy(Blocks.BIRCH_PLANKS).nonOpaque());
    public static final RiggingBlock RIGGING_BLOCK = new RiggingBlock(AbstractBlock.Settings.copy(Blocks.DARK_OAK_FENCE));
    public static final BallastBlock BALLAST_BLOCK = new BallastBlock(AbstractBlock.Settings.copy(Blocks.IRON_BLOCK));

    //add new constants for blocks here ^
    private void registerBlocks() {
        Registry.register(Registries.BLOCK, new Identifier("vs_sails", "sail_block"), SAIL_BLOCK);
        Registry.register(Registries.BLOCK, new Identifier("vs_sails", "helm_block"), HELM_BLOCK);
        Registry.register(Registries.BLOCK,new Identifier("vs_sails","rigging_block"),RIGGING_BLOCK);
        Registry.register(Registries.BLOCK,new Identifier("vs_sails","ballast_block"),BALLAST_BLOCK);

        //register new blocks here ^
    }

    //item registry stuff
    public static final Item ROPE = new Item(new Item.Settings());
    public static final Item CANNONBALL = new Item(new Item.Settings());

    //add new constants for items here ^
    private void registerItems() {
        Registry.register(Registries.ITEM, new Identifier("vs_sails", "rope"), ROPE);
        Registry.register(Registries.ITEM,new Identifier("vs_sails","cannonball"),CANNONBALL);

        //register new items here ^

        Registry.register(Registries.ITEM, new Identifier("vs_sails", "sail_block"), new BlockItem(SAIL_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM, new Identifier("vs_sails", "helm_block"), new BlockItem(HELM_BLOCK, new Item.Settings()));
        Registry.register(Registries.ITEM,new Identifier("vs_sails","rigging_block"),new BlockItem(RIGGING_BLOCK,new Item.Settings()));
        Registry.register(Registries.ITEM,new Identifier("vs_sails","ballast_block"),new BlockItem(BALLAST_BLOCK,new Item.Settings()));

        //register new block items here ^
    }

    //block entities go here
    public static final BlockEntityType<SailBlockEntity> SAIL_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("vs_sails", "sail_block_entity"),
            FabricBlockEntityTypeBuilder.create(SailBlockEntity::new, SAIL_BLOCK).build()
    );
    public static final BlockEntityType<HelmBlockEntity> HELM_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("vs_sails", "helm_block_entity"),
            FabricBlockEntityTypeBuilder.create(HelmBlockEntity::new, HELM_BLOCK).build()
    );
    public static final BlockEntityType<BallastBlockEntity> BALLAST_BLOCK_ENTITY = Registry.register(
            Registries.BLOCK_ENTITY_TYPE,
            new Identifier("vs_sails", "ballast_block_entity"),
            FabricBlockEntityTypeBuilder.create(BallastBlockEntity::new, BALLAST_BLOCK).build()
    );

    //entities would go here



}
