# WarhammerCore
This repository is the code for the core to WarhammerUHCF. The code is licensed for use only on this network and should not be viewed or used outside of it in anyway without permission by the applicable entities.

# For Admins/Config
This section will come soon.

# For Developers

**Using ItemBuilder**

    ItemBuilder.createItem(Material, Name, Amount, Data, Lore);
    ItemBuilder.createItem(Material.STAINED_GLASS, "&cHello!", 1, 15, "&7This is black glass!");

**Using the Custom Item API**

 1. Create your CustomItem class and create the constructor.
```java
public class TestItem extends CustomItem {
	public TestItem() {
		this.name = "internal_name";
		this.item = ItemBuilder.createItem(Material.DIRT, "Fancy Dirt");	
	}
}
```
2. Override the right/left click or inventory click methods to do whatever you need.
```java
public class TestItem extends CustomItem {
	public TestItem() {
		this.name = "internal_name";
		this.item = ItemBuilder.createItem(Material.DIRT, "Fancy Dirt");	
	}

	@Override
	public void onClick(PlayerInteractEvent e) {
		removeItem(e.getItem());
		e.getPlayer().sendMessage("You used fancy dirt!");
	}

	@Override
	public void onInventoryClick(InventoryClickEvent e, Player p) {
		clear(e);
		p.sendMessage("You clicked fancy dirt!");
	}
}
```
3. Register your custom item.
```java
WarhammerCore.get().getItemManager().registerItem(new TestItem());
```

**Using CustomCommands and StructuredCommands**
These custom command types are useful for easier command methods, they do **not** need to be registered in the plugin.yml.
*Custom Commands* are for simple commands that require no excess arguments.
*Structured Commands* are for commands that require many sub arguments.

_CustomCommands_
```java
public class TestCommand extends CustomCommand {
	public TestCommand() {
		super(commandString, requiresOp, requiresPerm, permissionNode);
	}

	@Override
	public void execute(Player player, List<String> args) {
		// do your thing.
	}

	@Override
	public void onFail(Player player, List<String> argsUsed) {
		player.sendMessage(getFailMessage()); //will send player default noperm message.
	}
}
```
```java
P.p.getCommandManager().registerCommand(new TestCommand());
```

*StructuredCommands*
Structured Commands create easy in-game help messages and info without having to do any work.

```java
public class TestCommand extends StructuredCommand {
	public TestCommand() {
		super(commandString, commandDescription, needsOp, needsPerm, permissionNode, aliasesArray, 
			new SubCommand(subcommandString, subcommandDescription, needsPerm, permNode, excessArgs)
		);
	}
}
```
Example super:
```java
public class CmdGivePaper extends StructuredCommand {
	public CmdGivePaper() {
		super("givepaper", "gives players paper", true, false, null, new String[]{"givep", "gp"}, 
			new SubCommand("give", "actually gives the paper", false, null, "<player> <amount>")
		);
	}
}
```
Executing the command:
```java
@Override
public void execute(Player player, SubCommand subCommand, List<String> args, Plugin plugin2) {
	String sub = subCommand.getSubCommand();
	P plugin = (P) plugin2;
	if(sub.equalsIgnoreCase("give")) {
		try {
			(Bukkit.getPlayer(args.get(0))).getInventory().addItem(new ItemStack(Material.PAPER, Integer.parseInt(args.get(1))));
		} catch(Exception e) { // catches all errors with commands and sends a message to the user explaining how to use the command properly, prints error to console ONLY if the error is not user-based.
			sendFailedSubCommand(player, subCommand, e);
		}
	}
}

@Override
public void executeNoArgs(Player player, Plugin plugin) {
	sendNoArgMessage(player); // sends base help command if not entered any args
}

@Override
public void fail(Player player, List<String> args, Plugin plugin) {
	player.sendMessage(getFailMessage()); // sends fail message
}
```
```java
P.p.getCommandManager().registerCommand(new TestCommand());
```



**Give Utils**
```java
GiveUtil.giveOrDropItem(player, item);
//will either give the item to the player or drop it on the floor protected if the user's inventory is full
//also sends message and plays sound already.
```

**Cooldowns**
Setting a cooldown:
```java
WarhammerCore.get().getCooldownManager().setUserCooldown(uuid, cooldownID, timeUnit, units);
WarhammerCore.get().getCooldownManager().setUserCooldown(player.getUniqueId(), "test-cooldown", TimeUnit.MINUTES, 1);
```
Checking if user is on cooldown:
```java
WarhammerCore.get().getCooldownManager().isOnCooldown(uuid, cooldownID);
```
Getting raw cooldown long:
```java
WarhammerCore.get().getCooldownManager().getUserCooldown(uuid, cooldownID);
```
Getting a formatted time-left string:
```java
WarhammerCore.get().getCooldownManager().getCooldownFormatted(uuid, cooldownID);
```

**Quick Color Code**
```java
c.c(string);
```

**PatchAPI**
The PatchAPI is a modular plugin system that allows features to be enabled/disabled in realtime in game. Think of it as plugins within your plugin.

Creating your Main class:
```java
public class TestPatch extends UHCFPatch {
	public TestPatch(Plugin p) {
		super(p);
	}

	public void enable() {}
	public void disable() {}
}
```
Registering your patch:
```java
WarhammerCore.get().registerPatches(new TestPatch());
```
Registering things inside your patch:
```java
@Override
public void enable() {
	registerListener(new TestListener());
	registerCommand(new CustomCommand());
	registerCommand(new StructuredCommand());
	registerTask(new TestTask());
}
```
