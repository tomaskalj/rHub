package com.pvpraids.rhub.manager;

import com.pvpraids.core.inventory.menu.Menu;
import com.pvpraids.rhub.HubPlugin;
import com.pvpraids.rhub.menus.ServerSelectorMenu;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.inventory.Inventory;

public class MenuManager {
	private final Map<Class<? extends Menu>, Menu> menus = new HashMap<>();

	public MenuManager(HubPlugin plugin) {
		registerMenus(
				new ServerSelectorMenu(plugin)
		);
	}

	public Menu getMenu(Class<? extends Menu> clazz) {
		return menus.get(clazz);
	}

	public Menu getMatchingMenu(Inventory other) {
		for (Menu menu : menus.values()) {
			if (menu.getInventory().equals(other)) {
				return menu;
			}
		}

		return null;
	}

	public void registerMenus(Menu... menus) {
		for (Menu menu : menus) {
			menu.setup();
			menu.update();
			this.menus.put(menu.getClass(), menu);
		}
	}
}
