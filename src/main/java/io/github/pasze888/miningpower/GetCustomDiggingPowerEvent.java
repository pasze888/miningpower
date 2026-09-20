package io.github.pasze888.miningpower;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.Event;

/// 挂在大事件总线上，在 [DiggingPower#getPower(ItemStack)] 兜底阶段发出。
/// 其他模组可订阅它给任意物品动态指定镐力（例如按 NBT 强化）。
public class GetCustomDiggingPowerEvent extends Event {
    private final ItemStack itemStack;
    private int power;

    public GetCustomDiggingPowerEvent(ItemStack itemStack, int power) {
        this.itemStack = itemStack;
        this.power = power;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getPower() {
        return power;
    }
}
