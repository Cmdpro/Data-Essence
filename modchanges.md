### Additions
- Added Entropic Processor recipes for sugar, glowstone block to dust, and quartz block to nether quartz
- Added the "always_shown" field to data tabs if the tab is wanted to be shown even when empty
- Added simple Pastel compat; subject to be expanded and/or changed as that mod grows

### Changes
- Updated Entropic Processor entry with recipe page layout changes and additional descriptions
- Replaced priority in data tabs with a new placement object
- Made data tabs not shown if there is no entries in them
- Moved mod compatibility data entries to a new Mod Integration tab - you will have to relearn such data
- Moved the fabricator logic to a separate abstract class
- Moved the data bank logic to a separate abstract class
- Arekko no longer has collision

### Fixes
- Fixed the Mineral Locator effect glitching out by making its scanning not render fluid states
- Added additional protection from render types other than solid being used to the Mineral Locator effect
- Fixed the recipes for Mekanism integration Chemical Nodes and Wires loading when Mek is not loaded
- Fixed the data tablet entry lines moving the wrong way