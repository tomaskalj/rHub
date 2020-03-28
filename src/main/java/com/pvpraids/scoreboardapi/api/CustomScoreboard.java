package com.pvpraids.scoreboardapi.api;

import com.pvpraids.scoreboardapi.ScoreboardHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomScoreboard implements Listener, Runnable {
	private final ScoreboardHandler handler = new ScoreboardHandler();

	public CustomScoreboard(JavaPlugin plugin, int updateRate) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, updateRate);
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		handler.updateScoreboard(player);
	}

	public void forceUpdate(Player player) {
		handler.updateScoreboard(player);
	}

	@Override
	public void run() {
		Bukkit.getOnlinePlayers().forEach(handler::updateScoreboard);
	}
}
