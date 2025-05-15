package com.quintonc.vs_sails.items;

import com.quintonc.vs_sails.ship.SailsShipControl;
import g_mungus.vlib.api.VLibGameUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.DebugStickItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.logging.log4j.core.jmx.Server;
import org.valkyrienskies.core.api.ships.*;
import org.valkyrienskies.mod.common.VSGameUtilsKt.*;
import static org.valkyrienskies.mod.common.VSGameUtilsKt.*;

import javax.swing.*;

public class SailWand extends Item {

    public int mode;

    public SailWand(Settings settings) {
        super(settings);
        mode = 0;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {

//        if (!context.getWorld().isClient) {
//            ServerShip ship = getShipObjectManagingPos((ServerWorld)context.getWorld(), context.getBlockPos());
//            if (ship != null) {
//                SailsShipControl controller = ship.getAttachment(SailsShipControl.class);
//                assert controller != null;
//                switch (mode) {
//                    case 0:
//                        controller.numSails = 0;
//                        break;
//                    case 1:
//                        controller.numBallast = 0;
//                        break;
//                    case 2:
//                        controller.numMagicBallast = 0;
//                        break;
//                    case 3:
//                        controller.numBuoys = 0;
//                        break;
//                }
//            }
//        }
        if (!context.getWorld().isClient) {
            VLibGameUtils.INSTANCE.assembleByConnectivity((ServerWorld)context.getWorld(), context.getBlockPos());
            return ActionResult.SUCCESS;
        }

        return ActionResult.FAIL;
    }

//    @Override
//    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
//        if (clickType == ClickType.LEFT) {
//            mode = mode+1 % 4;
//            player.sendMessage(Text.of("Mode: "+ modeToString()), true);
//            return true;
//        }
//        return false;
//    }

//    @Override
//    private boolean use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
//        if (!player.isCreativeLevelTwoOp()) {
//            return false;
//        } else {
//            mode = mode+1 % 4;
//            player.sendMessage(Text.of("Mode: "+ modeToString()), true);
//        }
//
//        return true;
//    }

    public String modeToString() {
        String string = "invalid: "+mode;
        switch (mode) {
            case 0:
                string =  "Sail Reset";
                break;
            case 1:
                string = "Ballast Reset";
                break;
            case 2:
                string = "Magic Ballast Reset";
                break;
            case 3:
                string = "Buoy Reset";
                break;
        }
        return string;
    }
}
