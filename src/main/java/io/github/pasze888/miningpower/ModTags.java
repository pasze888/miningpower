package io.github.pasze888.miningpower;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

/// 1~9 级挖掘等级标签。**向下包含**：`needs_2_level` 包含 `needs_3_level` 及更高级，
/// 因此判定只需从高档往低档依次检查 `!state.is(NEEDS_x)`。
public final class ModTags {
    public static class Blocks {
        public static final TagKey<Block> NEEDS_1_LEVEL = register("needs_1_level");
        public static final TagKey<Block> NEEDS_2_LEVEL = register("needs_2_level");
        public static final TagKey<Block> NEEDS_3_LEVEL = register("needs_3_level");
        public static final TagKey<Block> NEEDS_4_LEVEL = register("needs_4_level");
        public static final TagKey<Block> NEEDS_5_LEVEL = register("needs_5_level");
        public static final TagKey<Block> NEEDS_6_LEVEL = register("needs_6_level");
        public static final TagKey<Block> NEEDS_7_LEVEL = register("needs_7_level");
        public static final TagKey<Block> NEEDS_8_LEVEL = register("needs_8_level");
        public static final TagKey<Block> NEEDS_9_LEVEL = register("needs_9_level");
        /// 镐力不足时完全挖不动（挖掘进度归 0）的方块集合
        public static final TagKey<Block> UNBREAKABLE_IF_CANNOT_HARVEST = register("unbreakable_if_cannot_harvest");

        private static TagKey<Block> register(String id) {
            return BlockTags.create(MiningPower.asResource(id));
        }
    }
}
