package com.quintonc.vs_sails.recipes;

import com.quintonc.vs_sails.blocks.WindFlagBlock;
import com.quintonc.vs_sails.registration.SailsBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class WindFlagRecipeUtil {
    public static final int PATTERN_NONE = 0;
    private static final int DEFAULT_OVERLAY_COLOR = DyeColor.WHITE.getId();
    private static final String BLOCK_STATE_TAG_KEY = "BlockStateTag";
    private static final String PATTERN_KEY = WindFlagBlock.PATTERN.getName();
    private static final String OVERLAY_COLOR_KEY = WindFlagBlock.OVERLAY_COLOR.getName();

    private WindFlagRecipeUtil() {
    }

    public static boolean isWindFlagItem(ItemStack stack) {
        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WindFlagBlock;
    }

    public static int getPatternFromBannerPatternItem(ItemStack itemStack) {
        String itemPath = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        return switch (itemPath) {
            case "skull_banner_pattern" -> 1;
            case "creeper_banner_pattern" -> 2;
            case "flower_banner_pattern" -> 3;
            case "mojang_banner_pattern" -> 4;
            case "globe_banner_pattern" -> 5;
            case "piglin_banner_pattern" -> 6;
            default -> PATTERN_NONE;
        };
    }

    public static int readPattern(ItemStack stack) {
        CompoundTag stackTag = stack.getTag();
        if (stackTag == null || !stackTag.contains(BLOCK_STATE_TAG_KEY, Tag.TAG_COMPOUND)) {
            return PATTERN_NONE;
        }
        CompoundTag blockStateTag = stackTag.getCompound(BLOCK_STATE_TAG_KEY);
        if (blockStateTag.contains(PATTERN_KEY, Tag.TAG_ANY_NUMERIC)) {
            return blockStateTag.getInt(PATTERN_KEY);
        }
        if (!blockStateTag.contains(PATTERN_KEY, Tag.TAG_STRING)) {
            return PATTERN_NONE;
        }
        try {
            return Integer.parseInt(blockStateTag.getString(PATTERN_KEY));
        } catch (NumberFormatException ignored) {
            return PATTERN_NONE;
        }
    }

    public static int readOverlayColor(ItemStack stack) {
        CompoundTag stackTag = stack.getTag();
        if (stackTag == null || !stackTag.contains(BLOCK_STATE_TAG_KEY, Tag.TAG_COMPOUND)) {
            return DEFAULT_OVERLAY_COLOR;
        }
        CompoundTag blockStateTag = stackTag.getCompound(BLOCK_STATE_TAG_KEY);
        if (blockStateTag.contains(OVERLAY_COLOR_KEY, Tag.TAG_ANY_NUMERIC)) {
            return blockStateTag.getInt(OVERLAY_COLOR_KEY);
        }
        if (!blockStateTag.contains(OVERLAY_COLOR_KEY, Tag.TAG_STRING)) {
            return DEFAULT_OVERLAY_COLOR;
        }
        try {
            return Integer.parseInt(blockStateTag.getString(OVERLAY_COLOR_KEY));
        } catch (NumberFormatException ignored) {
            return DEFAULT_OVERLAY_COLOR;
        }
    }

    public static void writePattern(ItemStack stack, int pattern) {
        CompoundTag blockStateTag = stack.getOrCreateTagElement(BLOCK_STATE_TAG_KEY);
        blockStateTag.putString(PATTERN_KEY, Integer.toString(pattern));
    }

    public static void writeOverlayColor(ItemStack stack, int overlayColor) {
        CompoundTag blockStateTag = stack.getOrCreateTagElement(BLOCK_STATE_TAG_KEY);
        blockStateTag.putString(OVERLAY_COLOR_KEY, Integer.toString(overlayColor));
    }

    public static Item getFlagItemForDye(DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE -> SailsBlocks.WHITE_WIND_FLAG.get().asItem();
            case ORANGE -> SailsBlocks.ORANGE_WIND_FLAG.get().asItem();
            case MAGENTA -> SailsBlocks.MAGENTA_WIND_FLAG.get().asItem();
            case LIGHT_BLUE -> SailsBlocks.LIGHT_BLUE_WIND_FLAG.get().asItem();
            case YELLOW -> SailsBlocks.YELLOW_WIND_FLAG.get().asItem();
            case LIME -> SailsBlocks.LIME_WIND_FLAG.get().asItem();
            case PINK -> SailsBlocks.PINK_WIND_FLAG.get().asItem();
            case GRAY -> SailsBlocks.GRAY_WIND_FLAG.get().asItem();
            case LIGHT_GRAY -> SailsBlocks.LIGHT_GRAY_WIND_FLAG.get().asItem();
            case CYAN -> SailsBlocks.CYAN_WIND_FLAG.get().asItem();
            case PURPLE -> SailsBlocks.PURPLE_WIND_FLAG.get().asItem();
            case BLUE -> SailsBlocks.BLUE_WIND_FLAG.get().asItem();
            case BROWN -> SailsBlocks.BROWN_WIND_FLAG.get().asItem();
            case GREEN -> SailsBlocks.GREEN_WIND_FLAG.get().asItem();
            case RED -> SailsBlocks.RED_WIND_FLAG.get().asItem();
            case BLACK -> SailsBlocks.BLACK_WIND_FLAG.get().asItem();
        };
    }

}
