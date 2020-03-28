package com.pvpraids.rhub.tasks;

import com.pvpraids.rhub.HubPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

@RequiredArgsConstructor
public class QueueTask extends BukkitRunnable {
	private static final int TEAMS_MAX = 120;

	private final HubPlugin plugin;

	@Override
	public void run() {
		int teamsPlayerCount = plugin.getServerPlayerCount("teams");
		if (teamsPlayerCount < TEAMS_MAX) {
			plugin.getQueueManager().removeFromQueue();
		}
	}
}
