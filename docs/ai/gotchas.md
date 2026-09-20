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

## `getDestroyProgress` 相关 API 事实

已移至 [docs/reference/digging-power.md](../reference/digging-power.md)：局部变量 `i` 是
收割惩罚值（30/100）而非计时器，及本系统各判定入口的签名出处。
