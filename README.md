![grafik](https://github.com/user-attachments/assets/409097c5-5459-4454-bf92-32a6bf1d01f0)




VanillaPNX is a vanilla world generator for PowerNukkitX.
It uses a Paper server to generate the world, which then will be sent to the PNX server, where it is assembled again.

This plugin was heavily optimized, but good hardware is still a must have for this. 

This plugin is also still in beta. Not everything works yet.

- config.json to use

```json
{
  "format": "leveldb",
  "enable": true,
  "generators": {
    "0": {
      "name": "vanilla",
      "seed": 0,
      "enableAntiXray": false,
      "antiXrayMode": "LOW",
      "preDeobfuscate": true,
      "dimensionData": {
        "dimensionName": "minecraft:overworld",
        "dimensionId": 0,
        "minHeight": -64,
        "maxHeight": 319,
        "height": 384,
        "chunkSectionCount": 24
      },
      "preset": {}
    },
    "1": {
      "name": "vanilla",
      "seed": 0,
      "enableAntiXray": false,
      "antiXrayMode": "LOW",
      "preDeobfuscate": true,
      "dimensionData": {
        "dimensionName": "minecraft:nether",
        "dimensionId": 1,
        "minHeight": 0,
        "maxHeight": 127,
        "height": 128,
        "chunkSectionCount": 8
      },
      "preset": {}
    },
    "2": {
      "name": "vanilla",
      "seed": 0,
      "enableAntiXray": false,
      "antiXrayMode": "LOW",
      "preDeobfuscate": true,
      "dimensionData": {
        "dimensionName": "minecraft:end",
        "dimensionId": 2,
        "minHeight": 0,
        "maxHeight": 255,
        "height": 256,
        "chunkSectionCount": 16
      },
      "preset": {}
    }
  }
}
```
