package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import com.quintonc.vs_sails.blocks.entity.RedstoneHelmBlockEntity;
import com.quintonc.vs_sails.client.particles.WindParticle;
import com.quintonc.vs_sails.registration.SailsBlocks;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
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
        ValkyrienSails.WIND_PARTICLE = WIND_PARTICLE.get();
    }

    private void registerBlockEntities(IEventBus eventBus) {

        HELM_BLOCK_ENTITY = BLOCK_ENTITIES.register("helm_block_entity", () -> BlockEntityType.Builder.of(HelmBlockEntity::new, SailsBlocks.HELM_BLOCK.get()).build(null));
        REDSTONE_HELM_BLOCK_ENTITY = BLOCK_ENTITIES.register("redstone_helm_block_entity", () -> BlockEntityType.Builder.of(RedstoneHelmBlockEntity::new, SailsBlocks.REDSTONE_HELM_BLOCK.get()).build(null));

        BLOCK_ENTITIES.register(eventBus);

    }

    private static void registerParticles(IEventBus eventBus) {

        PARTICLE_TYPES.register(eventBus);

    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = ValkyrienSails.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ValkyrienSailsClient.clientInit();
        }

        @SubscribeEvent
        public static void registerParticleProvider(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(WIND_PARTICLE.get(), WindParticle.Factory::new);
        }
    }

}
