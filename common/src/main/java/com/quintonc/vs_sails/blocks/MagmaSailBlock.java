package com.quintonc.vs_sails.blocks;

import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class MagmaSailBlock extends SailBlock {
    public MagmaSailBlock(Properties settings) {
        super(settings);
    }

    @Override
    public boolean isFlammable(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return false;
    }

    @Override
    public int getFlammability(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
        return 0;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextInt(8) != 0) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.options.graphicsMode().get() == GraphicsStatus.FAST) {
            return;
        }

        //Sketchy "LOD" check - not sure if it's actually better than just running the particles
        //Only renders particles in the first 20% of render distance
        double maxAnimationDistance = minecraft.options.getEffectiveRenderDistance() * 16.0D * 0.20D;
        var cameraEntity = minecraft.getCameraEntity();
        if (cameraEntity != null && cameraEntity.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) > maxAnimationDistance * maxAnimationDistance) {
            return;
        }

        Direction direction = Direction.getRandom(random);
        BlockPos neighborPos = pos.relative(direction);

        if (level.getBlockState(neighborPos).isFaceSturdy(level, neighborPos, direction.getOpposite())) {
            return;
        }

        double x = pos.getX() + 0.15D + random.nextDouble() * 0.7D;
        double y = pos.getY() + 0.15D + random.nextDouble() * 0.7D;
        double z = pos.getZ() + 0.15D + random.nextDouble() * 0.7D;

        switch (direction) {
            case DOWN -> y = pos.getY() - 0.02D;
            case UP -> y = pos.getY() + 1.02D;
            case NORTH -> z = pos.getZ() - 0.02D;
            case SOUTH -> z = pos.getZ() + 1.02D;
            case WEST -> x = pos.getX() - 0.02D;
            case EAST -> x = pos.getX() + 1.02D;
        }

        level.addParticle(ParticleTypes.SMALL_FLAME, x, y, z, 0.0D, 0.01D, 0.0D);
    }
}
