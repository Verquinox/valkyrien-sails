package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.RedstoneHelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.WindFlagBlockEntity;
import com.quintonc.vs_sails.blocks.WindFlagBlock;
import com.quintonc.vs_sails.client.particles.WindParticle;
import com.quintonc.vs_sails.registration.SailsBlocks;
import com.quintonc.vs_sails.wind.WindDataReloadListener;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ValkyrienSails.MOD_ID)
public class ValkyrienSailsForge {
    public static final Logger LOGGER = LoggerFactory.getLogger("vs_sails_forge");

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ValkyrienSails.MOD_ID);

    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ValkyrienSails.MOD_ID);

    public static RegistryObject<BlockEntityType<HelmBlockEntity>> HELM_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<RedstoneHelmBlockEntity>> REDSTONE_HELM_BLOCK_ENTITY;
    public static RegistryObject<BlockEntityType<WindFlagBlockEntity>> WIND_FLAG_BLOCK_ENTITY;
    public static RegistryObject<SimpleParticleType> WIND_PARTICLE = PARTICLE_TYPES.register("wind_particle", () -> new SimpleParticleType(true));

    public ValkyrienSailsForge() {
        //LOGGER.info("Begin forge constructor");
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //LOGGER.info("add listener");
        modEventBus.addListener(this::commonSetup);

        //LOGGER.info("register event bus");
        MinecraftForge.EVENT_BUS.register(this);
        EventBuses.registerModEventBus(ValkyrienSails.MOD_ID, modEventBus);

        ValkyrienSails.init();
        //registerItems();
        registerBlockEntities(modEventBus);
        registerParticles(modEventBus);
        ModSounds.registerSounds();
    }

    private void commonSetup(final FMLCommonSetupEvent event) {

        ValkyrienSails.HELM_BLOCK_ENTITY = HELM_BLOCK_ENTITY.get();
        ValkyrienSails.REDSTONE_HELM_BLOCK_ENTITY = REDSTONE_HELM_BLOCK_ENTITY.get();
        ValkyrienSails.WIND_FLAG_BLOCK_ENTITY = WIND_FLAG_BLOCK_ENTITY.get();
        ValkyrienSails.WIND_PARTICLE = WIND_PARTICLE.get();
    }

    private void registerBlockEntities(IEventBus eventBus) {

        HELM_BLOCK_ENTITY = BLOCK_ENTITIES.register("helm_block_entity", () -> BlockEntityType.Builder.of(
                HelmBlockEntity::new,
                SailsBlocks.HELM_BLOCK.get(),
                SailsBlocks.OAK_HELM.get(),
                SailsBlocks.SPRUCE_HELM.get(),
                SailsBlocks.BIRCH_HELM.get(),
                SailsBlocks.JUNGLE_HELM.get(),
                SailsBlocks.DARK_OAK_HELM.get(),
                SailsBlocks.ACACIA_HELM.get(),
                SailsBlocks.MANGROVE_HELM.get(),
                SailsBlocks.CHERRY_HELM.get(),
                SailsBlocks.CRIMSON_HELM.get(),
                SailsBlocks.WARPED_HELM.get(),
                SailsBlocks.BAMBOO_HELM.get()).build(null));
        REDSTONE_HELM_BLOCK_ENTITY = BLOCK_ENTITIES.register("redstone_helm_block_entity", () -> BlockEntityType.Builder.of(RedstoneHelmBlockEntity::new, SailsBlocks.REDSTONE_HELM_BLOCK.get()).build(null));
        WIND_FLAG_BLOCK_ENTITY = BLOCK_ENTITIES.register("wind_flag_block_entity", () -> BlockEntityType.Builder.of(
                WindFlagBlockEntity::new,
                SailsBlocks.WIND_FLAG.get(),
                SailsBlocks.BLACK_WIND_FLAG.get(),
                SailsBlocks.BROWN_WIND_FLAG.get(),
                SailsBlocks.CYAN_WIND_FLAG.get(),
                SailsBlocks.GRAY_WIND_FLAG.get(),
                SailsBlocks.GREEN_WIND_FLAG.get(),
                SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(),
                SailsBlocks.BLUE_WIND_FLAG.get(),
                SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(),
                SailsBlocks.LIME_WIND_FLAG.get(),
                SailsBlocks.MAGENTA_WIND_FLAG.get(),
                SailsBlocks.ORANGE_WIND_FLAG.get(),
                SailsBlocks.PINK_WIND_FLAG.get(),
                SailsBlocks.PURPLE_WIND_FLAG.get(),
                SailsBlocks.RED_WIND_FLAG.get(),
                SailsBlocks.WHITE_WIND_FLAG.get(),
                SailsBlocks.YELLOW_WIND_FLAG.get()
        ).build(null));

        BLOCK_ENTITIES.register(eventBus);

    }

    private static void registerParticles(IEventBus eventBus) {

        PARTICLE_TYPES.register(eventBus);

    }

    @SubscribeEvent
    public void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() == null) {
            WindDataReloadListener.loadFromServer(event.getPlayerList().getServer());
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = ValkyrienSails.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ValkyrienSailsClient.clientInit();
            event.enqueueWork(() -> {
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.BLACK_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.BROWN_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.CYAN_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.GRAY_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.GREEN_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.BLUE_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.LIME_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.MAGENTA_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.ORANGE_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.PINK_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.PURPLE_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.RED_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.WHITE_WIND_FLAG.get(), RenderType.translucent());
                ItemBlockRenderTypes.setRenderLayer(SailsBlocks.YELLOW_WIND_FLAG.get(), RenderType.translucent());
            });
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(WIND_PARTICLE.get(), WindParticle.Factory::new);
        }

        @SubscribeEvent
        public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
            event.register(
                    (state, level, pos, tintIndex) -> WindFlagBlock.getOverlayTintColor(state, level, pos, tintIndex),
                    SailsBlocks.WIND_FLAG.get(),
                    SailsBlocks.BLACK_WIND_FLAG.get(),
                    SailsBlocks.BROWN_WIND_FLAG.get(),
                    SailsBlocks.CYAN_WIND_FLAG.get(),
                    SailsBlocks.GRAY_WIND_FLAG.get(),
                    SailsBlocks.GREEN_WIND_FLAG.get(),
                    SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(),
                    SailsBlocks.BLUE_WIND_FLAG.get(),
                    SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(),
                    SailsBlocks.LIME_WIND_FLAG.get(),
                    SailsBlocks.MAGENTA_WIND_FLAG.get(),
                    SailsBlocks.ORANGE_WIND_FLAG.get(),
                    SailsBlocks.PINK_WIND_FLAG.get(),
                    SailsBlocks.PURPLE_WIND_FLAG.get(),
                    SailsBlocks.RED_WIND_FLAG.get(),
                    SailsBlocks.WHITE_WIND_FLAG.get(),
                    SailsBlocks.YELLOW_WIND_FLAG.get()
            );
        }

        @SubscribeEvent
        public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
            event.register(
                    WindFlagBlock::getOverlayTintColor,
                    SailsBlocks.WIND_FLAG.get(),
                    SailsBlocks.BLACK_WIND_FLAG.get(),
                    SailsBlocks.BROWN_WIND_FLAG.get(),
                    SailsBlocks.CYAN_WIND_FLAG.get(),
                    SailsBlocks.GRAY_WIND_FLAG.get(),
                    SailsBlocks.GREEN_WIND_FLAG.get(),
                    SailsBlocks.LIGHT_BLUE_WIND_FLAG.get(),
                    SailsBlocks.BLUE_WIND_FLAG.get(),
                    SailsBlocks.LIGHT_GRAY_WIND_FLAG.get(),
                    SailsBlocks.LIME_WIND_FLAG.get(),
                    SailsBlocks.MAGENTA_WIND_FLAG.get(),
                    SailsBlocks.ORANGE_WIND_FLAG.get(),
                    SailsBlocks.PINK_WIND_FLAG.get(),
                    SailsBlocks.PURPLE_WIND_FLAG.get(),
                    SailsBlocks.RED_WIND_FLAG.get(),
                    SailsBlocks.WHITE_WIND_FLAG.get(),
                    SailsBlocks.YELLOW_WIND_FLAG.get()
            );
        }
    }

}
