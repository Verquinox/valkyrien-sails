//package com.quintonc.vs_sails.blocks.entity;
//
//import com.ibm.icu.impl.Pair;
//import net.minecraft.core.BlockPos;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.nbt.ListTag;
//import net.minecraft.network.protocol.Packet;
//import net.minecraft.network.protocol.game.ClientGamePacketListener;
//import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.block.entity.BlockEntityType;
//import net.minecraft.world.level.block.state.BlockState;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.ArrayList;
//
//public class FurledSailBlockEntity extends BlockEntity {
//    public FurledSailBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
//        super(type, pos, blockState);
//    }
//
//    ListTag sailGroup;
//    //NbtList<Pair<BlockPos, String>> sailGroup;
//
//    @Override
//    protected void saveAdditional(CompoundTag pTag) {
//        pTag.putInt("sail_group", sailGroup.size());
//        super.saveAdditional(pTag);
//    }
//
//    @Override
//    public void load(CompoundTag pTag) {
//        super.load(pTag);
//        sailGroup = pTag.get("sail_group");
//    }
//
//    public ArrayList<Pair<BlockPos, String>> getSailGroup() {
//        return sailGroup;
//    }
//
//    @Override
//    public void setChanged() {
//        assert level != null;
//        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
//        super.setChanged();
//    }
//
//    @Nullable
//    @Override
//    public Packet<ClientGamePacketListener> getUpdatePacket() {
//        return ClientboundBlockEntityDataPacket.create(this);
//    }
//
//    @Override
//    public CompoundTag getUpdateTag() {
//        return saveWithoutMetadata();
//    }
//}
