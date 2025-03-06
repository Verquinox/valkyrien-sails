package com.quintonc.vs_sails;

import kotlin.Triple;
import net.minecraft.block.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.core.apigame.world.chunks.BlockType;
import org.valkyrienskies.mod.common.BlockStateInfoProvider;
import org.valkyrienskies.physics_api.voxel.Lod1LiquidBlockState;
import org.valkyrienskies.physics_api.voxel.Lod1SolidBlockState;

import java.util.List;

public class SailsWeights implements BlockStateInfoProvider {

    @Override
    public @NotNull List<Triple<Integer, Integer, Integer>> getBlockStateData() {
        return List.of();
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public @Nullable Double getBlockStateMass(@NotNull BlockState blockState) {

        return 0.0;
    }

    @Override
    public @Nullable BlockType getBlockStateType(@NotNull BlockState blockState) {
        return null;
    }

    @Override
    public @NotNull List<Lod1SolidBlockState> getSolidBlockStates() {
        return List.of();
    }

    @Override
    public @NotNull List<Lod1LiquidBlockState> getLiquidBlockStates() {
        return List.of();
    }
}
