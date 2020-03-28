package com.pvpraids.rhub.listener;

import com.pvpraids.core.CorePlugin;
import com.pvpraids.core.player.CoreProfile;
import com.pvpraids.core.player.rank.Rank;
import com.pvpraids.core.utils.item.ItemBuilder;
import com.pvpraids.core.utils.message.CC;
import com.pvpraids.rhub.HubPlugin;
import com.pvpraids.rhub.menus.ServerSelectorMenu;
import com.pvpraids.rhub.utils.constants.Locations;
import com.pvpraids.scoreboardapi.PlayerScoreboardUpdateEvent;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

@RequiredArgsConstructor
public class PlayerListener implements Listener {
	private static final ItemStack SERVER_SELECTOR_ITEM = new ItemBuilder(Material.COMPASS).name(CC.PRIMARY + "Server Selector").build();
	private final HubPlugin plugin;

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();

		player.teleport(Locations.SPAWN);
		player.setWalkSpeed(0.4F);

		PlayerInventory playerInventory = player.getInventory();

		playerInventory.clear();
		playerInventory.setArmorContents(null);
		playerInventory.setItem(4, SERVER_SELECTOR_ITEM);
		playerInventory.setHeldItemSlot(4);

		player.sendMessage(CC.SEPARATOR);
		player.sendMessage(CC.GREEN + "Welcome to " + CC.B + "PvPRaids Network" + CC.GREEN + "!");
		player.sendMessage(CC.PRIMARY + "Pick a server with the server selector.");
		player.sendMessage("");
		player.sendMessage(CC.PRIMARY + "Discord: " + CC.SECONDARY + "discord.gg/UhRmHkv");
		player.sendMessage(CC.PRIMARY + "Twitter: " + CC.SECONDARY + "twitter.com/PvPRaidsNetwork");
		player.sendMessage(CC.PRIMARY + "Store: " + CC.SECONDARY + "store.pvpraids.com");
		player.sendMessage(CC.SEPARATOR);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		Player player = event.getPlayer();

		if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryMove(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();

		if (player.isOp() && player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (event.getClickedInventory() == player.getInventory()) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		Action action = event.getAction();

		switch (action) {
			case RIGHT_CLICK_BLOCK:
			case RIGHT_CLICK_AIR:
				if (!event.hasItem()) {
					return;
				}

				Material type = event.getItem().getType();

				switch (type) {
					case COMPASS:
						plugin.getMenuManager().getMenu(ServerSelectorMenu.class).open(player);
						break;
				}
				break;
		}
	}

	@EventHandler
	public void onCommandRun(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());

		if (!profile.hasStaff()) {
			event.setCancelled(true);
			player.sendMessage(CC.RED + "You can't use commands in the hub.");
		}
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		if (event.getFoodLevel() < 20) {
			event.setFoodLevel(20);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onScoreboardUpdate(PlayerScoreboardUpdateEvent event) {
		event.setTitle(CC.GOLD + "PvPRaids.com");
		event.setSeparator(CC.GRAY + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 25));

		Player player = event.getPlayer();
		CoreProfile profile = CorePlugin.getInstance().getProfileManager().getProfile(player.getUniqueId());
		Rank rank = profile.getRank();

		event.writeLine(CC.PRIMARY + "Rank" + CC.GRAY + ": " + rank.getColor() + rank.getName());
		event.writeLine(CC.PRIMARY + "Online" + CC.GRAY + ": " + CC.SECONDARY + plugin.getEntirePlayerCount() + "/1,000");
	}

	@EventHandler
	public void onPreDoubleJump(PlayerMoveEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) {
			player.setAllowFlight(true);
		}
	}

	@EventHandler
	public void onDoubleJump(PlayerToggleFlightEvent event) {
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		Vector jumpVelocity = player.getLocation().getDirection().normalize().multiply(3).setY(2.0);

		player.playSound(player.getLocation(), Sound.GHAST_FIREBALL, 64.0F, 1.0F);
		player.setVelocity(jumpVelocity);
		player.setAllowFlight(false);

		plugin.getServer().getScheduler().runTask(plugin, () -> {
			if (player.isOnline()) {
				player.setFlying(false);
			}
		});
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPop(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
			return;
		}

		Player damager = (Player) event.getDamager();
		Player entity = (Player) event.getEntity();

		damager.playSound(entity.getLocation(), Sound.CHICKEN_EGG_POP, 1.0F, 1.0F);
		damager.hidePlayer(entity);
		damager.sendMessage(CC.PRIMARY + "Pop!");

		entity.sendMessage(CC.PRIMARY + "You were popped by " + CC.SECONDARY + damager.getName() + CC.PRIMARY + "!");
	}
}
