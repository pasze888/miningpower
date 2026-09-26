# 镐力（digging power）系统验证过的 API 事实

以下签名与行为均在 **Minecraft 26.1.2 / NeoForge 26.1.2.99** 对照反编译源码核实。出处：

- Minecraft（含 NeoForge 补丁）反编译源码：`~/.gradle/caches/neoformruntime/intermediate_results/mergeWithSources_*_output.jar`
  ——NeoForm 产物，同一目录的 `recompile_*.txt` 里记着 `minecraft_26.1.2_version_manifest.json`，可据此确认版本。
- NeoForge 自有类：`~/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/26.1.2.99/*/neoforge-26.1.2.99-sources.jar`。

> ⚠️ 工作区共享的 `api-sources/` 是 **1.21.1**（判定依据：其中只有
> `net/minecraft/resources/ResourceLocation.java`，没有 26.x 的 `Identifier.java`），
> 本分支的签名不要拿它对照；下面所有行号都指上列 jar 内的文件。

## `BlockBehaviour#getDestroyProgress` 方法体与局部变量 `i`

`net/minecraft/world/level/block/state/BlockBehaviour.java:340-348`：

```java
protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
    float destroySpeed = state.getDestroySpeed(level, pos);
    if (destroySpeed == -1.0F) {
        return 0.0F;
    } else {
        int i = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
        return player.getDestroySpeed(state, pos) / destroySpeed / (float)i;
    }
}
```

- 局部变量 `i` 是**收割判定惩罚值**（可收 30 / 不可收 100），不是计时器。
  `mixin/BlockBehaviourMixin#miningpower$deny` 的 `@Local int i` 条件 `i > 30` 语义即
  「收割判定失败」。confluence 上游同款 Mixin 也依赖这一事实（其注释易被误读为
  「挖掘超过 30 tick」）。
- 1.21.1 时代本方法在 `api-sources` 里是 `BlockBehaviour.java:393-401`、局部变量名 `f`；
  26.1.2 行号变为 340-348、局部变量名变为 `destroySpeed`，注入点（那句 `EventHooks.doPlayerHarvestCheck`
  所在的除法表达式）本身没变。
- 判定链：`EventHooks#doPlayerHarvestCheck`（`net/neoforged/neoforge/event/EventHooks.java:223`，
  内部先取 `Player#hasCorrectToolForDrops(BlockState)` 作原版回退值）→ `PlayerEvent.HarvestCheck`
  事件 → 本模组 `MiningPowerEvents#harvestCheck` 覆写 `setCanHarvest`。因此镐力判定失败的
  `miningpower:unbreakable_if_cannot_harvest` 方块进度归 0，表现为基岩一样挖不开
  （该行为可由 `Config#UNBREAKABLE_IF_CANNOT_HARVEST` 关闭，见
  [mod-config.md](mod-config.md)）。
- `getDestroyProgress` 由客户端挖掘动画每 tick 调用，`HarvestCheck` 随之高频触发；
  `GetCustomDiggingPowerEvent` 订阅方需保持廉价。

## 判定入口签名

| API | 出处（26.1.2 反编译源码） |
|---|---|
| `PlayerEvent.HarvestCheck`（构造参数含初始 `success`） | `net/neoforged/neoforge/event/entity/player/PlayerEvent.java:80,86` |
| `HarvestCheck#getTargetBlock()/canHarvest()/setCanHarvest(boolean)` | 同文件 `:94,106,110` |
| `EventHooks.doPlayerHarvestCheck(Player, BlockState, BlockGetter, BlockPos)` | `net/neoforged/neoforge/event/EventHooks.java:223` |
| `ItemStack#isCorrectToolForDrops(BlockState)` | `net/minecraft/world/item/ItemStack.java:599` |
| `BlockBehaviour$BlockStateBase#requiresCorrectToolForDrops()` | `net/minecraft/world/level/block/state/BlockBehaviour.java:894` |
| `Holder#getData(DataMapType)`（`ItemStack#typeHolder()#getData(...)`） | `net/minecraft/core/Holder.java:251` |
| `DataMapType.builder(Identifier, ResourceKey<Registry<R>>, Codec)` / `Builder#synced(Codec, boolean)` | `net/neoforged/neoforge/registries/datamaps/DataMapType.java:85,152` |
| `ExtraCodecs.POSITIVE_INT` | `net/minecraft/util/ExtraCodecs.java:149` |
| `ToolMaterial`（record，组件 `incorrectBlocksForDrops()`） | `net/minecraft/world/item/ToolMaterial.java:20-21`（常量 `:23-31`） |

旧版文档列的 `TieredItem#getTier()` 在 26.1.2 **已不存在**：`Tier` / `Tiers` / `TieredItem`
被 `ToolMaterial` 取代，物品上不再保留材料实例，只能反查 `DataComponents#TOOL` 里的规则。

## 原版材料 → 镐力映射（沿用 confluence `ModTiers#getPowerForVanillaTiers`）

木 35、石 38、铜 39、金 39、铁 40、钻石 59、下界合金 90；等级阈值
34/46/51/60/71/101/131/191/201 见 `DiggingPower#isCorrectToolForDrops` 与 README 表格。

26.1.2 下材料识别路径：`ItemStack#get(DataComponents.TOOL)` → `Tool#rules()` 里
`correctForDrops == false` 的那条 `Tool.Rule` → 其方块标签（就是
`ToolMaterial#incorrectBlocksForDrops`，由 `ToolMaterial#applyToolProperties` 经
`Tool.Rule.deniesDrops(...)` 写进 `TOOL` 组件）→ 查 `DiggingPower#VANILLA_MATERIAL_POWER`。
映射表见 `src/main/java/io/github/pasze888/miningpower/DiggingPower.java`。
