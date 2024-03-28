package com.jho5245.cucumbery.listeners.server;

import com.jho5245.cucumbery.Cucumbery;
import com.jho5245.cucumbery.util.no_groups.MessageUtil;
import com.jho5245.cucumbery.util.no_groups.Method;
import com.jho5245.cucumbery.util.storage.component.util.ComponentUtil;
import com.jho5245.cucumbery.util.storage.no_groups.CustomConfig.UserData;
import com.jho5245.cucumbery.util.storage.component.util.sendercomponent.SenderComponentUtil;
import com.jho5245.cucumbery.util.storage.data.Constant;
import com.jho5245.cucumbery.util.storage.data.Prefix;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.CommandBlock;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;

import java.util.List;
import java.util.UUID;

public class ServerCommand implements Listener
{
  @EventHandler
  public void onServerCommand(ServerCommandEvent event)
  {
    String command = event.getCommand();
    // cucumberify
    if (Cucumbery.using_CommandAPI && command.startsWith("give "))
    {
      command = "cgive " + command.substring(5);
      event.setCommand(command);
    }
    // cucumberify
    if (Cucumbery.using_CommandAPI && command.startsWith("/give "))
    {
      command = "/cgive " + command.substring(6);
      event.setCommand(command);
    }
    if (command.contains("--cucumbery"))
    {
      if (event.getSender() instanceof BlockCommandSender blockCommandSender)
      {
        List<String> blackListWorlds = Cucumbery.config.getStringList("disable-command-block-worlds");
        if (Method.configContainsLocation(blockCommandSender.getBlock().getLocation(), blackListWorlds))
        {
          event.setCancelled(true);
          return;
        }
      }

      command = command.replaceFirst("--cucumbery", "");
      command = Method.parseCommandString(Bukkit.getServer().getConsoleSender(), command);
      event.setCommand(command);
    }

    if (!command.contains("--nocolor"))
    {
      command = MessageUtil.n2s(command);
    }
    else
    {
      command = command.replaceFirst("--nocolor", "");
    }
    event.setCommand(command);

    // cucumberify
    if (Cucumbery.using_CommandAPI && command.startsWith("/give"))
    {
      command = "/cgive" + command.substring(5);
      event.setCommand(command);
    }

    boolean debuggerExists = false;
    for (Player player : Bukkit.getOnlinePlayers())
    {
      if (UserData.SHOW_COMMAND_BLOCK_EXECUTION_LOCATION.getBoolean(player))
      {
        debuggerExists = true;
        break;
      }
    }

    if (debuggerExists)
    {
      CommandSender sender = event.getSender();
      if (event.getSender() instanceof BlockCommandSender blockCommandSender)
      {
        if (command.length() > 100)
        {
          command = command.substring(0, 99) + " ...";
        }
        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
          UUID uuid = player.getUniqueId();
          if (UserData.SHOW_COMMAND_BLOCK_EXECUTION_LOCATION.getBoolean(uuid))
          {
            MessageUtil.info(player, "%s(%s) - %s", sender, blockCommandSender.getBlock().getLocation(), command);
          }
        }
      }
    }
  }
}
