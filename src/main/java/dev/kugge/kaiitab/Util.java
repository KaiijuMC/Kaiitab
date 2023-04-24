package dev.kugge.kaiitab;

import io.papermc.paper.threadedregions.RegionizedServer;
import io.papermc.paper.threadedregions.ThreadedRegionizer;
import io.papermc.paper.threadedregions.ThreadedRegionizer.ThreadedRegion;
import io.papermc.paper.threadedregions.TickData;
import io.papermc.paper.threadedregions.TickRegions;

import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import net.minecraft.server.level.ServerLevel;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;

import java.util.ArrayList;
import java.util.List;

public class Util {
    public static double getGlobalMspt() {
        final DoubleArrayList tpsByRegion = new DoubleArrayList();
        final List<TickData.TickReportData> reportsByRegion = new ArrayList<>();
        long currTime = System.nanoTime();

        final List<ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData>> regions =
            new ArrayList<>();

        for (final World bukkitWorld : Bukkit.getWorlds()) {
            if (bukkitWorld == null) return 0;
            final ServerLevel world = ((CraftWorld) bukkitWorld).getHandle();
            world.regioniser.computeForAllRegions(regions::add);
        }
        for (final ThreadedRegionizer.ThreadedRegion<TickRegions.TickRegionData, TickRegions.TickRegionSectionData> region : regions) {
            final TickData.TickReportData report = region.getData().getRegionSchedulingHandle().getTickReport15s(currTime);
            tpsByRegion.add(report == null ? 20.0 : report.tpsData().segmentAll().average());
            reportsByRegion.add(report);
        }
        final TickData.TickReportData globalTickReport = RegionizedServer.getGlobalTickData().getTickReport15s(currTime);
        return globalTickReport.timePerTickData().segmentAll().average() / 1.0E6;
    }
}
