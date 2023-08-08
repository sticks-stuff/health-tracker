package dev.sharlot.health_tracker;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class App extends JavaPlugin implements Listener {
    Scoreboard scoreboard = null;
    Objective healthObjectiveSidebar = null;
    Objective healthObjectiveList = null;
    
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        healthObjectiveSidebar = this.scoreboard.registerNewObjective("healthSidebar", Criteria.DUMMY, "Health", RenderType.HEARTS);
        healthObjectiveList = this.scoreboard.registerNewObjective("healthList", Criteria.DUMMY, "Health", RenderType.HEARTS);
        healthObjectiveSidebar.setDisplaySlot(DisplaySlot.SIDEBAR);
        healthObjectiveList.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if(player.getGameMode() == GameMode.SPECTATOR) {
                scoreboard.resetScores(player.getPlayerListName());
            } else {
                healthObjectiveSidebar.getScore(player.getPlayerListName()).setScore((int) (player.getHealth() + player.getAbsorptionAmount()));
                healthObjectiveList.getScore(player.getPlayerListName()).setScore((int) (player.getHealth() + player.getAbsorptionAmount()));
            }
            player.setScoreboard(scoreboard);
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if(e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            scoreboard.resetScores(e.getPlayer().getPlayerListName());
        } else {
            healthObjectiveSidebar.getScore(e.getPlayer().getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
            healthObjectiveList.getScore(e.getPlayer().getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
        }
        e.getPlayer().setScoreboard(scoreboard);
    } 

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        new BukkitRunnable() {
            public void run() {
                if(e.getPlayer().getGameMode() == GameMode.SPECTATOR) {
                    scoreboard.resetScores(e.getPlayer().getPlayerListName());
                } else {
                    healthObjectiveSidebar.getScore(e.getPlayer().getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
                    healthObjectiveList.getScore(e.getPlayer().getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
                }
            }
        }.runTaskLater(this, 1); //for some reason respawn health is old so lets delay this by 1 tick to ensure we get the new health
        //this isnt even necessary for uhc but its here for completeness sake
    }
    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            healthObjectiveSidebar.getScore(((Player)(e.getEntity())).getPlayerListName()).setScore((int)(((Player)(e.getEntity())).getHealth() + ((Player)(e.getEntity())).getAbsorptionAmount() - e.getDamage()));
            healthObjectiveList.getScore(((Player)(e.getEntity())).getPlayerListName()).setScore((int)(((Player)(e.getEntity())).getHealth() + ((Player)(e.getEntity())).getAbsorptionAmount() - e.getDamage()));
        }
    }

    @EventHandler
    public void onHeal(EntityRegainHealthEvent e) {
        if(e.getEntity() instanceof Player) {
            healthObjectiveSidebar.getScore(((Player)(e.getEntity())).getPlayerListName()).setScore((int)(((Player)(e.getEntity())).getHealth() + ((Player)(e.getEntity())).getAbsorptionAmount() + e.getAmount()));
            healthObjectiveList.getScore(((Player)(e.getEntity())).getPlayerListName()).setScore((int)(((Player)(e.getEntity())).getHealth() + ((Player)(e.getEntity())).getAbsorptionAmount() + e.getAmount()));
        }
    }

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) { //hacky thing to get absorbtion from gapples to track when eaten
        new BukkitRunnable() {
            public void run() {
                healthObjectiveSidebar.getScore(e.getPlayer().getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
                healthObjectiveList.getScore(e.getPlayer().getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
            }
        }.runTaskLater(this, 1);
    }

    @EventHandler
    public void onGamemode(PlayerGameModeChangeEvent e) {
        if(e.getNewGameMode() == GameMode.SPECTATOR) {
            scoreboard.resetScores(e.getPlayer().getPlayerListName());
        } else {
            healthObjectiveSidebar.getScore((e.getPlayer()).getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
            healthObjectiveList.getScore((e.getPlayer()).getPlayerListName()).setScore((int) (e.getPlayer().getHealth() + e.getPlayer().getAbsorptionAmount()));
        }
    }
}
