CODING TEST : MONSTER-HUNT (JAVA BASED TEXT GAME)
==============================================

## Author

**Sachin Sachdeva** - *ssachdev* - [ShowStopper3](https://github.com/ShowStopper3)

Description
-------------
Monster-Hunt is a Java (text) based implementation of a game, having following stories:

● As a player You can to create a character

● As a player You can to explore

● As a player You can to gain experience through fighting

● As a player You can to save and resume a game

This game is in its initial state where player can walk 
through the game, find items and fight with some _nice_ Monsters! and save and resume the game at any point.

Story of the game.
-------------

You have a player, monster and to make game more interesting I have used Non Performing Character NPC.

These NPC's are present at different locations with different roles at each location for example at certain location they can act as guide and at other location they can fight with you.

At each level you will find items which you have a choice to  pick up. These items can be milk to boost your energy level or weapons to with with monsters.When you move on with game you start fighting with characters and monster to earn XP's, when you earn good amount of XP's you will move on to next level.

Algorithm of the game.
-------------

**How to create a new player**
* On creation of new player object, we initialize different type of attributes associated with it for example type of NPC, location associated with it and other etc .

**How to move a forward**
* When player moves on by entering direction we add the direction co-ordinates with existing co-ordinates and find the new co-ordinates. 
      

**How to get the commands from user**
* I have created my own command annotation to make sure all the new commands added follows the same pattern.Now when user enters a new commands we call that command from services via reflection framework, Inside that command we have business logic that takes the game forward. 

**How to check if the place is safe or it has a monster**
* Every Location object has a danger level associated with it, when player enters in that place it checks the danger level if it breaches the threshold value then it has monster else it is safe to go on.


**How user maintains the list of item he picked or dropped**
* I am using stack for that. 


Playing the Game
--------------

To start a new game:

    start

To save a game:

    s

Get a list of commands with:

    h

To get a list of monsters around you:

    m

To view details about your player:

    v <s,e,b> - view status, equipped items, backpack

To quit the game:

    exit

To move:

    g n - go north
    g s - go south
    g e - go east
    g w - go west

To pick up an item:

    p <itemName>

To drop an item:

    d <itemName>

To equip/unequip item:

    e <itemName>
    ue <itemName>

To attack:
    
    a <monster>

To look around:
 
    la

To talk to a Non-player Character:

    t <npc>

