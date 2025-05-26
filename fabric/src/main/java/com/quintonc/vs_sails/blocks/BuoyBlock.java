package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;

public class BuoyBlock extends CountableBlock {
    public BuoyBlock(Properties settings) {
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
    void sendMessage(Player player, SailsShipControl controller) {
        player.sendSystemMessage(Component.literal("Buoys: "+ (controller.numBuoys)));
    }

}
