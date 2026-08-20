package com.quintonc.vs_sails.items;

import com.quintonc.vs_sails.blocks.WindFlagBlock;
import com.quintonc.vs_sails.registration.SailsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WindFlagItem extends BlockItem {
    private static final int PATTERN_NONE = 0;
    private static final int PATTERN_SKULL = 1;
    private static final String JOLLY_ROGER_NAME_KEY = "item.vs_sails.wind_flag.jolly_roger";
    private static final String RAINBOW_NAME_KEY = "item.vs_sails.wind_flag.rainbow";

    public WindFlagItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        if (isJollyRoger(stack)) {
            return Component.translatable(JOLLY_ROGER_NAME_KEY);
        }
        return super.getName(stack);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(pos);

        if (!isWaterCauldron(clickedState) || isAlreadyDefaultFlag(context.getItemInHand())) {
            return super.useOn(context);
        }

        if (!level.isClientSide) {
            ItemStack cleanedFlag = new ItemStack(SailsBlocks.WIND_FLAG.get().asItem());
            replaceHeldItemWithCleanedFlag(context.getPlayer(), context.getHand(), context.getItemInHand(), cleanedFlag);
            LayeredCauldronBlock.lowerFillLevel(clickedState, level, pos);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void appendHoverText(
            ItemStack stack,
            @Nullable Level level,
            List<Component> tooltip,
            TooltipFlag tooltipFlag
    ) {
        super.appendHoverText(stack, level, tooltip, tooltipFlag);

        int pattern = PATTERN_NONE;
        CompoundTag blockStateTag = stack.getTagElement("BlockStateTag");
        if (blockStateTag != null && blockStateTag.contains(WindFlagBlock.PATTERN.getName())) {
            try {
                pattern = Integer.parseInt(blockStateTag.getString(WindFlagBlock.PATTERN.getName()));
            } catch (NumberFormatException ignored) {
                pattern = PATTERN_NONE;
            }
        }
        if (pattern <= 0) {
            return;
        }

        int overlayColorId = DyeColor.WHITE.getId();
        if (blockStateTag != null && blockStateTag.contains(WindFlagBlock.OVERLAY_COLOR.getName())) {
            try {
                overlayColorId = Integer.parseInt(blockStateTag.getString(WindFlagBlock.OVERLAY_COLOR.getName()));
            } catch (NumberFormatException ignored) {
                overlayColorId = DyeColor.WHITE.getId();
            }
        }
        Component colorName;
        if (overlayColorId == WindFlagBlock.OVERLAY_COLOR_RAINBOW) {
            colorName = Component.translatable(RAINBOW_NAME_KEY);
        } else {
            DyeColor overlayColor = DyeColor.byId(overlayColorId);
            if (overlayColor == null) {
                overlayColor = DyeColor.WHITE;
            }
            colorName = Component.translatable("color.minecraft." + overlayColor.getName());
        }

        String patternKey = getPatternLangKey(pattern);
        if (patternKey == null) {
            return;
        }

        Component patternName = Component.translatable(patternKey);
        tooltip.add(
                Component.translatable("item.vs_sails.wind_flag.patterned_name", colorName, patternName)
                        .withStyle(ChatFormatting.GRAY)
        );
    }

    private static String getPatternLangKey(int pattern) {
        return switch (pattern) {
            case 1 -> "item.minecraft.skull_banner_pattern.desc";
            case 2 -> "item.minecraft.creeper_banner_pattern.desc";
            case 3 -> "item.minecraft.flower_banner_pattern.desc";
            case 4 -> "item.minecraft.mojang_banner_pattern.desc";
            case 5 -> "item.minecraft.globe_banner_pattern.desc";
            case 6 -> "item.minecraft.piglin_banner_pattern.desc";
            default -> null;
        };
    }

    private static boolean isWaterCauldron(BlockState state) {
        return state.is(Blocks.WATER_CAULDRON)
                && state.getValue(LayeredCauldronBlock.LEVEL) > 0;
    }

    private static boolean isAlreadyDefaultFlag(ItemStack stack) {
        if (stack.getItem() != SailsBlocks.WIND_FLAG.get().asItem()) {
            return false;
        }
        int pattern = PATTERN_NONE;
        CompoundTag blockStateTag = stack.getTagElement("BlockStateTag");
        if (blockStateTag != null && blockStateTag.contains(WindFlagBlock.PATTERN.getName())) {
            try {
                pattern = Integer.parseInt(blockStateTag.getString(WindFlagBlock.PATTERN.getName()));
            } catch (NumberFormatException ignored) {
                pattern = PATTERN_NONE;
            }
        }
        return pattern == PATTERN_NONE;
    }

    private static boolean isJollyRoger(ItemStack stack) {
        if (stack.getItem() != SailsBlocks.BLACK_WIND_FLAG.get().asItem()) {
            return false;
        }
        int pattern = PATTERN_NONE;
        int overlayColor = DyeColor.WHITE.getId();
        CompoundTag blockStateTag = stack.getTagElement("BlockStateTag");
        if (blockStateTag != null && blockStateTag.contains(WindFlagBlock.PATTERN.getName())) {
            try {
                pattern = Integer.parseInt(blockStateTag.getString(WindFlagBlock.PATTERN.getName()));
            } catch (NumberFormatException ignored) {
                pattern = PATTERN_NONE;
            }
        }
        if (pattern != PATTERN_SKULL) {
            return false;
        }

        if (blockStateTag != null && blockStateTag.contains(WindFlagBlock.OVERLAY_COLOR.getName())) {
            try {
                overlayColor = Integer.parseInt(blockStateTag.getString(WindFlagBlock.OVERLAY_COLOR.getName()));
            } catch (NumberFormatException ignored) {
                overlayColor = DyeColor.WHITE.getId();
            }
        }
        return overlayColor == DyeColor.WHITE.getId();
    }

    private static void replaceHeldItemWithCleanedFlag(
            @Nullable Player player,
            InteractionHand hand,
            ItemStack heldStack,
            ItemStack cleanedFlagStack
    ) {
        if (player == null) {
            heldStack.shrink(1);
            return;
        }

        if (heldStack.getCount() == 1) {
            player.setItemInHand(hand, cleanedFlagStack);
            return;
        }

        heldStack.shrink(1);
        if (!player.getInventory().add(cleanedFlagStack)) {
            player.drop(cleanedFlagStack, false);
        }
    }
}
