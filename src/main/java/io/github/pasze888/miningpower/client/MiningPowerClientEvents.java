package io.github.pasze888.miningpower.client;

import io.github.pasze888.miningpower.DiggingPower;
import io.github.pasze888.miningpower.MiningPower;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = MiningPower.MODID, value = Dist.CLIENT)
public final class MiningPowerClientEvents {
    @SubscribeEvent
    public static void itemToolTip(ItemTooltipEvent event) {
        ItemStack itemStack = event.getItemStack();
        if (!itemStack.is(ItemTags.PICKAXES)) return;
        int power = DiggingPower.getPower(itemStack);
        if (power > 0) {
            event.getToolTip().add(Component.translatable("tooltip.miningpower.digging_power", power).withStyle(ChatFormatting.GRAY));
        }
    }
}
