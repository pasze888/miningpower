package io.github.pasze888.miningpower;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Map;

/// 镐力（挖掘能力数值），一般用于镐。
/// 判定逻辑改写自 Confluence（汇流）模组的 `org.confluence.mod.common.init.ModTiers`
/// 与 `org.confluence.mod.common.data.map.DiggingPower`（LGPL-3.0-or-later，上游
/// https://github.com/Magic-team-jvav/confluence ），整体是 Terraria 式
/// 「连续镐力 → 离散挖掘等级」方案（参见 https://terraria.wiki.gg/zh/wiki/%E9%95%90 ）。
public record DiggingPower(int power) {
    public static final Codec<DiggingPower> CODEC = ExtraCodecs.POSITIVE_INT.xmap(DiggingPower::new, DiggingPower::power);

    /// 原版 ToolMaterial 的「不可掉落」标签 → 镐力。1.21.2 起 `Tier`/`Tiers` 被 `ToolMaterial` 取代，
    /// 物品上不再保留材料实例，只能通过 `DataComponents#TOOL` 里那条 deniesDrops 规则引用的标签反查。
    /// COPPER 是后来新增的材料，上游 Confluence 无对应数值，这里取在 STONE(38) 与 IRON(40) 之间。
    private static final Map<TagKey<Block>, Integer> VANILLA_MATERIAL_POWER = Map.of(
            ToolMaterial.WOOD.incorrectBlocksForDrops(), 35,
            ToolMaterial.STONE.incorrectBlocksForDrops(), 38,
            ToolMaterial.COPPER.incorrectBlocksForDrops(), 39,
            ToolMaterial.GOLD.incorrectBlocksForDrops(), 39,
            ToolMaterial.IRON.incorrectBlocksForDrops(), 40,
            ToolMaterial.DIAMOND.incorrectBlocksForDrops(), 59,
            ToolMaterial.NETHERITE.incorrectBlocksForDrops(), 90);

    /// 取值优先级：`miningpower:digging_power` data map → 原版材料映射 → 事件放行。
    /// 无挖掘能力的物品返回 -1。
    public static int getPower(ItemStack itemStack) {
        int power = -1;
        DiggingPower diggingPower = itemStack.typeHolder().getData(ModDataMaps.DIGGING_POWER);
        if (diggingPower != null) {
            power = diggingPower.power;
        } else {
            power = getPowerForVanillaMaterial(itemStack);
        }
        return NeoForge.EVENT_BUS.post(new GetCustomDiggingPowerEvent(itemStack, power)).getPower();
    }

    /// 从 `DataComponents#TOOL` 的 deniesDrops 规则取出它引用的标签，据此识别原版材料。
    /// 非原版材料（或无 tool 组件）返回 -1，交由 data map / 事件决定。
    private static int getPowerForVanillaMaterial(ItemStack itemStack) {
        Tool tool = itemStack.get(DataComponents.TOOL);
        if (tool == null) return -1;
        for (Tool.Rule rule : tool.rules()) {
            Boolean correctForDrops = rule.correctForDrops().orElse(null);
            if (correctForDrops != null && !correctForDrops) {
                TagKey<Block> tag = rule.blocks().unwrapKey().orElse(null);
                if (tag != null) return VANILLA_MATERIAL_POWER.getOrDefault(tag, -1);
            }
        }
        return -1;
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
