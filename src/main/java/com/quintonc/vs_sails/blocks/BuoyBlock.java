package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class BuoyBlock extends CountableBlock {
    public BuoyBlock(Settings settings) {
        super(settings);
    }

    @Override
    void addToShip(SailsShipControl controller) {
        controller.numBuoys++;
    }

    @Override
    void removeFromShip(SailsShipControl controller) {
        controller.numBuoys--;
    }

    @Override
    void sendMessage(PlayerEntity player, SailsShipControl controller) {
        player.sendMessage(Text.of("Buoys: "+ (controller.numBuoys)));
    }

}
