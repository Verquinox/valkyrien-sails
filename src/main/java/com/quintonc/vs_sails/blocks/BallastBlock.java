package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class BallastBlock extends CountableBlock {
    public BallastBlock(Settings settings) {
        super(settings);
    }

    @Override
    void addToShip(SailsShipControl controller) {
        controller.numBallast++;
    }

    @Override
    void removeFromShip(SailsShipControl controller) {
        controller.numBallast--;
    }

    @Override
    void sendMessage(PlayerEntity player, SailsShipControl controller) {
        player.sendMessage(Text.of("Ballast: "+ (controller.numBallast)));
    }


}
