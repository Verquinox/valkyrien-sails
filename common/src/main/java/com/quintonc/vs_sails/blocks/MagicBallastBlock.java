package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;


public class MagicBallastBlock extends CountableBlock {
    public MagicBallastBlock(Properties settings) {
        super(settings);
    }

    @Override
    void addToShip(SailsShipControl controller) {
        controller.numMagicBallast++;
    }

    @Override
    void removeFromShip(SailsShipControl controller) {
        controller.numMagicBallast--;
    }

    @Override
    void sendMessage(Player player, SailsShipControl controller) {
        player.sendSystemMessage(Component.literal("Magic Ballast: "+ (controller.numMagicBallast)));
    }

}
