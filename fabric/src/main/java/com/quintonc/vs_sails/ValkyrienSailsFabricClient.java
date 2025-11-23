package com.quintonc.vs_sails;


import com.quintonc.vs_sails.client.particles.WindParticle;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class ValkyrienSailsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ValkyrienSailsClient.clientInit();
        ParticleFactoryRegistry.getInstance().register(ValkyrienSails.WIND_PARTICLE, WindParticle.Factory::new);
    }
}
