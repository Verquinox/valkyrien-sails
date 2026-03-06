package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.WindFlagBlock;
import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import com.quintonc.vs_sails.blocks.entity.renderer.WindFlagBlockEntityRenderer;
import com.quintonc.vs_sails.client.ClientWindManager;
import com.quintonc.vs_sails.networking.PacketHandler;
import com.quintonc.vs_sails.registration.SailsBlocks;
import dev.architectury.event.events.client.ClientTickEvent;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ValkyrienSailsClient {
    private static final int BANNER_PATTERN_SKULL = 1;
    private static final int BANNER_PATTERN_CREEPER = 2;
    private static final int BANNER_PATTERN_FLOWER = 3;
    private static final int BANNER_PATTERN_MOJANG = 4;
    private static final int BANNER_PATTERN_GLOBE = 5;
    private static final int BANNER_PATTERN_PIGLIN = 6;

    //private static KeyBinding testKeyBinding;

    public static void clientInit() {
        BlockEntityRenderers.register(ValkyrienSails.HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);
        BlockEntityRenderers.register(ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);
        BlockEntityRenderers.register(ValkyrienSails.WIND_FLAG_BLOCK_ENTITY, WindFlagBlockEntityRenderer::new);
        registerWindFlagItemProperties();

        //System.out.println("Client init");
        com.quintonc.vs_sails.client.ClientWindManager.InitializeWind();

//        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((altasTexture, registry) -> {
//            registry.register(new Identifier(MOD_ID, PARTICLE));
//        }));

        PacketHandler.register();

        if (!ValkyrienSails.weather2) {
            //ClientTickEvents.END_CLIENT_TICK.register(ModSounds::windSoundHandler);
            ClientTickEvent.CLIENT_POST.register(ModSounds::windSoundHandler);
        }

        ClientTickEvent.CLIENT_POST.register(ClientWindManager::handleClientTick);

    }

    private static void registerWindFlagItemProperties() {
        ResourceLocation skullPatternId = ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "banner_pattern_skull");
        ResourceLocation creeperPatternId = ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "banner_pattern_creeper");
        ResourceLocation flowerPatternId = ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "banner_pattern_flower");
        ResourceLocation mojangPatternId = ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "banner_pattern_mojang");
        ResourceLocation globePatternId = ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "banner_pattern_globe");
        ResourceLocation piglinPatternId = ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "banner_pattern_piglin");

        Item[] windFlagItems = new Item[]{
                SailsBlocks.WIND_FLAG.get().asItem(),
                SailsBlocks.BLACK_WIND_FLAG.get().asItem(),
                SailsBlocks.BROWN_WIND_FLAG.get().asItem(),
                SailsBlocks.CYAN_WIND_FLAG.get().asItem(),
                SailsBlocks.GRAY_WIND_FLAG.get().asItem(),
                SailsBlocks.GREEN_WIND_FLAG.get().asItem(),
                SailsBlocks.LIGHT_BLUE_WIND_FLAG.get().asItem(),
                SailsBlocks.BLUE_WIND_FLAG.get().asItem(),
                SailsBlocks.LIGHT_GRAY_WIND_FLAG.get().asItem(),
                SailsBlocks.LIME_WIND_FLAG.get().asItem(),
                SailsBlocks.MAGENTA_WIND_FLAG.get().asItem(),
                SailsBlocks.ORANGE_WIND_FLAG.get().asItem(),
                SailsBlocks.PINK_WIND_FLAG.get().asItem(),
                SailsBlocks.PURPLE_WIND_FLAG.get().asItem(),
                SailsBlocks.RED_WIND_FLAG.get().asItem(),
                SailsBlocks.WHITE_WIND_FLAG.get().asItem(),
                SailsBlocks.YELLOW_WIND_FLAG.get().asItem()
        };
        for (Item windFlagItem : windFlagItems) {
            registerPatternProperty(windFlagItem, skullPatternId, BANNER_PATTERN_SKULL);
            registerPatternProperty(windFlagItem, creeperPatternId, BANNER_PATTERN_CREEPER);
            registerPatternProperty(windFlagItem, flowerPatternId, BANNER_PATTERN_FLOWER);
            registerPatternProperty(windFlagItem, mojangPatternId, BANNER_PATTERN_MOJANG);
            registerPatternProperty(windFlagItem, globePatternId, BANNER_PATTERN_GLOBE);
            registerPatternProperty(windFlagItem, piglinPatternId, BANNER_PATTERN_PIGLIN);
        }
    }

    private static void registerPatternProperty(Item windFlagItem, ResourceLocation propertyId, int patternId) {
        ItemProperties.register(
                windFlagItem,
                propertyId,
                (stack, level, entity, seed) -> WindFlagBlock.getItemPatternMatchProperty(stack, patternId)
        );
    }
}
