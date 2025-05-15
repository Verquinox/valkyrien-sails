package com.quintonc.vs_sails;

import com.quintonc.vs_sails.blocks.entity.renderer.HelmBlockEntityRenderer;
import com.quintonc.vs_sails.client.particles.WindParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;

import static com.quintonc.vs_sails.ValkyrienSailsJava.MOD_ID;

public class ValkyrienSailsClient implements ClientModInitializer {

    private static KeyBinding testKeyBinding;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererFactories.register(ValkyrienSailsJava.HELM_BLOCK_ENTITY, HelmBlockEntityRenderer::new);

        System.out.println("Client init");
        com.quintonc.vs_sails.client.ClientWindManager.InitializeWind();

//        ClientSpriteRegistryCallback.event(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE).register(((altasTexture, registry) -> {
//            registry.register(new Identifier(MOD_ID, PARTICLE));
//        }));

        ParticleFactoryRegistry.getInstance().register(ValkyrienSailsJava.WIND_PARTICLE, WindParticle.Factory::new);



        testKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.examplemod.spook", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_J, // The keycode of the key
                "category."+ MOD_ID +".test" // The translation key of the keybinding's category.
        ));



        ClientTickEvents.END_CLIENT_TICK.register(ModSounds::windSoundHandler);


        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (testKeyBinding.wasPressed()) {

                assert client.player != null;
                //BlockPos targetedPos = client.player.getBlockPos();
                //fixme add ScreenshakeHandler.addScreenshake(new PositionedScreenshakeInstance(10, BlockPosHelper.fromBlockPos(targetedPos), 10f, 800f, Easing.EXPO_OUT).setIntensity(2f, 0));
                client.player.sendMessage(Text.literal("Key 1 was pressed!"), false);
            }
        });
    }
}
