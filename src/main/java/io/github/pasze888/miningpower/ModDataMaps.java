package io.github.pasze888.miningpower;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = MiningPower.MODID, bus = EventBusSubscriber.Bus.MOD)
public final class ModDataMaps {
    /// 给任意物品注册静态镐力的数据通道，数据包亦可写入（随 S2C 自动同步）。
    public static final DataMapType<Item, DiggingPower> DIGGING_POWER = DataMapType
            .builder(MiningPower.asResource("digging_power"), Registries.ITEM, DiggingPower.CODEC)
            .synced(DiggingPower.CODEC, false)
            .build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(DIGGING_POWER);
    }
}
