# NeoForge 1.21.1 配置（ModConfig）验证过的 API 事实

本模组为「镐力不足则无法挖掘」加配置开关时的核对记录。出处：

- 工作区共享 `api-sources/`（Minecraft 1.21.1 + NeoForge 21.1.x 反编译源码，带 Parchment 参数名），
  下方行号均指该目录内文件。
- `fancymodloader loader-4.0.44.jar`（1.21.1 代 FML，Gradle 缓存）——用 `javap` 核对。

> 26.1.2 分支有同名文档，内容框架一致但行号不同（那边对着 NeoForge 26.1.2.99 的源码），别混用。

## 签名

| API | 出处 |
|---|---|
| `net.neoforged.neoforge.common.ModConfigSpec` / `ModConfigSpec$Builder` | `net/neoforged/neoforge/common/ModConfigSpec.java` |
| `Builder#comment(String)` / `comment(String...)` | `ModConfigSpec.java:803,808` |
| `Builder#translation(String)` | `ModConfigSpec.java:820` |
| `Builder#push(String)` | `ModConfigSpec.java:842` |
| `Builder#define(String, boolean)` → `ModConfigSpec$BooleanValue` | `ModConfigSpec.java:733` |
| `Builder#build()` | `ModConfigSpec.java:877` |
| `BooleanValue#getAsBoolean()`（`implements BooleanSupplier`）→ `ConfigValue#get()` | `ModConfigSpec.java:1290`、`:1220` |
| `ConfigValue#getRaw()`（不走缓存，仅编辑值时用） | `ModConfigSpec.java:1235` |
| `ModContainer#registerConfig(ModConfig.Type, IConfigSpec)` / 带文件名重载 | `javap -classpath loader-4.0.44.jar net.neoforged.fml.ModContainer` |
| `ModConfig.Type` = `COMMON` / `CLIENT` / `SERVER` / `STARTUP` | 同上，`net.neoforged.fml.config.ModConfig$Type` |

## 行为

- **`ConfigValue#get()` 自带缓存**：结果存进 `cachedValue`（`ModConfigSpec.java:1192`，`get()` 见
  `:1220`），重复读取只是字段读 + 一次 null 判断，可以放进
  `BlockBehaviour#getDestroyProgress` 这类每 tick 调用的热路径。
- **加载前首次读取会抛异常**：`getRaw()` 里有
  `Preconditions.checkState(loadedConfig != null, "Cannot get config value before config is loaded.")`
  （`ModConfigSpec.java:1235`）。COMMON 配置在 `FMLCommonSetupEvent` 之前读入，世界内的调用踩不到。
- 配置界面取 `ValueSpec#getTranslationKey()`，未设置时回退到分类键 / 路径本身
  （`net/neoforged/neoforge/client/gui/ConfigurationScreen.java:554`）；每个值的提示键是
  `<翻译键>.tooltip`（同文件 `:574`），提示内容取该值的注释（`:579`）。

## 本模组用法

`Config#UNBREAKABLE_IF_CANNOT_HARVEST`（键 `unbreakableIfCannotHarvest`，默认 `true`）由
`mixin/BlockBehaviourMixin` 读取，控制「镐力不足则无法挖掘」这一行为。COMMON 配置客户端与服务端
各自读本地文件、**不跨网络同步**，两端取值不一致时表现会不一致（客户端进度归 0 即表现为挖不动），
专用服务器需自行保持两端一致。
