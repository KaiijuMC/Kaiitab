package dev.kugge.kaiitab.watcher;

import dev.kugge.kaiitab.Kaiitab;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegionScheduler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import java.util.concurrent.TimeUnit;

public class LoginWatcher implements Listener {
    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        Bukkit.getAsyncScheduler().runAtFixedRate(Kaiitab.instance, task1 -> player.getScheduler().run(
                Kaiitab.instance,
                task2 -> {
                    TickData.TickReportData report = TickRegionScheduler.getCurrentRegion()
                                                                        .getData()
                                                                        .getRegionSchedulingHandle()
                                                                        .getTickReport15s(System.nanoTime());
                    double rawLmspt = report.timePerTickData().segmentAll().average() / 1.0E6;
                    String localMspt = report == null ? "0.00" : String.format("%.2f", rawLmspt);
                    int rawPing = player.getPing();

                    // PING
                    NamedTextColor pingColor;
                    if (rawPing < 70) pingColor = NamedTextColor.GREEN;
                    else if (rawPing < 120) pingColor = NamedTextColor.GOLD;
                    else pingColor = NamedTextColor.RED;

                    // MSPT
                    NamedTextColor msptColor;
                    if (rawLmspt < 50) msptColor = NamedTextColor.GREEN;
                    else if (rawLmspt < 55) msptColor = NamedTextColor.GOLD;
                    else msptColor = NamedTextColor.RED;

                    final Component lmspt = Component.text(localMspt, msptColor);
                    final Component ping = Component.text(rawPing, pingColor);
                    final Component footer = Component.text("Ping: ", NamedTextColor.GRAY).append(ping);
                    final Component header = Component.text("MSPT: ", NamedTextColor.GRAY).append(lmspt);
                    player.sendPlayerListHeaderAndFooter(header, footer);
                }, null), 1, 5, TimeUnit.SECONDS);
    }
}
