package com.pvpraids.rhub.tasks;

import com.pvpraids.rhub.HubPlugin;
import com.pvpraids.rhub.menus.ServerSelectorMenu;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlayerCountUpdateTask implements Runnable {
	private final HubPlugin plugin;

	@Override
	public void run() {
		if (!plugin.getServer().getOnlinePlayers().isEmpty()) {
			BungeeChannelApi bungeeChannelApi = plugin.getBungeeChannelApi();

			try {
				int entirePlayerCount = bungeeChannelApi.getPlayerCount("ALL").get();

				plugin.setServerPlayerCount("ALL", entirePlayerCount);

				List<String> serverNames = bungeeChannelApi.getServers().get();

				for (String serverName : serverNames) {
					int playerCount = bungeeChannelApi.getPlayerCount(serverName).get();

					plugin.setServerPlayerCount(serverName, playerCount);
				}

				plugin.getMenuManager().getMenu(ServerSelectorMenu.class).update();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}
}
