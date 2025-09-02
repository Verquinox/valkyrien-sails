package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

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

    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return true;
    }

    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 5;
    }

    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 20;
    }

}
