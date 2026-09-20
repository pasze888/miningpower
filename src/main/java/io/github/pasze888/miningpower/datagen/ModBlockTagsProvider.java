package io.github.pasze888.miningpower.datagen;

import io.github.pasze888.miningpower.MiningPower;
import io.github.pasze888.miningpower.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

/// 方块分级沿用 Confluence（汇流）的思路（原版高硬度方块归 3 级，4~9 级留给数据包/其他模组）：
/// 3 级门槛为镐力 ≥46，恰好落在原版「钻石可挖、铁不可挖」一侧。
public class ModBlockTagsProvider extends BlockTagsProvider {
    public ModBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MiningPower.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.NEEDS_3_LEVEL).add(
                Blocks.OBSIDIAN,
                Blocks.CRYING_OBSIDIAN,
                Blocks.ANCIENT_DEBRIS,
                Blocks.NETHERITE_BLOCK
        );
        // 出厂为空的等级也要产出标签文件（dummy 引用 minecraft:air），否则被引用的标签缺失、datagen 校验失败
        tag(ModTags.Blocks.NEEDS_4_LEVEL).add(Blocks.AIR);
        tag(ModTags.Blocks.NEEDS_5_LEVEL).add(Blocks.AIR);
        tag(ModTags.Blocks.NEEDS_6_LEVEL).add(Blocks.AIR);
        tag(ModTags.Blocks.NEEDS_7_LEVEL).add(Blocks.AIR);
        tag(ModTags.Blocks.NEEDS_8_LEVEL).add(Blocks.AIR);
        tag(ModTags.Blocks.NEEDS_9_LEVEL).add(Blocks.AIR);

        // 向下包含：needs_x 含一切 needs_{>x}，高档方块自动被低档排除
        tag(ModTags.Blocks.NEEDS_1_LEVEL).addTags(ModTags.Blocks.NEEDS_2_LEVEL, ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_2_LEVEL).addTags(ModTags.Blocks.NEEDS_3_LEVEL, ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_3_LEVEL).addTags(ModTags.Blocks.NEEDS_4_LEVEL, ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_4_LEVEL).addTags(ModTags.Blocks.NEEDS_5_LEVEL, ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_5_LEVEL).addTags(ModTags.Blocks.NEEDS_6_LEVEL, ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_6_LEVEL).addTags(ModTags.Blocks.NEEDS_7_LEVEL, ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_7_LEVEL).addTags(ModTags.Blocks.NEEDS_8_LEVEL, ModTags.Blocks.NEEDS_9_LEVEL);
        tag(ModTags.Blocks.NEEDS_8_LEVEL).addTags(ModTags.Blocks.NEEDS_9_LEVEL);

        // 并入原版 INCORRECT_FOR_* 标签，让 Tier 级检查（ItemStack#isCorrectToolForDrops）与镐力分级保持一致
        tag(BlockTags.INCORRECT_FOR_WOODEN_TOOL).addTag(ModTags.Blocks.NEEDS_2_LEVEL);
        tag(BlockTags.INCORRECT_FOR_GOLD_TOOL).addTag(ModTags.Blocks.NEEDS_2_LEVEL);
        tag(BlockTags.INCORRECT_FOR_STONE_TOOL).addTag(ModTags.Blocks.NEEDS_2_LEVEL);
        tag(BlockTags.INCORRECT_FOR_IRON_TOOL).addTag(ModTags.Blocks.NEEDS_3_LEVEL);
        tag(BlockTags.INCORRECT_FOR_DIAMOND_TOOL).addTag(ModTags.Blocks.NEEDS_4_LEVEL);
        tag(BlockTags.INCORRECT_FOR_NETHERITE_TOOL).addTag(ModTags.Blocks.NEEDS_6_LEVEL);

        // 挖不动就彻底无法破坏（Mixin 归 0 进度），而不是慢磨无掉落
        tag(ModTags.Blocks.UNBREAKABLE_IF_CANNOT_HARVEST).addTags(
                ModTags.Blocks.NEEDS_2_LEVEL,
                ModTags.Blocks.NEEDS_3_LEVEL,
                ModTags.Blocks.NEEDS_4_LEVEL,
                ModTags.Blocks.NEEDS_5_LEVEL,
                ModTags.Blocks.NEEDS_6_LEVEL,
                ModTags.Blocks.NEEDS_7_LEVEL,
                ModTags.Blocks.NEEDS_8_LEVEL,
                ModTags.Blocks.NEEDS_9_LEVEL
        );
    }
}
