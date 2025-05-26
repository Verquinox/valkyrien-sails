package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

public class BallastBlock extends CountableBlock {
    public BallastBlock(Properties settings) {
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
    void sendMessage(Player player, SailsShipControl controller) {
        player.sendSystemMessage(Component.literal("Ballast: "+ (controller.numBallast)));
    }


}
