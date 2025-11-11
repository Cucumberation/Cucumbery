package com.jho5245.cucumbery.commands.brigadier;

import com.jho5245.cucumbery.commands.brigadier.base.CommandBase;
import de.tr7zw.changeme.nbtapi.NBTContainer;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.entity.Entity;

import java.util.Collection;

import static com.jho5245.cucumbery.commands.brigadier.base.ArgumentUtil.MANY_ENTITIES;

public class CommandData2 extends CommandBase
{
  @SuppressWarnings("unchecked")
  public void registerCommand(String command, String permission, String... aliases)
  {
    CommandAPICommand commandAPICommand = getCommandBase(command, permission, aliases);
    commandAPICommand = commandAPICommand.withArguments(MANY_ENTITIES, new StringArgument("nbt"));
    commandAPICommand = commandAPICommand.executesNative((sender, args) ->
            {
              Collection<Entity> entities = (Collection<Entity>) args.get(0);
              String nbtContainer = (String) args.get("nbt");
              for (Entity entity : entities)
              {
                NBTEntity nbtEntity = new NBTEntity(entity);
                nbtEntity.mergeCompound(new NBTContainer(nbtContainer));
              }
            }
    );
    commandAPICommand.register();
  }
}
