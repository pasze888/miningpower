package io.github.pasze888.miningpower;

import net.neoforged.neoforge.common.ModConfigSpec;

/// 模组配置（COMMON 类型，位于 `config/miningpower-common.toml`）。
/// 客户端与服务端各自读取本地文件，**不跨网络同步**：专用服务器上两端需保持一致的取值。
public class Config {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /// `miningpower:unbreakable_if_cannot_harvest` 标签内的方块，在镐力不足（收割判定失败）时是否
    /// 彻底无法挖掘（挖掘进度归 0，表现为基岩）。默认启用；关闭后退回原版「工具不对」：挖得慢且不掉落。
    public static final ModConfigSpec.BooleanValue UNBREAKABLE_IF_CANNOT_HARVEST = BUILDER
            .comment(
                    "Whether blocks in the miningpower:unbreakable_if_cannot_harvest tag become completely unbreakable",
                    "(destroy progress forced to 0) when the harvest check fails.",
                    "true  = blocks cannot be broken at all, Terraria-style bedrock behaviour (default)",
                    "false = vanilla wrong-tool behaviour: the block breaks slowly and drops nothing"
            )
            .translation("miningpower.configuration.unbreakable_if_cannot_harvest")
            .define("unbreakableIfCannotHarvest", true);

    public static final ModConfigSpec SPEC = BUILDER.build();
}
