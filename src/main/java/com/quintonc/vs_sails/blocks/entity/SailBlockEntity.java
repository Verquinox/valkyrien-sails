package com.quintonc.vs_sails.blocks.entity;

//import com.quintonc.vs_sails.util.ConfigUtils;
//import com.quintonc.vs_sails.util.PatternProcessor;


//public class SailBlockEntity extends BlockEntity {
//
//    private static final int sailspeed = 10000;
//    public static final Logger LOGGER = LoggerFactory.getLogger("sail_entity");
//
//    public SailBlockEntity(BlockPos pos, BlockState state) {
//        super(ValkyrienSails.SAIL_BLOCK_ENTITY, pos, state);
//    }
//
//    public static void tick(World world, BlockPos pos, BlockState state, SailBlockEntity be) {
//
//        //future behavior for sails only working when other sails adjacent
////        if (!(world.getBlockState(pos.up()).getBlock() instanceof SailBlock)) {
////            return;
////        }
//
//        if (!world.isClient) {
//
//            if (VSGameUtilsKt.isBlockInShipyard(world, pos)) {
//
//
//                ChunkPos chunkPos = world.getChunk(pos).getPos();
//                LoadedServerShip ship = VSGameUtilsKt.getShipObjectManagingPos((ServerWorld) world, chunkPos);
//
//                if (ship != null) {
////                    SeatedControllingPlayer seatedControllingPlayer = ship.getAttachment(SeatedControllingPlayer.class);
////                    if (seatedControllingPlayer == null) {
////                        if (world.getBlockState(pos.up()).getBlock() instanceof SailBlock) {
////                            seatedControllingPlayer = new SeatedControllingPlayer(world.getBlockState(pos.up()).get(HORIZONTAL_FACING).getOpposite());
////                        } else {
////                            return;
////                        }
////                        ship.setAttachment(SeatedControllingPlayer.class, seatedControllingPlayer);
////                    }
//
//
//                    //be.moveShipForward(ship, state);
//
//
//                }
//            }
//        }
//
//    }
//
//
//
//    /**
//     * This method drives the ship forward
//     * @param ship the ship the block is on
//     * @param state blockstate of the sail block
//     */
//    private void moveShipForward(LoadedServerShip ship, BlockState state)
//    {
//        LOGGER.info("move ship forward");
//        //double mass = ship.getInertiaData().getMass();
//        if (state.get(SET)) {
//            Vector3d dirfor = new Vector3d(sailspeed, 0, 0);
//            SailsShipControl shipForceApplier = ship.getAttachment(SailsShipControl.class);
//            //if (shipForceApplier != null) {
//                //shipForceApplier.applyRotDependentForceToPos(dirfor, ship.getInertiaData().getCenterOfMassInShip()); fixme put back to applyRotDependentForce()
//            //}
//        }
//    }
//}