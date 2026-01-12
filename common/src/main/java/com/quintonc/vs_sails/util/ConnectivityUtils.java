package com.quintonc.vs_sails.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

import java.util.*;

public class ConnectivityUtils {
    private static final int MAX_RECURSION = 100_000;

    private static final List<Block> BLACKLIST = List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR, Blocks.WATER, Blocks.KELP, Blocks.KELP_PLANT, Blocks.SEAGRASS, Blocks.TALL_SEAGRASS, Blocks.GRASS, Blocks.TALL_GRASS, Blocks.DEAD_BUSH);

    public static @Nullable Set<BlockPos> tryFillByConnectivity(BlockGetter level, BlockPos start) {
        Set<BlockPos> result = new HashSet<>();
        Set<BlockPos> visited = new HashSet<>();
        ArrayDeque<BlockPos> queue = new ArrayDeque<>();

        if (!isBlockStateValid(level.getBlockState(start))) {
            return null;
        }

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.removeFirst();
            result.add(current);

            if (result.size() > MAX_RECURSION) {
                return null;
            }

            for (BlockPos offset : NEIGHBOR_OFFSETS) {
                BlockPos neighbor = current.offset(offset);
                if (visited.contains(neighbor)) {
                    continue;
                } else {
                    visited.add(neighbor);
                }

                BlockState blockState = level.getBlockState(neighbor);

                if (blockState.is(Blocks.BEDROCK)) {
                    return null;
                } else if (isBlockStateValid(blockState)) {
                    queue.add(neighbor);
                }
            }
        }

        return result;
    }

    private static boolean isBlockStateValid(BlockState state) {
        return !(BLACKLIST.contains(state.getBlock()) || VSGameUtilsKt.inAssemblyBlacklist(state));
    }

    private static final List<BlockPos> NEIGHBOR_OFFSETS = Arrays.asList(
            // Face directions
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),

            // Edge directions (no corners)
            new BlockPos(1, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, 1, -1),
            new BlockPos(0, -1, -1),
            new BlockPos(1, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(1, 0, -1),
            new BlockPos(-1, 0, -1)
    );
}
