package io.github.pasze888.miningpower;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = MiningPower.MODID)
public final class MiningPowerEvents {
    @SubscribeEvent
    public static void harvestCheck(PlayerEvent.HarvestCheck event) {
        ItemStack itemStack = event.getEntity().getMainHandItem();
        if (!itemStack.isEmpty() && itemStack.is(ItemTags.PICKAXES)) {
            event.setCanHarvest(DiggingPower.isCorrectToolForDrops(DiggingPower.getPower(itemStack), itemStack, event.getTargetBlock()));
        }
    }
}
