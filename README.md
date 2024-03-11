[![build](https://github.com/Mrbysco/TimeStages/actions/workflows/build.yml/badge.svg)](https://github.com/Mrbysco/TimeStages/actions/workflows/build.yml) [![](http://cf.way2muchnoise.eu/versions/285375.svg)](https://www.curseforge.com/minecraft/mc-mods/time-stages)

# Time Stages #

## About ##
Allows stages to be unlocked/locked with a timer.

## License ##
* Time Stages is licensed under the MIT License
  - (c) 2020 Mrbysco
  - [![License](https://img.shields.io/badge/License-MIT-red.svg?style=flat)](http://opensource.org/licenses/MIT)

## Downloads ##
Downloads will be located on [CurseForge](https://www.curseforge.com/minecraft/mc-mods/time-stages)

## Example

```
// Makes you gain a stage 30 seconds after getting the needed stage.
mods.timestages.Timers.addTimer("UniqueID", "neededStage" ,"UnlockedStage" ,30 ,"seconds");

// Makes you gain a stage 30 seconds after getting the needed stage and remove the original required stage.
mods.timestages.Timers.addTimer("UniqueID", "neededStage" ,"UnlockedStage" ,30 ,"seconds", true);

// Removes a stage after 20 minutes.
mods.timestages.Timers.removalTimer("UniqueID", "removedStage" ,20 ,"minutes");

// Removes a stage after 20 minutes without putting a message in chat.
mods.timestages.Timers.silentRemovalTimer("UniqueID", "removedStage" ,20 ,"minutes");

// Makes you gain a stage 30 seconds after getting the needed stage without putting a message in chat.
mods.timestages.Timers.addSilentTimer("UniqueID", "neededStage" ,"UnlockedStage" ,30 ,"seconds");

// Makes you gain a stage 30 seconds after getting the needed stage and remove the original required stage without putting a message in chat.
mods.timestages.Timers.addSilentTimer("UniqueID", "neededStage" ,"UnlockedStage" ,30 ,"seconds", true);
```
