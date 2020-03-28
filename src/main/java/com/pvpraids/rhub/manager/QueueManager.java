package com.pvpraids.rhub.manager;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.rhub.HubPlugin;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import org.bukkit.entity.Player;

public class QueueManager {
	private final HubPlugin plugin;

	private final Map<Rank, Queue<UUID>> queueMap = new HashMap<>();

	public QueueManager(HubPlugin plugin) {
		this.plugin = plugin;

		Arrays.asList(Rank.HIGHROLLER, Rank.PRO, Rank.VIP, Rank.DEFAULT).forEach(rank -> queueMap.put(rank, new LinkedList<>()));
	}

	public void addToQueue(Player player) {
		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		Queue<UUID> queue = queueMap.get(profile.getRank());
		queue.add(player.getUniqueId());

		queueMap.put(profile.getRank(), queue);
	}

	public void removeFromQueue() {
		Queue<UUID> queue;
		if (queueMap.get(Rank.HIGHROLLER).isEmpty()) {
			if (queueMap.get(Rank.PRO).isEmpty()) {
				if (queueMap.get(Rank.VIP).isEmpty()) {
					if (queueMap.get(Rank.DEFAULT).isEmpty()) {
						return;
					} else {
						queue = queueMap.get(Rank.DEFAULT);
					}
				} else {
					queue = queueMap.get(Rank.VIP);
				}
			} else {
				queue = queueMap.get(Rank.PRO);
			}
		} else {
			queue = queueMap.get(Rank.HIGHROLLER);
		}

		CoreProfile first = CorePlugin.getInstance().getProfileManager().getProfile(queue.remove());
		Rank removedFrom = first.getRank();

		plugin.getBungeeChannelApi().connect(plugin.getServer().getPlayer(first.getId()), "teams");

		queueMap.put(removedFrom, queue);
	}
}
