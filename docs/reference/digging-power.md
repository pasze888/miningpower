# 镐力（digging power）系统验证过的 API 事实

以下签名与行为均在 1.21.1 / NeoForge 21.1.250 对照 api-sources 反编译源码核实。

## `BlockBehaviour#getDestroyProgress` 方法体与局部变量 `i`

`net/minecraft/world/level/block/state/BlockBehaviour.java:393-401`（NeoForge 补丁版）：

```java
protected float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
    float f = state.getDestroySpeed(level, pos);
    if (f == -1.0F) {
        return 0.0F;
    } else {
        int i = net.neoforged.neoforge.event.EventHooks.doPlayerHarvestCheck(player, state, level, pos) ? 30 : 100;
        return player.getDigSpeed(state, pos) / f / (float)i;
    }
}
```

- 局部变量 `i` 是**收割判定惩罚值**（可收 30 / 不可收 100），不是计时器。
  `mixin/BlockBehaviourMixin#miningpower$deny` 的 `@Local int i` 条件 `i > 30` 语义即
  「收割判定失败」。confluence 上游同款 Mixin 也依赖这一事实（其注释易被误读为
  「挖掘超过 30 tick」）。
- 判定链：`doPlayerHarvestCheck` → `Player#canHarvest` → `PlayerEvent.HarvestCheck` 事件
  → 本模组 `MiningPowerEvents#harvestCheck` 覆写 `setCanHarvest`。因此镐力判定失败的
  `#miningpower:unbreakable_if_cannot_harvest` 方块进度归 0，表现为基岩一样挖不开
  （该行为可由 `Config#UNBREAKABLE_IF_CANNOT_HARVEST` 关闭，见 [mod-config.md](mod-config.md)）。
- `getDestroyProgress` 由客户端挖掘动画每 tick 调用，`HarvestCheck` 随之高频触发；
  `GetCustomDiggingPowerEvent` 订阅方需保持廉价。

## 判定入口签名

| API | 出处（api-sources） |
|---|---|
| `PlayerEvent.HarvestCheck#getTargetBlock()/canHarvest()/setCanHarvest(boolean)` | `net/neoforged/neoforge/event/entity/player/PlayerEvent.java:65,79,91,95` |
| `ItemStack#isCorrectToolForDrops(BlockState)` | `net/minecraft/world/item/ItemStack.java:562` |
| `BlockBehaviour$BlockStateBase#requiresCorrectToolForDrops()` | `net/minecraft/world/level/block/state/BlockBehaviour.java:958` |
| `Holder<Item>#getData(DataMapType)`（`ItemStack#getItemHolder().getData(...)`） | `net/minecraft/core/Holder.java:224` |
| `DataMapType.builder(...)` / `Builder#synced(Codec, boolean)` | `net/neoforged/neoforge/registries/datamaps/DataMapType.java:85,152` |
| `ExtraCodecs.POSITIVE_INT` | `net/minecraft/util/ExtraCodecs.java:126` |
| `TieredItem#getTier()` | `net/minecraft/world/item/TieredItem.java:11` |

## 原版 Tier → 镐力映射（沿用 confluence `ModTiers#getPowerForVanillaTiers`）

木 35、石 38、金 39、铁 40、钻石 59、下界合金 90。等级阈值 34/46/51/60/71/101/131/191/201
见 `DiggingPower#isCorrectToolForDrops` 与 README 表格。
