package io.github.pasze888.miningpower;

import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(MiningPower.MODID)
public class MiningPower {
    public static final String MODID = "miningpower";

    public MiningPower(IEventBus modEventBus) {
    }

    public static Identifier asResource(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }
}
