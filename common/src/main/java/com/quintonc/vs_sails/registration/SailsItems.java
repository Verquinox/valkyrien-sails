package com.quintonc.vs_sails.registration;

import com.quintonc.vs_sails.ValkyrienSails;
import com.quintonc.vs_sails.items.DedicationBottle;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class SailsItems {
    private static DeferredRegister<Item> ITEMS = DeferredRegister.create(ValkyrienSails.MOD_ID, Registries.ITEM);

    public static RegistrySupplier<Item> DEDICATION_BOTTLE;
    public static RegistrySupplier<Item> ROPE;

    public static void register() {
        SailsBlocks.registerItems(ITEMS);
        ROPE = ITEMS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "rope"), () -> new Item(new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        DEDICATION_BOTTLE = ITEMS.register(ResourceLocation.tryBuild(ValkyrienSails.MOD_ID, "dedication_bottle"), () -> new DedicationBottle(new Item.Properties().arch$tab(ValkyrienSails.SAILS_MAIN)));
        ITEMS.register();
    }

}
