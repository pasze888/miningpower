package io.github.pasze888.miningpower.datagen;

import io.github.pasze888.miningpower.MiningPower;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = MiningPower.MODID, value = Dist.CLIENT)
public final class ModDataGenerators {
    /// 26.1.2 起 GatherDataEvent 分为 Client/Server 两种；MDG 的 data() 只触发 Client 事件
    /// （NeoForge 把 client 与 server 数据生成合并到同一次 clientData run），故这里监听 Client。
    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new ModBlockTagsProvider(packOutput, lookupProvider));
    }
}
