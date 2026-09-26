# NeoForge 26.1.2 配置（ModConfig）验证过的 API 事实

本模组 26.1.2 分支引入配置系统时的核对记录。出处：

- `neoforge-26.1.2.99-sources.jar`——`~/.gradle/caches/modules-2/files-2.1/net.neoforged/neoforge/26.1.2.99/`，
  下方 `ModConfigSpec.java` 行号均指该 sources jar 内的
  `net/neoforged/neoforge/common/ModConfigSpec.java`。
- `fancymodloader loader-11.0.15.jar`——NeoForge 26.1.2.99 的 module 元数据
  （`neoforge-26.1.2.99.module`，`modDevApiElements` 变体）指定的 FML 版本，用 `javap` 核对。
- 官方文档：`Documentation/docs/misc/config.md`。

## 签名

| API | 出处 |
|---|---|
| `net.neoforged.neoforge.common.ModConfigSpec` / `ModConfigSpec$Builder` | 包名与 1.21.1 相同，未随 26.1.2 改名 |
| `Builder#comment(String...)` / `Builder#translation(String)` | `ModConfigSpec.java:829`（translation） |
| `Builder#define(String, boolean)` → `ModConfigSpec$BooleanValue` | `ModConfigSpec.java:742` |
| `Builder#define` / `defineInRange` / `defineEnum` / `defineList` / `defineListAllowEmpty` | 同文件 `ModConfigSpec$Builder` 内 |
| `Builder#push/pop`（分类）、`Builder#build()` | `ModConfigSpec.java:886`（build） |
| `BooleanValue#getAsBoolean()`（`implements BooleanSupplier`）→ `ConfigValue#get()` | `ModConfigSpec.java:1299`；`get()` 见 `1229` |
| `ConfigValue#getRaw()`（不走缓存，仅编辑值时用） | `ModConfigSpec.java:1241` |
| `ModContainer#registerConfig(ModConfig.Type, IConfigSpec)` / 带文件名重载 | `javap -classpath loader-11.0.15.jar net.neoforged.fml.ModContainer` |
| `ModConfig.Type` = `COMMON` / `CLIENT` / `SERVER` / `STARTUP` | `javap -classpath loader-11.0.15.jar 'net.neoforged.fml.config.ModConfig$Type'` |

## 行为

- **`ConfigValue#get()` 自带缓存**：结果存进 `cachedValue`（`ModConfigSpec.java:1201`），
  重复读取只是字段读 + 一次 null 判断（`1229-1234`），可以放进
  `BlockBehaviour#getDestroyProgress` 这类每 tick 调用的热路径。
- **加载前首次读取会抛异常**：`getRaw()` 里有
  `Preconditions.checkState(loadedConfig != null, "Cannot get config value before config is loaded.")`
  （`ModConfigSpec.java:1241-1246`）。COMMON 配置在 `FMLCommonSetupEvent` 之前读入，
  世界内的调用踩不到；若要在更早的阶段读取，务必先订阅 `ModConfigEvent.Loading`。
- `Builder#translation(String)` 只对**紧接着定义**的那个值生效，或对随后 `push` 出来的分类生效
  （`ModConfigSpec.java:829-832,861-864`）。未设置翻译键时，配置界面回退到分类键 / 路径本身
  （`net/neoforged/neoforge/client/gui/ConfigurationScreen.java:543`）；每个值的提示键是
  `<翻译键>.tooltip`（同文件 `:563`）。
- 配置类型语义（官方文档 `misc/config.md`）：`COMMON` 客户端与服务端各自读本地 `config/` 文件，
  **不跨网络同步**；`SERVER` 会同步给客户端，但文件按存档 / 世界存放。本模组用 `COMMON`。

## 本模组用法

`Config#UNBREAKABLE_IF_CANNOT_HARVEST`（键 `unbreakableIfCannotHarvest`，默认 `true`）由
`mixin/BlockBehaviourMixin` 读取，控制「镐力不足则无法挖掘」这一行为。因为 `getDestroyProgress`
客户端与服务端都会调用，而 COMMON 配置不同步，两端取值不一致时表现会不一致
（客户端进度归 0 即表现为挖不动），专用服务器需自行保持两端一致。
