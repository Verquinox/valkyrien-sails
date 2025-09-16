package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.blocks.entity.HelmBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelmBlock extends BaseHelmBlock {

    public static final Logger LOGGER = LoggerFactory.getLogger("helm_block");

    public HelmBlock(Properties settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HelmBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ValkyrienSails.HELM_BLOCK_ENTITY, (world1, pos, state1, blockEntity) -> HelmBlockEntity.tick(world1, pos, state1));
    }
}
