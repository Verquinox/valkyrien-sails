package com.quintonc.vs_sails.client.particles;

import com.quintonc.vs_sails.client.ClientWindManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.client.particle.*;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Unique;
import org.valkyrienskies.mod.common.VSGameUtilsKt;

public class WindParticle extends TextureSheetParticle {
    public static final Logger LOGGER = LoggerFactory.getLogger("wind_particle");

    protected WindParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteSet, double xd, double yd, double zd) {
        super(world, x, y, z, xd, yd, zd);

        this.friction = 0.6f;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        this.quadSize = 0.25f;
        this.lifetime = 20;
        this.alpha = 0;
        this.setSpriteFromAge(spriteSet);

        this.rCol = 1f;
        this.gCol = 1f;
        this.bCol = 1f;

    }

    @Override
    public void tick() {
        super.tick();
        this.xd = modifyDx(this.xd);
        this.zd = modifyDz(this.zd);
        fade();
    }

    private void fade() {
        double windStrength = ClientWindManager.getWindStrength();
        //int lightLevel = this.world.getLightLevel(LightType.SKY, new BlockPos((int) this.x, (int) this.y, (int) this.z));
        //todo how would I get the viewing player to make the particles fade out when they get close to the player?
        this.alpha = (float)(Math.abs(windStrength)*0.75*Math.sin((Math.PI*age)/lifetime));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(EnvType.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;
        public Factory(SpriteSet spriteSet) {
            sprites = spriteSet;
        }

        @Override
        public @Nullable Particle createParticle(SimpleParticleType parameters, ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
            return new WindParticle(world, x, y, z, this.sprites, velocityX, velocityY, velocityZ);
        }
    }

    private double modifyDx(double dx) {
        //String simpleName = this.getClass().getSimpleName();
//		System.out.println(simpleName);

//        if (this.world.getLightLevel(LightType.SKY, new BlockPos((int) this.x, (int) this.y, (int) this.z)) == 0 || this.y < 40) {
//            return dx;
//        }

        Vec3 windEffect = calculateWindEffect();
        Vec3 particlePos = new Vec3(this.x, this.y, this.z);
        Vec3 windDirection = new Vec3(Math.cos(Math.toRadians(ClientWindManager.getWindDirection())), 0, Math.sin(Math.toRadians(ClientWindManager.getWindDirection())));

        double windInfluenceFactor = getWindInfluenceFactor(particlePos, windDirection);
        return dx + windEffect.x * windInfluenceFactor;
    }

    private double modifyDz(double dz) {

//        if (this.world.getLightLevel(LightType.SKY, new BlockPos((int) this.x, (int) this.y, (int) this.z)) == 0 || this.y < 40) {
//            return dz;
//        }


        Vec3 windEffect = calculateWindEffect();
        Vec3 particlePos = new Vec3(this.x, this.y, this.z);
        Vec3 windDirection = new Vec3(Math.cos(Math.toRadians(ClientWindManager.getWindDirection())), 0, Math.sin(Math.toRadians(ClientWindManager.getWindDirection())));

        double windInfluenceFactor = getWindInfluenceFactor(particlePos, windDirection);
        return dz + windEffect.z * windInfluenceFactor;
    }

    private double getWindInfluenceFactor(Vec3 particlePosition, Vec3 windDirection) {
        int range = 5; // Define how far back in the direction from which the wind comes we should check
        Vec3 invertedWindDirection = windDirection.scale(-1); // Invert wind direction for checking

        for (int i = 1; i <= range; i++) {
            Vec3 checkPosition = particlePosition.add(invertedWindDirection.scale(i));
            BlockPos pos = new BlockPos((int) checkPosition.x(), (int) checkPosition.y(), (int) checkPosition.z());
            BlockState state = level.getBlockState(pos);

            if (state.isAir() || isNonSolidBlock(state)) {
                return 1; // Full influence if wind exposure is confirmed
            }
        }
        return 0.0;
    }

    @Unique
    private boolean isNonSolidBlock(BlockState state) {
        return state.is(Blocks.GLASS) || state.is(Blocks.OAK_LEAVES) || state.is(Blocks.IRON_BARS) ||
                state.getFluidState().getType() == Fluids.WATER || state.getFluidState().getType() == Fluids.LAVA;
    }

    @Unique
    private Vec3 calculateWindEffect() {
        double windEffectiveness = 2;

        double angleRadians = Math.toRadians(ClientWindManager.getWindDirection());
        double windX = Math.cos(angleRadians) * ClientWindManager.getWindStrength() * windEffectiveness;
        double windZ = Math.sin(angleRadians) * ClientWindManager.getWindStrength() * windEffectiveness;
        Vec3 initialWindEffect = new Vec3(windX, 0, windZ);

        BlockPos pos = new BlockPos((int) this.x, (int) this.y, (int) this.z);
        return calculateRealisticWindFlow(initialWindEffect, pos);
    }

    private boolean checkForWallInteraction(BlockPos particlePos) {
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(particlePos.relative(dir));
            if (state.isRedstoneConductor(level, particlePos.relative(dir))) {
                return true;
            }
        }
        return false;
    }

    private Vec3 deflectWind(double windX, double windZ, BlockPos pos) {
        Direction windDirection = getWindDirection(windX, windZ);
        Direction wallDirection = getWallFacingDirection(pos, windDirection);
        double incidenceAngle = calculateIncidenceAngle(windDirection, wallDirection);
        double deflectionFactor = calculateDeflectionFactor(incidenceAngle, windX, windZ);

        double deflectedWindX = windX * deflectionFactor;
        double deflectedWindZ = windZ * deflectionFactor;

        deflectedWindX += randomizeDeflection(incidenceAngle);
        deflectedWindZ += randomizeDeflection(incidenceAngle);

        return new Vec3(deflectedWindX, 0, deflectedWindZ);
    }

    private double randomizeDeflection(double incidenceAngle) {
        return Math.random() * Math.cos(Math.toRadians(incidenceAngle)) * 0.05;
    }

    private Direction getWindDirection(double windX, double windZ) {
        double angle = Math.toDegrees(Math.atan2(windZ, windX));
        if (angle < 0) angle += 360;
        if (angle <= 45 || angle > 315) return Direction.EAST;
        if (angle > 45 && angle <= 135) return Direction.SOUTH;
        if (angle > 135 && angle <= 225) return Direction.WEST;
        if (angle > 225) return Direction.NORTH;
        return Direction.EAST;
    }

    private Direction getWallFacingDirection(BlockPos pos, Direction windDirection) {
        for (Direction dir : Direction.values()) {
            BlockState state = level.getBlockState(pos.relative(dir));
            if (state.isRedstoneConductor(level, pos.relative(dir)) && dir.getAxis().isHorizontal()) {
                return dir;
            }
        }
        return windDirection;
    }

    private double calculateIncidenceAngle(Direction windDirection, Direction wallDirection) {
        int windAngle = directionToAngle(windDirection);
        int wallAngle = directionToAngle(wallDirection);
        int angleDifference = Math.abs(windAngle - wallAngle);

        if (angleDifference > 180) {
            angleDifference = 360 - angleDifference;
        }

        return angleDifference;
    }

    private int directionToAngle(Direction direction) {
        return switch (direction) {
            case NORTH -> 180;
            case WEST -> 270;
            case EAST -> 90;
            default -> 0; // SOUTH
        };
    }

    private double calculateDeflectionFactor(double incidenceAngle, double windX, double windZ) {
        double baseDeflection = 0.01;
        double velocityFactor = Math.sqrt(windX * windX + windZ * windZ) * 0.01;
        double angleFactor = Math.cos(Math.toRadians(incidenceAngle));
        return baseDeflection * angleFactor * velocityFactor;
    }

    private boolean checkForLaminarFlow(double incidenceAngle) {
        return incidenceAngle < 45;
    }

    private Vec3 adjustWindFlow(Vec3 windEffect, BlockPos pos, double windX, double windZ) {
        Direction windDirection = getWindDirection(windX, windZ);
        Direction wallDirection = getWallFacingDirection(pos, windDirection);
        double incidenceAngle = calculateIncidenceAngle(windDirection, wallDirection);

        if (checkForLaminarFlow(incidenceAngle)) {
            return slideWindAlongWall(windEffect, wallDirection);
        } else {
            return deflectWind(windX, windZ, pos);
        }
    }

    private Vec3 slideWindAlongWall(Vec3 windEffect, Direction wallDirection) {
        return switch (wallDirection) {
            case NORTH, SOUTH -> new Vec3(windEffect.x, windEffect.y, 0);
            case EAST, WEST -> new Vec3(0, windEffect.y, windEffect.z);
            default -> windEffect;
        };
    }

    private Vec3 funnelWindAroundStructure(Vec3 windEffect, BlockPos pos) {
        Direction windDirection = getWindDirection(windEffect.x, windEffect.z);
//        if (VSGameUtilsKt.isBlockInShipyard(world, pos)) { //fixme wip wind deflecting around ship blocks
//            Vector3d newPos = Objects.requireNonNull(VSGameUtilsKt.getShipObjectManagingPos(world, pos)).getShipToWorld().transformPosition(new Vector3d(pos.getX(),pos.getY(),pos.getZ()));
//            if (newPos != null) {
//                pos = new BlockPos(newPos.x,newPos.y,newPos.z);
//            }
//        }
        Direction wallDirection = getWallFacingDirection(pos, windDirection);
        double incidenceAngle = calculateIncidenceAngle(windDirection, wallDirection);

        if (incidenceAngle >= 45 && incidenceAngle <= 135) {
            double funnelFactor = 1.0 + (1.0 - Math.cos(Math.toRadians(incidenceAngle))) * 0.5;
            return windEffect.scale(funnelFactor);
        }

        return windEffect;
    }

    private boolean isNearTunnel(BlockPos pos) {
        int airCount = 0;
        int solidCount = 0;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockPos checkPos = pos.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(checkPos);

                    if (state.isAir()) {
                        airCount++;
                    } else if (state.isRedstoneConductor(level, checkPos)) {
                        solidCount++;
                    }
                }
            }
        }

        return airCount >= 15 && solidCount >= 10;
    }

    private Vec3 adjustForTunnelAttraction(Vec3 windEffect, BlockPos pos) {
        if (isNearTunnel(pos)) {
            double attractionFactor = 1.5;
            return windEffect.scale(attractionFactor);
        }
        return windEffect;
    }

    private Vec3 calculateRealisticWindFlow(Vec3 windEffect, BlockPos pos) {
        if (checkForWallInteraction(pos)) {
            windEffect = adjustWindFlow(windEffect, pos, windEffect.x, windEffect.z);
        }
        windEffect = funnelWindAroundStructure(windEffect, pos);
        return adjustForTunnelAttraction(windEffect, pos);
    }

}
