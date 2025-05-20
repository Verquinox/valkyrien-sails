package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ship.SailsShipControl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class MagicBallastBlock extends CountableBlock {
    public MagicBallastBlock(Settings settings) {
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
    void sendMessage(PlayerEntity player, SailsShipControl controller) {
        player.sendMessage(Text.of("Magic Ballast: "+ (controller.numMagicBallast)));
    }

}
