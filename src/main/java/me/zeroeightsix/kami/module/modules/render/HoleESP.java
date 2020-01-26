package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.module.modules.combat.CrystalAura;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.GeometryMasks;
import me.zeroeightsix.kami.util.KamiTessellator;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static me.zeroeightsix.kami.module.modules.combat.CrystalAura.getPlayerPos;


/**
 * Created 16 November 2019 by hub
 * Updated by S-B99 on 15/12/19
 */
@Module.Info(name = "HoleESP", category = Module.Category.COMBAT, description = "Show safe holes")
public class HoleESP extends Module {

    private final BlockPos[] surroundOffset = {
            new BlockPos(0, -1, 0), // down
            new BlockPos(0, 0, -1), // north
            new BlockPos(1, 0, 0), // east
            new BlockPos(0, 0, 1), // south
            new BlockPos(-1, 0, 0) // west
    };

    private Setting<Double> renderDistance = register(Settings.d("Render Distance", 8.0d));
    private Setting<Boolean> renObby = register(Settings.b("Render Obby", true));
    private Setting<Boolean> renBedr = register(Settings.b("Render Bedrock", true));
    private Setting<RenderMode> renderMode = register(Settings.e("Render Mode", RenderMode.BLOCK));
    private Setting<Integer> Red = register(
            Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(255));
    private Setting<Integer> Green = register(
            Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(0));
    private Setting<Integer> Blue = register(
            Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(255));
    private Setting<Integer> Alpha = register(
            Settings.integerBuilder("Alpha").withMinimum(0).withMaximum(70).withValue(45));
    private Setting<Integer> Red2 = register(
            Settings.integerBuilder("Red").withMinimum(0).withMaximum(255).withValue(255));
    private Setting<Integer> Green2 = register(
            Settings.integerBuilder("Green").withMinimum(0).withMaximum(255).withValue(0));
    private Setting<Integer> Blue2 = register(
            Settings.integerBuilder("Blue").withMinimum(0).withMaximum(255).withValue(255));


    private ConcurrentHashMap<BlockPos, Boolean> safeHoles;

    @Override
    public void onUpdate() {

        if (safeHoles == null) {
            safeHoles = new ConcurrentHashMap<>();
        } else {
            safeHoles.clear();
        }

        int range = (int) Math.ceil(renderDistance.getValue());

    /*    CrystalAura crystalAura = (CrystalAura) ModuleManager.getModuleByName("CrystalAura");
        List<BlockPos> blockPosList = crystalAura.getSphere(getPlayerPos(), range, range, false, true, 0);

        for (BlockPos pos : blockPosList) {

            // block gotta be air
            if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 1 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 1, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            // block 2 above gotta be air
            if (!mc.world.getBlockState(pos.add(0, 2, 0)).getBlock().equals(Blocks.AIR)) {
                continue;
            }

            boolean isSafe = true;
            boolean isBedrock = true;

            for (BlockPos offset : surroundOffset) {
                Block block = mc.world.getBlockState(pos.add(offset)).getBlock();
                if (block != Blocks.BEDROCK) {
                    isBedrock = false;
                }
                if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST && block != Blocks.ANVIL) {
                    isSafe = false;
                    break;
                }
            }

            if (isSafe) {
                safeHoles.put(pos, isBedrock);
            }

        }

    */}

    @Override
    public void onWorldRender(final RenderEvent event) {

        if (mc.player == null || safeHoles == null) {
            return;
        }

        if (safeHoles.isEmpty()) {
            return;
        }

        KamiTessellator.prepare(GL11.GL_QUADS);

        safeHoles.forEach((blockPos, isBedrock) -> {
            if (isBedrock && renBedr.getValue()) {
                drawBox(blockPos, Red2.getValue(), Green2.getValue(), Blue2.getValue());
            } else if (renObby.getValue()){
                drawBox(blockPos, Red.getValue(), Green.getValue(), Blue.getValue());
            }
        });

        KamiTessellator.release();

    }

    private void drawBox(BlockPos blockPos, int r, int g, int b) {
        Color color = new Color(r, g, b, Alpha.getValue());
        if (renderMode.getValue().equals(RenderMode.DOWN)) {
            KamiTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.DOWN);
        } else if (renderMode.getValue().equals(RenderMode.BLOCK)) {
            KamiTessellator.drawBox(blockPos, color.getRGB(), GeometryMasks.Quad.ALL);
        }
    }

    private enum RenderMode {
        DOWN, BLOCK
    }

}