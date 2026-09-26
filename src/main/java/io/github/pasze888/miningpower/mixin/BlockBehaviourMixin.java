package io.github.pasze888.miningpower.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.pasze888.miningpower.ModTags;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/// 镐力不足（收割判定失败，局部变量 i 取惩罚值 100 而非 30）时，
/// 属于 `unbreakable_if_cannot_harvest` 标签的方块挖掘进度归 0——表现为基岩一样挖不开。
/// 改写自 Confluence（汇流）模组 `org.confluence.mod.mixin.block.BlockBehaviourMixin#deny`
/// （LGPL-3.0-or-later）。
@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {
    @ModifyExpressionValue(method = "getDestroyProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getDestroySpeed(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)F"))
    private float miningpower$deny(float original, @Local(argsOnly = true) BlockState state, @Local int i) {
        if (i > 30 && state.is(ModTags.Blocks.UNBREAKABLE_IF_CANNOT_HARVEST)) {
            return 0.0F;
        }
        return original;
    }
}
