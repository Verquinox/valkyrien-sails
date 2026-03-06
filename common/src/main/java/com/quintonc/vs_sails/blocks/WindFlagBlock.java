package com.quintonc.vs_sails.blocks;

import com.quintonc.vs_sails.blocks.entity.WindFlagBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WindFlagBlock extends BaseEntityBlock {
    //Blockstate properties used in rendering
    public static final BooleanProperty FLAG_GROUP = BooleanProperty.create("flag_group");
    public static final BooleanProperty OVERLAY_ONLY = BooleanProperty.create("overlay_only");
    public static final BooleanProperty EMISSIVE = BooleanProperty.create("emissive");
    public static final BooleanProperty FURLED = BooleanProperty.create("furled");
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    //Banner pattern currently displayed
    public static final IntegerProperty PATTERN = IntegerProperty.create("pattern", 0, 6);
    //Color of the banner pattern
    //16 is special state, "jeb_" like rainbow rendering
    public static final IntegerProperty OVERLAY_COLOR = IntegerProperty.create("overlay_color", 0, 16);

    private static final int PATTERN_NONE = 0;
    private static final int PATTERN_SKULL = 1;
    private static final int PATTERN_CREEPER = 2;
    private static final int PATTERN_FLOWER = 3;
    private static final int PATTERN_MOJANG = 4;
    private static final int PATTERN_GLOBE = 5;
    private static final int PATTERN_PIGLIN = 6;

    private static final int DEFAULT_OVERLAY_COLOR = DyeColor.WHITE.getId();

    //Rainbow color values
    public static final int OVERLAY_COLOR_RAINBOW = 16;
    private static final float RAINBOW_TICKS_PER_COLOR = 25.0f;
    private static final long MILLIS_PER_TICK = 50L;
    private static final int[] BANNER_DYE_COLOR_TABLE = createBannerDyeColorTable();

    private static final VoxelShape LOWER_SHAPE = box(7.0, 0.0, 7.0, 9.0, 16.0, 9.0);
    private static final VoxelShape UPPER_TOPPER_SHAPE = box(6.7, 13.5, 6.75, 9.2, 16.0, 9.25);
    private static final VoxelShape UPPER_SHAPE = Shapes.or(
            box(7.0, 0.0, 7.0, 9.0, 15.0, 9.0),
            UPPER_TOPPER_SHAPE
    );

    public WindFlagBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(FLAG_GROUP, false)
                .setValue(OVERLAY_ONLY, false)
                .setValue(EMISSIVE, false)
                .setValue(FURLED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(PATTERN, PATTERN_NONE)
                .setValue(OVERLAY_COLOR, DEFAULT_OVERLAY_COLOR));
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return RenderShape.INVISIBLE;
        }
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull SoundType getSoundType(BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return SoundType.EMPTY;
        }
        return super.getSoundType(state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() >= level.getMaxBuildHeight() - 1) {
            return null;
        }
        if (!level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        return this.defaultBlockState()
                .setValue(FLAG_GROUP, false)
                .setValue(OVERLAY_ONLY, false)
                .setValue(EMISSIVE, false)
                .setValue(FURLED, false)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(PATTERN, PATTERN_NONE)
                .setValue(OVERLAY_COLOR, DEFAULT_OVERLAY_COLOR);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        level.setBlock(
                pos.above(),
                state.setValue(HALF, DoubleBlockHalf.UPPER)
                        .setValue(FLAG_GROUP, false)
                        .setValue(OVERLAY_ONLY, false),
                3
        );
    }

    @Override
    public @NotNull InteractionResult use(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        ItemStack heldItem = player.getItemInHand(hand);
        int pattern = getPatternFromItem(heldItem);
        BlockPos lowerPos = state.getValue(HALF) == DoubleBlockHalf.LOWER ? pos : pos.below();
        BlockState lowerState = level.getBlockState(lowerPos);
        if (!isWindFlagHalf(lowerState, DoubleBlockHalf.LOWER)) {
            return InteractionResult.PASS;
        }

        //Furling/Unfurling
        if (hand == InteractionHand.MAIN_HAND && heldItem.isEmpty()) {
            boolean furled = !lowerState.getValue(FURLED);
            if (!level.isClientSide) {
                BlockPos upperPos = lowerPos.above();
                BlockState upperState = level.getBlockState(upperPos);

                level.setBlock(lowerPos, lowerState.setValue(FURLED, furled), 3);
                if (isWindFlagHalf(upperState, DoubleBlockHalf.UPPER)) {
                    level.setBlock(upperPos, upperState.setValue(FURLED, furled), 3);
                }
                level.playSound(
                        null,
                        lowerPos,
                        furled ? SoundEvents.BUNDLE_INSERT : SoundEvents.BUNDLE_DROP_CONTENTS,
                        SoundSource.BLOCKS,
                        1.0f,
                        1.0f
                );
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //Jeb_ nametag rainbow mode
        if (isRainbowNameTag(heldItem)) {
            if (lowerState.getValue(OVERLAY_COLOR) == OVERLAY_COLOR_RAINBOW) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                BlockPos upperPos = lowerPos.above();
                BlockState upperState = level.getBlockState(upperPos);

                level.setBlock(lowerPos, lowerState.setValue(OVERLAY_COLOR, OVERLAY_COLOR_RAINBOW), 3);
                if (isWindFlagHalf(upperState, DoubleBlockHalf.UPPER)) {
                    level.setBlock(upperPos, upperState.setValue(OVERLAY_COLOR, OVERLAY_COLOR_RAINBOW), 3);
                }
                level.playSound(null, lowerPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //Emissive rendering with glow ink sac
        if (heldItem.is(Items.GLOW_INK_SAC)) {
            if (lowerState.getValue(PATTERN) == PATTERN_NONE) {
                return InteractionResult.PASS;
            }
            if (lowerState.getValue(EMISSIVE)) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                BlockPos upperPos = lowerPos.above();
                BlockState upperState = level.getBlockState(upperPos);

                level.setBlock(lowerPos, lowerState.setValue(EMISSIVE, true), 3);
                if (isWindFlagHalf(upperState, DoubleBlockHalf.UPPER)) {
                    level.setBlock(upperPos, upperState.setValue(EMISSIVE, true), 3);
                }
                level.playSound(null, lowerPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //Flag blockswap
        if (heldItem.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WindFlagBlock replacementFlagBlock) {
            int desiredPattern = PATTERN_NONE;
            int desiredOverlayColor = DEFAULT_OVERLAY_COLOR;
            boolean desiredEmissive = false;
            boolean desiredFurled = false;

            CompoundTag blockStateTag = heldItem.getTagElement("BlockStateTag");
            if (blockStateTag != null) {
                if (blockStateTag.contains(PATTERN.getName(), Tag.TAG_ANY_NUMERIC)) {
                    desiredPattern = blockStateTag.getInt(PATTERN.getName());
                } else if (blockStateTag.contains(PATTERN.getName(), Tag.TAG_STRING)) {
                    try {
                        desiredPattern = Integer.parseInt(blockStateTag.getString(PATTERN.getName()));
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (blockStateTag.contains(OVERLAY_COLOR.getName(), Tag.TAG_ANY_NUMERIC)) {
                    desiredOverlayColor = blockStateTag.getInt(OVERLAY_COLOR.getName());
                } else if (blockStateTag.contains(OVERLAY_COLOR.getName(), Tag.TAG_STRING)) {
                    try {
                        desiredOverlayColor = Integer.parseInt(blockStateTag.getString(OVERLAY_COLOR.getName()));
                    } catch (NumberFormatException ignored) {
                    }
                }

                if (blockStateTag.contains(EMISSIVE.getName(), Tag.TAG_BYTE)) {
                    desiredEmissive = blockStateTag.getBoolean(EMISSIVE.getName());
                } else if (blockStateTag.contains(EMISSIVE.getName(), Tag.TAG_ANY_NUMERIC)) {
                    desiredEmissive = blockStateTag.getInt(EMISSIVE.getName()) != 0;
                } else if (blockStateTag.contains(EMISSIVE.getName(), Tag.TAG_STRING)) {
                    String value = blockStateTag.getString(EMISSIVE.getName());
                    if ("1".equals(value)) {
                        desiredEmissive = true;
                    } else if ("0".equals(value)) {
                        desiredEmissive = false;
                    } else {
                        desiredEmissive = Boolean.parseBoolean(value);
                    }
                }

                if (blockStateTag.contains(FURLED.getName(), Tag.TAG_BYTE)) {
                    desiredFurled = blockStateTag.getBoolean(FURLED.getName());
                } else if (blockStateTag.contains(FURLED.getName(), Tag.TAG_ANY_NUMERIC)) {
                    desiredFurled = blockStateTag.getInt(FURLED.getName()) != 0;
                } else if (blockStateTag.contains(FURLED.getName(), Tag.TAG_STRING)) {
                    String value = blockStateTag.getString(FURLED.getName());
                    if ("1".equals(value)) {
                        desiredFurled = true;
                    } else if ("0".equals(value)) {
                        desiredFurled = false;
                    } else {
                        desiredFurled = Boolean.parseBoolean(value);
                    }
                }
            }

            desiredPattern = Mth.clamp(desiredPattern, PATTERN_NONE, PATTERN_PIGLIN);
            desiredOverlayColor = Mth.clamp(desiredOverlayColor, 0, OVERLAY_COLOR_RAINBOW);

            boolean sameBlockType = lowerState.getBlock() == replacementFlagBlock;
            boolean sameStateValues =
                    lowerState.getValue(PATTERN) == desiredPattern
                            && lowerState.getValue(OVERLAY_COLOR) == desiredOverlayColor
                            && lowerState.getValue(EMISSIVE) == desiredEmissive
                            && lowerState.getValue(FURLED) == desiredFurled;
            if (sameBlockType && sameStateValues) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                BlockState replacementLowerState = replacementFlagBlock.defaultBlockState()
                        .setValue(FLAG_GROUP, false)
                        .setValue(OVERLAY_ONLY, false)
                        .setValue(EMISSIVE, desiredEmissive)
                        .setValue(FURLED, desiredFurled)
                        .setValue(HALF, DoubleBlockHalf.LOWER)
                        .setValue(PATTERN, desiredPattern)
                        .setValue(OVERLAY_COLOR, desiredOverlayColor);
                BlockState replacementUpperState = replacementFlagBlock.defaultBlockState()
                        .setValue(FLAG_GROUP, false)
                        .setValue(OVERLAY_ONLY, false)
                        .setValue(EMISSIVE, desiredEmissive)
                        .setValue(FURLED, desiredFurled)
                        .setValue(HALF, DoubleBlockHalf.UPPER)
                        .setValue(PATTERN, desiredPattern)
                        .setValue(OVERLAY_COLOR, desiredOverlayColor);

                BlockPos upperPos = lowerPos.above();
                level.setBlock(lowerPos, replacementLowerState, 3);
                level.setBlock(upperPos, replacementUpperState, 3);
                level.playSound(null, lowerPos, SoundEvents.WOOL_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);

                if (!player.getAbilities().instabuild) {
                    heldItem.shrink(1);

                    Item oldFlagItem = lowerState.getBlock().asItem();
                    ItemStack returnedStack = new ItemStack(oldFlagItem);
                    writeStateTagToItem(returnedStack, lowerState);

                    if (heldItem.isEmpty()) {
                        player.setItemInHand(hand, returnedStack);
                    } else if (!player.getInventory().add(returnedStack)) {
                        player.drop(returnedStack, false);
                    }
                }
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //Setting banner patterns
        if (pattern != PATTERN_NONE) {
            if (lowerState.getValue(PATTERN) == pattern) {
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            if (!level.isClientSide) {
                BlockPos upperPos = lowerPos.above();
                BlockState upperState = level.getBlockState(upperPos);

                level.setBlock(lowerPos, lowerState.setValue(PATTERN, pattern), 3);
                if (isWindFlagHalf(upperState, DoubleBlockHalf.UPPER)) {
                    level.setBlock(upperPos, upperState.setValue(PATTERN, pattern), 3);
                }
                level.playSound(null, lowerPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);
            }

            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        //Dyeing banner patterns in world
        DyeColor dyeColor = getDyeColorFromItem(heldItem);
        if (dyeColor == null) {
            return InteractionResult.PASS;
        }
        if (lowerState.getValue(PATTERN) == PATTERN_NONE) {
            return InteractionResult.PASS;
        }

        int overlayColor = dyeColor.getId();
        if (lowerState.getValue(OVERLAY_COLOR) == overlayColor) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.isClientSide) {
            BlockPos upperPos = lowerPos.above();
            BlockState upperState = level.getBlockState(upperPos);

            level.setBlock(lowerPos, lowerState.setValue(OVERLAY_COLOR, overlayColor), 3);
            if (isWindFlagHalf(upperState, DoubleBlockHalf.UPPER)) {
                level.setBlock(upperPos, upperState.setValue(OVERLAY_COLOR, overlayColor), 3);
            }
            level.playSound(null, lowerPos, SoundEvents.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, SoundSource.BLOCKS, 1.0f, 1.0f);

            if (!player.getAbilities().instabuild) {
                heldItem.shrink(1);
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_SHAPE : UPPER_SHAPE;
    }

    @Override
    public VoxelShape getBlockSupportShape(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_TOPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    public @NotNull BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos currentPos,
            BlockPos neighborPos
    ) {
        DoubleBlockHalf half = state.getValue(HALF);
        if (direction.getAxis() == Direction.Axis.Y) {
            if (half == DoubleBlockHalf.LOWER && direction == Direction.UP) {
                if (!isWindFlagHalf(neighborState, DoubleBlockHalf.UPPER)) {
                    return Blocks.AIR.defaultBlockState();
                }
            } else if (half == DoubleBlockHalf.UPPER && direction == Direction.DOWN) {
                if (!isWindFlagHalf(neighborState, DoubleBlockHalf.LOWER)) {
                    return Blocks.AIR.defaultBlockState();
                }
            }
        }
        return super.updateShape(state, direction, neighborState, level, currentPos, neighborPos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return null;
        }
        return new WindFlagBlockEntity(pos, state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            return List.of();
        }
        List<ItemStack> drops = super.getDrops(state, params);
        for (ItemStack drop : drops) {
            if (drop.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == this) {
                writeStateTagToItem(drop, state);
            }
        }
        return drops;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        ItemStack stack = super.getCloneItemStack(level, pos, state);
        writeStateTagToItem(stack, state);
        return stack;
    }

    private static int getPatternFromItem(ItemStack itemStack) {
        String itemPath = BuiltInRegistries.ITEM.getKey(itemStack.getItem()).getPath();
        return switch (itemPath) {
            case "skull_banner_pattern" -> PATTERN_SKULL;
            case "creeper_banner_pattern" -> PATTERN_CREEPER;
            case "flower_banner_pattern" -> PATTERN_FLOWER;
            case "mojang_banner_pattern" -> PATTERN_MOJANG;
            case "globe_banner_pattern" -> PATTERN_GLOBE;
            case "piglin_banner_pattern" -> PATTERN_PIGLIN;
            default -> PATTERN_NONE;
        };
    }

    @Nullable
    private static DyeColor getDyeColorFromItem(ItemStack itemStack) {
        if (itemStack.getItem() instanceof DyeItem dyeItem) {
            return dyeItem.getDyeColor();
        }
        return null;
    }

    private static int[] createBannerDyeColorTable() {
        int[] colors = new int[DyeColor.values().length];
        for (DyeColor dyeColor : DyeColor.values()) {
            float[] diffuseColors = dyeColor.getTextureDiffuseColors();
            int red = Math.round(diffuseColors[0] * 255.0f);
            int green = Math.round(diffuseColors[1] * 255.0f);
            int blue = Math.round(diffuseColors[2] * 255.0f);
            colors[dyeColor.getId()] = (red << 16) | (green << 8) | blue;
        }
        return colors;
    }

    private static boolean isRainbowNameTag(ItemStack itemStack) {
        return itemStack.is(Items.NAME_TAG)
                && itemStack.hasCustomHoverName()
                && "jeb_".equals(itemStack.getHoverName().getString());
    }

    private static boolean isWindFlagHalf(BlockState state, DoubleBlockHalf expectedHalf) {
        return state.getBlock() instanceof WindFlagBlock
                && state.hasProperty(HALF)
                && state.getValue(HALF) == expectedHalf;
    }

    public static int getOverlayTintColor(BlockState state, @Nullable BlockGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        int colorId = state.getValue(OVERLAY_COLOR);
        if (colorId == OVERLAY_COLOR_RAINBOW) {
            return getRainbowTintColor(level, pos);
        }
        if (colorId < 0 || colorId >= BANNER_DYE_COLOR_TABLE.length) {
            return -1;
        }
        return BANNER_DYE_COLOR_TABLE[colorId];
    }

    public static int getOverlayTintColor(ItemStack stack, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        int colorId = DEFAULT_OVERLAY_COLOR;
        CompoundTag blockStateTag = stack.getTagElement("BlockStateTag");
        if (blockStateTag != null) {
            if (blockStateTag.contains(OVERLAY_COLOR.getName(), Tag.TAG_ANY_NUMERIC)) {
                colorId = blockStateTag.getInt(OVERLAY_COLOR.getName());
            } else if (blockStateTag.contains(OVERLAY_COLOR.getName(), Tag.TAG_STRING)) {
                try {
                    colorId = Integer.parseInt(blockStateTag.getString(OVERLAY_COLOR.getName()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        colorId = Mth.clamp(colorId, 0, OVERLAY_COLOR_RAINBOW);
        if (colorId == OVERLAY_COLOR_RAINBOW) {
            return getRainbowTintColor(null, null);
        }
        if (colorId < 0 || colorId >= BANNER_DYE_COLOR_TABLE.length) {
            return -1;
        }
        return BANNER_DYE_COLOR_TABLE[colorId];
    }

    public static float getItemPatternMatchProperty(ItemStack stack, int expectedPattern) {
        int pattern = PATTERN_NONE;
        CompoundTag blockStateTag = stack.getTagElement("BlockStateTag");
        if (blockStateTag != null) {
            if (blockStateTag.contains(PATTERN.getName(), Tag.TAG_ANY_NUMERIC)) {
                pattern = blockStateTag.getInt(PATTERN.getName());
            } else if (blockStateTag.contains(PATTERN.getName(), Tag.TAG_STRING)) {
                try {
                    pattern = Integer.parseInt(blockStateTag.getString(PATTERN.getName()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        int clampedPattern = Math.max(PATTERN_NONE, Math.min(PATTERN_PIGLIN, pattern));
        int clampedExpectedPattern = Math.max(PATTERN_NONE, Math.min(PATTERN_PIGLIN, expectedPattern));
        return clampedPattern == clampedExpectedPattern ? 1.0f : 0.0f;
    }

    private static int getRainbowTintColor(@Nullable BlockGetter level, @Nullable BlockPos pos) {
        int colorCount = BANNER_DYE_COLOR_TABLE.length;
        if (colorCount == 0) {
            return -1;
        }

        double ticks = getRenderTimeTicks(level);
        int phaseOffset = pos == null ? 0 : (int) Math.floorMod(pos.asLong(), colorCount);
        double cycle = ticks / RAINBOW_TICKS_PER_COLOR;
        int baseStep = (int) Math.floor(cycle);
        int currentIndex = Math.floorMod(baseStep + phaseOffset, colorCount);
        int nextIndex = (currentIndex + 1) % colorCount;
        float blend = (float) (cycle - Math.floor(cycle));
        return lerpRgb(BANNER_DYE_COLOR_TABLE[currentIndex], BANNER_DYE_COLOR_TABLE[nextIndex], blend);
    }

    private static double getRenderTimeTicks(@Nullable BlockGetter level) {
        long nowMillis = System.currentTimeMillis();
        double partialTick = (nowMillis % MILLIS_PER_TICK) / (double) MILLIS_PER_TICK;
        if (level instanceof Level world) {
            return world.getGameTime() + partialTick;
        }
        return nowMillis / (double) MILLIS_PER_TICK;
    }

    private static int lerpRgb(int fromColor, int toColor, float delta) {
        int fromRed = (fromColor >> 16) & 0xFF;
        int fromGreen = (fromColor >> 8) & 0xFF;
        int fromBlue = fromColor & 0xFF;

        int toRed = (toColor >> 16) & 0xFF;
        int toGreen = (toColor >> 8) & 0xFF;
        int toBlue = toColor & 0xFF;

        int red = Mth.clamp(Math.round(Mth.lerp(delta, fromRed, toRed)), 0, 255);
        int green = Mth.clamp(Math.round(Mth.lerp(delta, fromGreen, toGreen)), 0, 255);
        int blue = Mth.clamp(Math.round(Mth.lerp(delta, fromBlue, toBlue)), 0, 255);
        return (red << 16) | (green << 8) | blue;
    }

    private static void writeStateTagToItem(ItemStack stack, BlockState state) {
        CompoundTag blockStateTag = stack.getOrCreateTagElement("BlockStateTag");
        blockStateTag.putString(PATTERN.getName(), Integer.toString(state.getValue(PATTERN)));
        blockStateTag.putString(OVERLAY_COLOR.getName(), Integer.toString(state.getValue(OVERLAY_COLOR)));
        blockStateTag.putString(EMISSIVE.getName(), Boolean.toString(state.getValue(EMISSIVE)));
        blockStateTag.putString(FURLED.getName(), Boolean.toString(state.getValue(FURLED)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FLAG_GROUP, OVERLAY_ONLY, EMISSIVE, FURLED, HALF, PATTERN, OVERLAY_COLOR);
    }
}
