# Mining Power

[English](README.md) | [简体中文](README.zh-CN.md)

A Terraria-style mining-level system for NeoForge 1.21.1, extracted from the
[Confluence](https://github.com/Magic-team-jvav/confluence) mod (LGPL-3.0-or-later).

Blocks are gated by nine discrete levels (`miningpower:needs_1_level` … `needs_9_level`
block tags, downward-inclusive). Tools carry a continuous numeric **digging power**
(镐力); the power value decides the highest block level a tool can harvest. With
insufficient power, high-level blocks simply cannot be broken — like bedrock (see
[Configuration](#configuration) to turn that off).

## Mechanics

| Digging power | Level unlocked |
|---|---|
| >= 201 | 9 (mines everything) |
| >= 191 | 8 |
| >= 131 | 7 |
| >= 101 | 6 |
| >= 71 | 5 |
| >= 60 | 4 |
| >= 51 | 3 |
| >= 46 | 2 |
| >= 34 | 1 |

Vanilla tiers map to: wood 35, stone 38, gold 39, iron 40, diamond 59, netherite 90.
By default obsidian, crying obsidian, ancient debris and blocks of netherite sit at
level 3, so diamond (and above) still works while iron no longer harvests them.
Levels 4–9 ship empty — they are hooks for datapacks and other mods.

Only items in `minecraft:pickaxes` (main hand) go through the power check.

## Extending

- **Block level**: add blocks to `miningpower:needs_x_level` tags from any datapack.
- **Item power**: assign the `miningpower:digging_power` data map (positive int) to any
  item via datapack, or subscribe to `GetCustomDiggingPowerEvent` on the NeoForge event
  bus for dynamic power.
- **Blocks that cannot be mined**: blocks in `miningpower:unbreakable_if_cannot_harvest`
  become unbreakable (destroy progress forced to 0) when the harvest check fails; the tag
  covers `needs_2..9` by default. Toggle it with the `unbreakableIfCannotHarvest`
  config option.

## Configuration

Config file: `config/miningpower-common.toml` (COMMON type — the client and the server
each read their own local file, and it is **not** synced over the network; keep both
sides consistent on a dedicated server).

| Key | Default | Description |
|---|---|---|
| `unbreakableIfCannotHarvest` | `true` | Force the destroy progress of blocks in `miningpower:unbreakable_if_cannot_harvest` to 0 when the pickaxe power is insufficient (harvest check fails). When `false`, they fall back to vanilla wrong-tool behaviour: slow to break and dropping nothing. |

## Commands

```
./gradlew build          # compile + jar
./gradlew runData        # regenerate block tags into src/generated/resources
./gradlew runClient      # dev client
```

## License & attribution

LGPL-3.0-or-later. Core logic (`DiggingPower`, harvest check, destroy-progress mixin)
is adapted from the Confluence mod's `org.confluence.mod.common.init.ModTiers`,
`common.data.map.DiggingPower` and `mixin.block.BlockBehaviourMixin`.
