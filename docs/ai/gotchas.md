# AI 协作坑记录

## NeoForge datagen：被 `addTags` 引用的标签必须有自身产物文件

`BlockTagsProvider` 里用 `tag(A).addTags(B)` 建立标签间引用（本模组 needs_x 向下包含）时，
B 必须在本次 datagen 中至少写入一个条目，否则 `runData` 在最后校验阶段抛：

```
java.lang.IllegalArgumentException: Couldn't define tag miningpower:needs_3_level
as it is missing following references: #miningpower:needs_9_level
```

空标签只被引用、自身没有任何 `add`/`addTags` 调用时不会产出 JSON 文件，引用方就解析失败。

**解法**：给出厂为空的标签放一个占位条目。本模组用 `tag(NEEDS_4..9_LEVEL).add(Blocks.AIR)`，
见 `src/main/java/io/github/pasze888/miningpower/datagen/ModBlockTagsProvider.java`。
副作用可忽略：`minecraft:air` 永远不会走到挖掘判定（`requiresCorrectToolForDrops()` 为 false
提前返回）。其他替代方案（`addOptional`、required=false 引用）未在本项目验证。

## `BlockBehaviour#getDestroyProgress` 局部变量 `i` 不是计时器

1.21.1 NeoForge 补丁版方法体（api-sources `net/minecraft/world/level/block/state/BlockBehaviour.java:393-401`）：

```java
float f = state.getDestroySpeed(level, pos);
if (f == -1.0F) return 0.0F;
int i = EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
return player.getDigSpeed(state, pos) / f / (float) i;
```

confluence 的 Mixin（本项目 `BlockBehaviourMixin#miningpower$deny` 改写自它）里
`@Local int i` 条件是 `i > 30`，语义是「收割判定失败（惩罚值 100）」，不是
「挖了超过 30 tick」。照抄 confluence 相关 Mixin 时不要按字面把它理解成计时。

推论：`HarvestCheck` 事件会在客户端挖掘动画中高频触发，`GetCustomDiggingPowerEvent`
订阅方需保持廉价。
