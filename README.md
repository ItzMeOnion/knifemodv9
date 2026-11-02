# CS2 Knives (client-side Fabric mod, Minecraft 1.21.8)

Client-only. Nothing is sent to servers, and other players will not see your knives.

## Controls
- **K**: open the menu (only when no other screen is open)

## Features
- **Inventory**: cards for every knife you own, with copy count and grade. Click a card to inspect it.
- **Inspect**: large turning knife, wear name, float, pattern seed, obtained date. Cycle copies with the Copy buttons.
- **Open Cases**: Gamma Case (all 10 knives) and Shadow Case (5 cheaper knives). Press Open for a roulette spin; the knife is added to your inventory.
- **Grant one of every knife**: starter button from the menu.

Your inventory is saved to `config/cs2knives/inventory.json`.

## Build
1. Check the versions in `gradle.properties` against https://fabricmc.net/develop (loader, Fabric API, Loom).
2. `./gradlew build`
3. Put `build/libs/cs2knives-1.0.0.jar` in `mods/` with Fabric Loader and Fabric API for 1.21.8.

## Notes
- Icons are the 64x64 slot images from the CS2 knife pack (`textures/melee/slot/*.png`), copied to `assets/cs2knives/textures/gui/knife/`.
- The "spin" in Inspect is a 2D width-squash illusion, not a 3D model.
- Knives are not rendered in your hand. The pack's 3D models use GeckoLib/TACZ formats that this mod doesn't load.
