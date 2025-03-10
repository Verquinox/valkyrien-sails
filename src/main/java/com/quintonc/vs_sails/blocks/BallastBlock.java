package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.blocks.entity.BallastBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class BallastBlock extends BlockWithEntity {
    public BallastBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BallastBlockEntity(pos, state);
    }
}
