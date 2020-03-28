package com.pvpraids.rhub.menus;

import com.pvpraids.core.inventory.menu.Menu;
import com.pvpraids.core.utils.item.ItemBuilder;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.rhub.HubPlugin;
import org.bukkit.Material;

public class ServerSelectorMenu extends Menu {
	private final HubPlugin plugin;

	public ServerSelectorMenu(HubPlugin plugin) {
		super(1, "Server Selector");
		this.plugin = plugin;
	}

	@Override
	public void setup() {
	}

	@Override
	public void update() {
		setActionableItem(4, new ItemBuilder(Material.DIAMOND_PICKAXE)
						.name(CC.PRIMARY + "Teams")
						.lore(CC.GREEN + "Raid players' bases and fight with loot.",
								CC.PRIMARY + "Online: " + CC.SECONDARY + plugin.getServerPlayerCount("teams"))
						.build(),
				player -> {
//					plugin.getBungeeChannelApi().connect(player, "teams");
					plugin.getQueueManager().addToQueue(player);
					player.closeInventory();
				}
		);
	}
}
