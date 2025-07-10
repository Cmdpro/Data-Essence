## Music Update - Song of the Factory
### Additions
- Added the Song of the Factory - major machines now play synchronized melodies when making or using Essence; build up your factory and watch the Song grow with it
- Added synchronized music system
- Added Entropic Processor recipes for sugar, glowstone block to dust, and quartz block to nether quartz
- Added the "always_shown" field to data tabs if the tab is wanted to be shown even when empty
- Added simple Pastel compat; subject to be expanded and/or changed as that mod grows
- Added new variant of Ancient Rock blocks to prevent using the old, now structure-only Ancient Rock as a cheese method for other mods
- Added sound for deactivating (breaking) a Structure Protector

### Changes
- Updated Entropic Processor entry with recipe page layout changes and additional descriptions
- Replaced priority in data tabs with a new placement object
- Made data tabs not shown if there is no entries in them
- Moved mod compatibility data entries to a new Mod Integration tab - you will have to relearn such data
- Moved the fabricator logic to a separate abstract class
- Moved the data bank logic to a separate abstract class
- Arekko no longer has collision
- Changed Fabricator finish crafting noise to amethyst breaking
- Adjusted several fabrication recipe times
- Ancient Rock renamed to Shielding Rock
- Shielding Rock now drops non-unbreakable Ancient Rock no matter what
- Shielding Rock can no longer be relocated with other mods
- Hid the names of Industrial Essence Crystals/Shards, Lensing Crystals, and Bonding Powder before you have progressed

### Fixes
- Fixed the Mineral Locator effect glitching out by making its scanning not render fluid states
- Added additional protection from render types other than solid being used to the Mineral Locator effect
- Fixed the recipes for Mekanism integration Chemical Nodes and Wires loading when Mek is not loaded
- Fixed the data tablet entry lines moving the wrong way