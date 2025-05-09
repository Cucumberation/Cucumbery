package com.jho5245.cucumbery.util.storage.no_groups;

import com.jho5245.cucumbery.Cucumbery;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.*;

@SuppressWarnings("all")
public class PluginLoader
{

	public static void unload()
	{
		Plugin plugin = Cucumbery.getPlugin();
		// May not be compatible with Spigot
		String name = Cucumbery.isPaper ? plugin.getPluginMeta().getName().toLowerCase(Locale.ENGLISH) : plugin.getName();
		// commandwrap
		PluginManager pluginManager = Bukkit.getPluginManager();
		SimpleCommandMap commandMap = null;
		List<Plugin> plugins = null;
		Map<String, Plugin> names = null;
		Map<String, Command> commands = null;
		Map<Event, SortedSet<RegisteredListener>> listeners = null;
		boolean reloadlisteners = true;
		if (pluginManager != null)
		{
			pluginManager.disablePlugin(plugin);
			try
			{
				// For Paper, PaperPluginManagerImpl implements PluginManager
				// todo inject listeners (perhaps PaperEventManager does the thing)
				Field paperPluginManagerField = Bukkit.getPluginManager().getClass().getDeclaredField("paperPluginManager");
				// io.papermc.paper.plugin.manager.PaperPluginManagerImpl
				Object pluginManagerImpl = paperPluginManagerField.get(Bukkit.getPluginManager());
				Field instanceManagerField = pluginManagerImpl.getClass().getDeclaredField("instanceManager");
				instanceManagerField.setAccessible(true);

				Object instanceManager = instanceManagerField.get(pluginManagerImpl);
				// io.papermc.paper.plugin.manager.PaperPluginInstanceManager
				Class<?> instanceManagerClass = instanceManager.getClass();
				Field pluginsField = instanceManagerClass.getDeclaredField("plugins");
				Field lookupNamesField = instanceManagerClass.getDeclaredField("lookupNames");
				Field commandMapField = instanceManagerClass.getDeclaredField("commandMap");
				Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
				paperPluginManagerField.setAccessible(true);
				pluginsField.setAccessible(true);
				lookupNamesField.setAccessible(true);
				commandMapField.setAccessible(true);
				knownCommandsField.setAccessible(true);
				plugins = (List<Plugin>) pluginsField.get(instanceManager);
				names = (Map<String, Plugin>) lookupNamesField.get(instanceManager);
				commandMap = (SimpleCommandMap) commandMapField.get(instanceManager);
				commands = (Map<String, Command>) knownCommandsField.get(commandMap);

				// Detaching plugin from entrypoint storage
				Class<?> handlerClass = Class.forName("io.papermc.paper.plugin.entrypoint.LaunchEntryPointHandler");
				Class<?> entrypointClass = Class.forName("io.papermc.paper.plugin.entrypoint.Entrypoint");
				Class<?> providerStorageClass = Class.forName("io.papermc.paper.plugin.storage.SimpleProviderStorage");
				Class<?> pluginProviderClass = Class.forName("io.papermc.paper.plugin.provider.PluginProvider");
				Object handler = handlerClass.getDeclaredField("INSTANCE").get(null);
				Object entrypoint = entrypointClass.getDeclaredField("PLUGIN").get(null);
				Object providerStorage = handlerClass.getMethod("get", entrypointClass).invoke(handler, entrypoint);
				Iterable<?> iterable = (Iterable<?>) providerStorageClass.getMethod("getRegisteredProviders").invoke(providerStorage);
				Iterator<?> iter = iterable.iterator();
				String pluginName = Cucumbery.getPlugin().getPluginMeta().getName();

				while (iter.hasNext())
				{
					Object pluginProvider = iter.next();
					PluginMeta pluginMeta = (PluginMeta) pluginProviderClass.getMethod("getMeta").invoke(pluginProvider);
					if (pluginMeta.getName().equals(pluginName))
					{
						iter.remove();
						break;
					}
				}
			}
			catch (Exception e)
			{
				Cucumbery.getPlugin().getLogger().warning(e.getMessage());
			}
		}

		if (listeners != null && reloadlisteners)
		{
			for (SortedSet<RegisteredListener> set : listeners.values())
			{
				set.removeIf(value -> value.getPlugin() == plugin);
			}
		}

		if (commandMap != null)
		{
			for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry<String, Command> entry = it.next();
				if (entry.getValue() instanceof PluginCommand)
				{
					PluginCommand c = (PluginCommand) entry.getValue();
					if (c.getPlugin() == plugin)
					{
						c.unregister(commandMap);
						// TODO: Fix it later
						try
						{
							it.remove();
						}
						catch (Exception ignored)
						{

						}
					}
				}
				else
				{
					try
					{
						Field pluginField = Arrays.stream(entry.getValue().getClass().getDeclaredFields()).filter(field -> Plugin.class.isAssignableFrom(field.getType()))
								.findFirst().orElse(null);
						if (pluginField != null)
						{
							Plugin owningPlugin;
							try
							{
								pluginField.setAccessible(true);
								owningPlugin = (Plugin) pluginField.get(entry.getValue());
								if (owningPlugin.getName().equalsIgnoreCase(plugin.getName()))
								{
									entry.getValue().unregister(commandMap);
									it.remove();
								}
							}
							catch (Exception e)
							{
								Cucumbery.getPlugin().getLogger().warning(e.getMessage());
							}
						}
					}
					catch (Exception e)
					{
						Cucumbery.getPlugin().getLogger().warning(e.getMessage());
						if (e.getMessage().equalsIgnoreCase("zip file closed"))
						{
							entry.getValue().unregister(commandMap);
							it.remove();
						}
					}
				}
			}
		}

		if (plugins != null && plugins.contains(plugin))
		{
			plugins.remove(plugin);
		}
		if (names != null && names.containsKey(name))
		{
			names.remove(name);
		}
		// Closing the plugin classloader
		ClassLoader cl = plugin.getClass().getClassLoader();
		if (cl instanceof URLClassLoader)
		{
			try
			{
				Field pluginField = cl.getClass().getDeclaredField("plugin");
				pluginField.setAccessible(true);
				pluginField.set(cl, null);
				Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
				pluginInitField.setAccessible(true);
				pluginInitField.set(cl, null);
			}
			catch (Exception ex)
			{
				Cucumbery.getPlugin().getLogger().warning(ex.getMessage());
			}
			try
			{
				((URLClassLoader) cl).close();
			}
			catch (Exception ex)
			{
				Cucumbery.getPlugin().getLogger().warning(ex.getMessage());
			}
		}
		System.gc();
	}

	// For Paper, bootstrapper will inhibit plugin from loading
	// For Paper, paper-plugin.yml will inhibit plugin from loading at runtime
	public static void load(File file)
	{
		Plugin plugin = null;
		if (!file.isFile())
		{
			return;
		}

		try
		{
			file.setReadable(true);
			plugin = Bukkit.getPluginManager().loadPlugin(file);
		}
		catch (Exception e)
		{
			Cucumbery.getPlugin().getLogger().warning(e.getMessage());
		}
		if (plugin == null)
		{
			return;
		}
		plugin.onLoad();
		Bukkit.getPluginManager().enablePlugin(plugin);
	}
}