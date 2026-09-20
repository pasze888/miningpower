# Mining Power（挖掘等级 / 镐力）

[English](README.md) | [简体中文](README.zh-CN.md)

Terraria 式挖掘等级系统，NeoForge 1.21.1，从 [Confluence（汇流）](https://github.com/Magic-team-jvav/confluence)
模组提取（LGPL-3.0-or-later）。

方块按九个离散等级分级（`miningpower:needs_1_level` … `needs_9_level` 方块标签，向下包含）；
工具携带连续的「镐力」数值，镐力决定它能收掉多高等级的方块。镐力不足时，高等级方块
完全无法破坏——像基岩一样。

## 机制

| 镐力 | 可挖等级 |
|---|---|
| >= 201 | 9（通挖） |
| >= 191 | 8 |
| >= 131 | 7 |
| >= 101 | 6 |
| >= 71 | 5 |
| >= 60 | 4 |
| >= 51 | 3 |
| >= 46 | 2 |
| >= 34 | 1 |

原版 Tier 映射：木 35、石 38、金 39、铁 40、钻石 59、下界合金 90。
默认把黑曜石、哭泣的黑曜石、远古残骸、块状下界合金归入 3 级：钻石及以上仍可挖，
铁镐不再能收黑曜石。4~9 级出厂为空，作为数据包与其他模组的挂载点。

只有主手物品属于 `minecraft:pickaxes` 标签时才走镐力判定。

## 扩展方式

- **方块等级**：任意数据包往 `miningpower:needs_x_level` 标签加方块即可。
- **物品镐力**：通过数据包给任意物品写 `miningpower:digging_power` data map（正整数），
  或在大事件总线订阅 `GetCustomDiggingPowerEvent` 动态指定。
- **硬锁集合**：`miningpower:unbreakable_if_cannot_harvest` 标签内的方块在收割判定失败时
  挖掘进度归 0（彻底挖不开）；默认覆盖 `needs_2..9`。

## 常用命令

```
./gradlew build          # 编译 + 打 jar
./gradlew runData        # 重新生成方块标签到 src/generated/resources
./gradlew runClient      # 开发客户端
```

## 许可与署名

LGPL-3.0-or-later。核心逻辑（`DiggingPower`、收割判定、挖掘进度 Mixin）改写自
Confluence 模组的 `org.confluence.mod.common.init.ModTiers`、`common.data.map.DiggingPower`
与 `mixin.block.BlockBehaviourMixin`。
