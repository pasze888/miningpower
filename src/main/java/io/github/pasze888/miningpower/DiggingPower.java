package io.github.pasze888.miningpower;

import com.mojang.serialization.Codec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

/// 镐力（挖掘能力数值），一般用于镐。
/// 判定逻辑改写自 Confluence（汇流）模组的 `org.confluence.mod.common.init.ModTiers`
/// 与 `org.confluence.mod.common.data.map.DiggingPower`（LGPL-3.0-or-later，上游
/// https://github.com/Magic-team-jvav/confluence ），整体是 Terraria 式
/// 「连续镐力 → 离散挖掘等级」方案（参见 https://terraria.wiki.gg/zh/wiki/%E9%95%90 ）。
public record DiggingPower(int power) {
    public static final Codec<DiggingPower> CODEC = ExtraCodecs.POSITIVE_INT.xmap(DiggingPower::new, DiggingPower::power);

    /// 取值优先级：`miningpower:digging_power` data map → 原版 Tier 映射 → 事件放行。
    /// 无挖掘能力的物品返回 -1。
    public static int getPower(ItemStack itemStack) {
        int power = -1;
        DiggingPower diggingPower = itemStack.getItemHolder().getData(ModDataMaps.DIGGING_POWER);
        if (diggingPower != null) {
            power = diggingPower.power;
        } else if (itemStack.getItem() instanceof TieredItem tieredItem && tieredItem.getTier() instanceof Tiers tiers) {
            power = getPowerForVanillaTier(tiers);
        }
        return NeoForge.EVENT_BUS.post(new GetCustomDiggingPowerEvent(itemStack, power)).getPower();
    }

    /// 原版 Tiers 的对应镐力
    public static int getPowerForVanillaTier(Tiers tiers) {
        return switch (tiers) {
            case WOOD -> 35;
            case STONE -> 38;
            case GOLD -> 39;
            case IRON -> 40;
            case DIAMOND -> 59;
            case NETHERITE -> 90;
        };
    }

    /// 镐力 ======================= 等级
    /// 全都能挖
    /// 201 ======================= 9
    /// 191 ======================= 8
    /// 131 ======================= 7
    /// 101 ======================= 6
    ///  71 ======================= 5
    ///  60 ======================= 4
    ///  51 ======================= 3
    ///  46 ======================= 2
    ///  34 ======================= 1
    ///  什么也挖不了（仅能收 1 级以下）
    public static boolean isCorrectToolForDrops(int power, ItemStack pickaxeItem, BlockState blockState) {
        if (!blockState.requiresCorrectToolForDrops()) return true;
        if (!pickaxeItem.isCorrectToolForDrops(blockState)) return false;
        if (power == -1 || power >= 201) return true;
        if (power >= 191) return !blockState.is(ModTags.Blocks.NEEDS_9_LEVEL);
        if (power >= 131) return !blockState.is(ModTags.Blocks.NEEDS_8_LEVEL);
        if (power >= 101) return !blockState.is(ModTags.Blocks.NEEDS_7_LEVEL);
        if (power >= 71) return !blockState.is(ModTags.Blocks.NEEDS_6_LEVEL);
        if (power >= 60) return !blockState.is(ModTags.Blocks.NEEDS_5_LEVEL);
        if (power >= 51) return !blockState.is(ModTags.Blocks.NEEDS_4_LEVEL);
        if (power >= 46) return !blockState.is(ModTags.Blocks.NEEDS_3_LEVEL);
        if (power >= 34) return !blockState.is(ModTags.Blocks.NEEDS_2_LEVEL);
        return !blockState.is(ModTags.Blocks.NEEDS_1_LEVEL);
    }
}
