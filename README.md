# Goety Log Suppressor

NeoForge 1.21.1 dedicated-server-only mod that filters four specific Minecraft log message shapes at the Log4j root logger.

The filter is **not Goety-specific**. It ignores the mod namespace/resource ID, and it ignores the player name/ID in statistic JSON paths.

## Suppressed message shapes

1. `Encountered unknown or non-serializable data attachment <namespace:path>. Skipping.`
2. `Skipping BlockEntity with id <namespace:path>`
3. `Ignoring unknown attribute '<namespace:path>'`
4. `Invalid statistic in ...\\stats\\<any-player>.json: Don't know what <namespace:path> is`

Both `/` and `\\` are accepted in the statistic path.

Examples such as `goety:misc`, `goety:pedestal`, `goety:abyss_potency`, and `goety:web_spider` are therefore only examples; the filter works with other namespaces and IDs too.

## Scope

Only these exact message structures are denied. Other WARN/ERROR messages remain untouched.

Because the filter is added to the Log4j root logger, denied events are prevented from reaching the normal console and file appenders, so they do not keep filling `latest.log`.

## Build

Requires Java 21 and NeoForge 1.21.1 development environment.

```bash
gradle build
```

The resulting JAR is in `build/libs/`.
