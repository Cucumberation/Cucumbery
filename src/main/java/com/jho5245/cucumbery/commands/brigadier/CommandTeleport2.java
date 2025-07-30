package com.jho5245.cucumbery.commands.brigadier;

import com.jho5245.cucumbery.commands.brigadier.base.CommandBase;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.LocationArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class CommandTeleport2 extends CommandBase
{
  public void registerCommand(String command, String permission, String... aliases)
  {
    CommandAPICommand commandAPICommand = getCommandBase(command, permission, aliases);
    commandAPICommand = commandAPICommand.withArguments(new LocationArgument("location"));
    commandAPICommand = commandAPICommand.executesPlayer((sender, args) ->
    {
      Location location = (Location) args.get("location");
			if (location != null && !sender.teleport(location))
			{
				throw CommandAPI.failWithString("이동할 수 없습니다");
			}
		});
    commandAPICommand.register();

    commandAPICommand = getCommandBase(command, permission, aliases);
    commandAPICommand = commandAPICommand.withArguments(new MultiLiteralArgument("waypoint", "current", "world-spawn", "respawn", "target-block"));
    commandAPICommand = commandAPICommand.executesPlayer((sender, args) ->
    {
      switch ((String) args.get("waypoint"))
      {
        case "current" -> sender.teleport(sender.getLocation());
        case "world-spawn" -> sender.teleport(sender.getWorld().getSpawnLocation());
        case "respawn" -> {
          Location bed = sender.getRespawnLocation();
          sender.teleport(bed != null ? bed : sender.getLocation());
        }
        case "target-block" -> {
          Block block = sender.getTargetBlockExact(256);
          sender.teleport(block != null ? block.getLocation() : sender.getLocation());
        }
        case null, default -> {
          throw CommandAPI.failWithString("error");
        }
      }
    });
    commandAPICommand.register();
  }
}
