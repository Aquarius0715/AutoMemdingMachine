package aquarius0715.automendhing;


import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public final class AutoMendingMachine extends JavaPlugin implements Listener {

    Map<Player, Integer> itemAmount = new HashMap<>();//GUIにいれたアイテム数をプレイヤーごとに管理する

    @Override
    public void onEnable() {
        getLogger().info("AutoEXPMachine is running.");
        getServer().getPluginManager().registerEvents(this, this);
        // config.ymlが存在しない場合はファイルに出力します。
        saveDefaultConfig();
        // config.ymlを読み込みます。
        FileConfiguration config = getConfig();
        reloadConfig();
        if (config.getBoolean("mode")) {
            getLogger().info("AutoEXPMachine is not running.");
        }
    }


    @EventHandler
    public void onPlayerClicks(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();
        FileConfiguration config = getConfig();
        if (!config.getBoolean("mode")) {
            getLogger().info("AutoEXPMachine is not running.");
            return;
        }
        if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK)) {
            if (item != null && item.getType() == Material.STICK) {
                player.sendMessage("経験値瓶を入れてください");
                openGUI(player);
            }
        }
    }


    //GUIを開かせる
    public void openGUI(Player player) {
        Inventory inv = Bukkit.createInventory(null, 9, "EXPTank");
        FileConfiguration config = getConfig();
        if (!config.getBoolean("mode")) {
            getLogger().info("AutoEXPMachine is not running.");
            return;
        }
        player.openInventory(inv);
        if (itemAmount.get(player) != null){
            Integer amount = itemAmount.get(player);
            ItemStack item = new ItemStack(Material.EXPERIENCE_BOTTLE, amount);
            inv.addItem(item);
        }
    }

    @EventHandler
    public void onItemClick(InventoryClickEvent event) {
        Player p = (Player) event.getWhoClicked();
        String q = event.getView().getTitle();
        FileConfiguration config = getConfig();
        if (!config.getBoolean("mode")) {
            getLogger().info("AutoEXPMachine is not running.");
            return;
        }
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() != Material.EXPERIENCE_BOTTLE) {
            if ("EXPTank".equals(q)) {
                if (event.getCurrentItem() != null){
                    p.sendMessage("入れられるのは経験値瓶のみです！");
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void closeGUI(InventoryCloseEvent event){
        Player p = (Player) event.getPlayer();
        Inventory inv = event.getInventory();
        if(event.getView().getTitle().equals("EXPtank")) {
            int amount = 0;
            for (int i = 0; i < inv.getSize(); i++) {
                if (inv.getItem(i) != null) {
                    if (Objects.requireNonNull(inv.getItem(i)).getType() == Material.EXPERIENCE_BOTTLE) {
                        amount += Objects.requireNonNull(inv.getItem(i)).getAmount();
                    }
                }
            }
            if (amount >= 0) {
                if (amount > 0){
                    p.sendMessage(amount + "個入れました");
                }
                itemAmount.put(p, amount);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent Player) {
        Player p = Player.getPlayer();
        itemAmount.putIfAbsent(p, 0);
    }


    @EventHandler
    public void AutoMending(BlockBreakEvent event) {
        Player p = event.getPlayer();
        Material name = p.getInventory().getItemInMainHand().getType();
        Random ran = new Random();
        int nowDurability = name.getMaxDurability()-p.getInventory().getItemInMainHand().getDurability();
        int maxDurability = name.getMaxDurability();
        int amount = itemAmount.get(p);

        if (amount == 0) return;

        Inventory i = p.getInventory();
            p.sendMessage("ugoku1");
            if (name == Material.WOODEN_PICKAXE){
                p.sendMessage("ugoku2");
                if (nowDurability < maxDurability * 0.8){
                    p.sendMessage("ugoku3");
                    if (p.getInventory().getItemInMainHand().getEnchantments().containsKey(Enchantment.MENDING)) {
                        p.sendMessage("ugoku4");
                        while (nowDurability < maxDurability * 0.8){
                            amount = itemAmount.get(p);
                            if(amount == 0){
                                p.sendMessage("ugoku5");
                                break;
                            }
                            p.sendMessage("ugoku51");
                            p.giveExp(ran.nextInt(8)+3,true);
                            amount--;
                            itemAmount.put(p, amount);
                            nowDurability = name.getMaxDurability()-p.getInventory().getItemInMainHand().getDurability();
                        }
                }
            }
        }
    }
}