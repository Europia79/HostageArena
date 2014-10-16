BattleArena Extraction
======
Bukkit plugin that adds the Extraction game type to
Minecraft servers running BattleArena (dependency).


- **Extraction**
  * this word describes a Special Operations missions where hostages or injured soldiers are rescued.
  * One team guards the hostages while the other team tries to extract (rescue) the hostages.


There are four ways to win:  
1. Eliminate the other team.  
2. Guard the hostages and let the time expire.  
3. Rescue the hostages by bringing them to the extraction point.  
4. Trick the other team into killing the hostages.  


This plugin was inspired by SOCOM US Navy Seals.


Thanks to 
[TeeePeee](http://forums.bukkit.org/threads/nms-tutorial-how-to-override-default-minecraft-mobs.216788/ "nms tutorial - how to override default minecraft mobs") 
& [Jacek](http://forums.bukkit.org/threads/tutorial-how-to-customize-the-behaviour-of-a-mob-or-entity.54547/ "how to customize the behaviour of a mob or entity") 
for such great tutorials on NMS.


Thanks to 
[bigteddy98](https://forums.bukkit.org/threads/tutorial-get-your-nms-obc-working-for-multiple-versions-no-reflection.261614/ "tutorial get your nms obc working for multiple versions") 
& [mbaxter](https://forums.bukkit.org/threads/support-multiple-minecraft-versions-with-abstraction-maven.115810/ "support multiple minecraft versions with maven") 
for such great tutorials on supporting multiple Minecraft versions.


Why HostageArena ?
---
So, why all the extra gamemodes ?


Each game type has it own strategies involved. 
The cool thing about HostageArena is that you can 
try to get the other team to accidentally kill the hostages, 
which will make them lose. So it cuts down on "spray N pray" 
and forces players to actually aim their weapons and be more 
cautious with their grenades.


Additionally, a team that is losing badly with only 1 player left 
cannot simply run and hide to gain a tie because the opposing team 
can just rescue the hostages or guard them to win (depending on what 
side you're on). 


Also, successfully rescuing the hostages is a difficult task. 
And actually accomplishing this goal successfully is an enormous high. 


Obviously, because the Terrorist (guards) can win by simply letting 
time expire, this gamemode should NOT have infinite respawn.


Downloads:
---

**Official builds**

You can find the official builds at dev.bukkit.org .The source code for these builds 
have been checked to make sure that they do NOT contain any malicious code. 

[http://dev.bukkit.org/bukkit-plugins/hostagearena/] (http://dev.bukkit.org/bukkit-plugins/hostagearena/ "Official builds")


**Development builds**

```python
"Development builds of this project can be acquired at the provided continuous integration server."
"These builds have not been approved by the BukkitDev staff. Use them at your own risk."
```

[http://rainbowcraft.sytes.net:8080/job/HostageArena/](http://rainbowcraft.sytes.net:8080/job/HostageArena/ "dev builds")

The dev builds are primarily for testing purposes.


Dependencies:
---

- **Bukkit API**
  * https://github.com/Bukkit/Bukkit
  * Requires Spigot, Craftbukkit, or any other server software that implements the Bukkit API.
- **BattleArena**
  * http://dev.bukkit.org/bukkit-plugins/battlearena/
  * Extraction plugin is just a game-type addition to BattleArena.
  * Required dependency
- **BattleTracker**
  * http://dev.bukkit.org/bukkit-plugins/battletracker/
  * Used to track player stats like `Hostages Rescued` and `Hostages Killed`
  * Optional dependency
- **Holographic Displays**
  * http://dev.bukkit.org/bukkit-plugins/holographic-displays/
  * Adds icons at extraction points
  * Optional dependency
- **HoloAPI**
  * http://dev.bukkit.org/bukkit-plugins/holoapi/
  * Adds icons at extractions points
  * Optional dependency
- **EnjinMinecraftPlugin**
  * http://dev.bukkit.org/bukkit-plugins/emp/
  * Used to track player stats like `Hostages Rescued` and `Hostages Killed`
  * Future optional dependency


Arena Setup:
---

`/vips create ArenaName`  

`/vips alter ArenaName wr 1` (Waiting rooms)  

`/vips alter ArenaName wr 2` (Waiting rooms)  

`/vips alter <arena> 1` (spawn point for team1)  

`/vips setspawn <arena>` (sets the spawn location for hostages)  

If the `/vips setspawn <arena>` command ever breaks, 
(or if you want more fine-grained control over where each hostage spawns), 
then you can use these two commands instead:  

`/aa select <arena>`  

`/aa addspawn VILLAGER 1 fs=0 rs=0 ds=2500 index=1`  

"aa stands for `/arenaAlter`. `fs` stands for First Spawn (normally 0 or 1 seconds after the match beings). 
`rs=0` stands for ReSpawn. NOTE: `rs` MUST BE LESS THAN OR EQUAL TO ZERO. 
`ds=2500` stands for DeSpawn after 2500 seconds (hopefully never). 
The cmd `/vips setspawn` automatically sets the despawn time to the duration of the match.
So if you use `/aa addspawn` then try to set the despawn time greater than or equal to the match duration,  
and set the respawn time to zero. Also, if you use `addspawn` then you can choose 
3 different spawn locations for each VIP.
`setspawn` will spawn all 3 hostages in the same room.

Other commands:  

**/vips join**  

**/vips leave**  

**/vips forcestart**  

**/vips delete ArenaName**  


Extraction Point Setup
---
This plugin needs some kind of way to identify where 
hostages can be safely extracted. 


This is how you make extraction points:  


- do `/vips set|add extractionpoint <arena>`  


- or `vips clear extractionpoints <arena>`


You can now join a HostageArena.  


(Optional) You can add a Worldguard region to BattleArena 
so that block changes reset after each match. (Be careful 
when using LARGE areas, it might lag your server).  

`/region select RegionName`  

`/vips alter <arena> addregion`  

FYI: You do NOT need to use this last command.  
The last command should be only used if you want players to break blocks 
(or access chests) in the arena and have WorldGuard reset all the broken blocks 
to their original state after each match.  


How to access Player Stats Database:
---
```sql
sqlite3 tracker.sqlite
.tables
.schema bt_Extraction_overall
```
output:
```sql
CREATE TABLE bt_Demolition 
  (ID VARCHAR(32) NOT NULL,
  Name VARCHAR(48),
  Wins INTEGER UNSIGNED,
  Losses INTEGER UNSIGNED,
  Ties INTEGER UNSIGNED,
  Streak, INTEGER UNSIGNED,
  maxStreak INTEGER UNSIGNED,
  Elo INTEGER UNSIGNED DEFAULT 1250,
  maxElo INTEGER UNSIGNED DEFAULT 1250,
  Count INTEGER UNSIGNED DEFAULT 1,
  Flags INTEGER UNSIGNED DEFAULT 0,
  PIMRARY KEY (ID));
```
sql command:
```sql
SELECT ID, Wins, Losses FROM bt_Demolition_overall order by Losses desc limit 100;
```
sample output:

| Player | No. of Hostages Extracted | No. of Hostages Killed |
|:-------|:-------------------------:|:----------------------:|
|`Autumn07`     | 1  | 105 |
|`SmileyBrooke` | 3  | 99  |
|`Europia79`    | 32 | 3   |
|`Ralkia`       | 15 | 1   |
|**Totals**     | 51 | 208 |


As you can see, hostages are hard to rescue and easy to kill.


Also notice what the SQL columns mean for the Extraction Game Type:
```sql
  Wins   = # of Hostages Extracted
  Losses = # of Hostages Killed
```


So... you can use BattleTracker to store player stats into either 
an sqlite or MySQL database... You can then have your website access 
the database and print the player stats. Just use the above SQL SELECT 
command: it's the same for both sqlite and MySQL. Then, you can even 
calculate the percentages and display those too if you want.

Also, there's a fake player in the SQL table called 
`Hostages Extracted Killed` that is there by virtue of tracking 
stats that were never meant to be tracked.

  


To-Do List
---
- implement win/loss conditions
- track player stats


Bugs to fix:
---
- none


Known Issues:
---
- Compatible with any version of BattleArena.
- Backwards compatible with older versions of Minecraft (1.6.1 to 1.7.10).
- Minecraft versions 1.5 and older contain an NPC bug where Hostages immediately disappear.
- HostageArena contains version dependent code, therefore it will break and automatically disable itself for new versions of Minecraft.
  

Contact:
======

Nick at Europia79@hotmail.com

Nicodemis79 on Skype


[http://rainbowcraft.net/](http://Rainbowcraft.net/ "Rainbowcraft")


Javadocs & Wiki
---

[http://rainbowcraft.sytes.net:8080/job/HostageArena/](http://rainbowcraft.sytes.net:8080/job/HostageArena/ "javadocs")

[http://wiki.battleplugins.org/Main_Page](http://wiki.battleplugins.org/Main_Page "wiki")
