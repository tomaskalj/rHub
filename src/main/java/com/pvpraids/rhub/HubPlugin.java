package com.pvpraids.rhub;

import com.pvpraids.rhub.listener.EntityListener;
import com.pvpraids.rhub.listener.PlayerListener;
import com.pvpraids.rhub.listener.WorldListener;
import com.pvpraids.rhub.manager.InventoryListener;
import com.pvpraids.rhub.manager.MenuManager;
import com.pvpraids.rhub.manager.QueueManager;
import com.pvpraids.rhub.tasks.PlayerCountUpdateTask;
import com.pvpraids.rhub.tasks.QueueTask;
import com.pvpraids.scoreboardapi.api.CustomScoreboard;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

@Getter
public class HubPlugin extends JavaPlugin {
	private final Map<String, Integer> serverPlayerCounts = new HashMap<>();
	private BungeeChannelApi bungeeChannelApi;

	private MenuManager menuManager;
	private QueueManager queueManager;

	@Override
	public void onEnable() {
		bungeeChannelApi = BungeeChannelApi.of(this);

		menuManager = new MenuManager(this);
		queueManager = new QueueManager(this);

		registerListeners(
				new PlayerListener(this),
				new InventoryListener(this),
				new EntityListener(),
				new WorldListener()
		);

		new CustomScoreboard(this, 20);

		new QueueTask(this).runTaskTimer(this, 20, 20);

		BukkitScheduler scheduler = getServer().getScheduler();

		scheduler.runTaskTimerAsynchronously(this, new PlayerCountUpdateTask(this), 1L, 20 * 3L);
	}

	private void registerListeners(Listener... listeners) {
		for (Listener listener : listeners) {
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	public void setServerPlayerCount(String serverName, int playerCount) {
		serverPlayerCounts.put(serverName, playerCount);
	}

	public int getServerPlayerCount(String serverName) {
		return serverPlayerCounts.getOrDefault(serverName, 0);
	}

	public int getEntirePlayerCount() {
		return getServerPlayerCount("ALL");
	}
}
