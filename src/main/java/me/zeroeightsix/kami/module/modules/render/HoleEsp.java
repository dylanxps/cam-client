package me.zeroeightsix.kami.module.modules.render;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import org.lwjgl.opengl.GL11;

import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.util.Wrapper;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.ArrayList;

import static me.zeroeightsix.kami.util.EntityUtil.calculateLookAt;
import java.util.concurrent.TimeUnit;

@Module.Info(name = "HoleFinder", category = Module.Category.RENDER)
public class HoleEsp extends Module {
	private Setting<Double> range = register(Settings.d("Range", 7));
	private Setting<Boolean> highlight = register(Settings.b("Block Highlight", false));
	private Setting<Boolean> box = register(Settings.b("Bouding Box", true));
	private Setting<Boolean> bottom = register(Settings.b("Bottom Highlight", false));
	private Setting<Boolean> bottomBox = register(Settings.b("Bottom Bounding Box", false));
	private Setting<Integer> Red = register(
			Settings.integerBuilder("Red").withMinimum(1).withMaximum(255).withValue(255));
	private Setting<Integer> Green = register(
			Settings.integerBuilder("Green").withMinimum(1).withMaximum(255).withValue(0));
	private Setting<Integer> Blue = register(
			Settings.integerBuilder("Blue").withMinimum(1).withMaximum(255).withValue(255));
	private Setting<Integer> alpha = register(
			Settings.integerBuilder("Alpha").withMinimum(1).withMaximum(255).withValue(125));
	private Setting<Integer> alpha2 = register(
			Settings.integerBuilder("Bounding Box Alpha").withMinimum(1).withMaximum(255).withValue(255));
	private Setting<Float> width = register(
			Settings.floatBuilder("Line Width").withMinimum(0f).withMaximum(7f).withValue(1.5f));
	BlockPos render;
	@Override
	public void onUpdate() {
		List<BlockPos> blocks = findCrystalBlocks();
		BlockPos q = null;
		for (BlockPos blockPos : blocks) {
			q = blockPos;

		}
		render = q;
	}

	@Override
	public void onWorldRender(RenderEvent event) {
		List<BlockPos> blocks = findCrystalBlocks();

		GL11.glEnable(GL11.GL_CULL_FACE);
		if (render != null) {
			for (BlockPos hole : blocks) {
				if (highlight.getValue()) {
					KamiTessellator.prepare(GL11.GL_QUADS);
					KamiTessellator.drawBox(hole, Red.getValue(), Green.getValue(), Blue.getValue(), alpha.getValue(),
							GeometryMasks.Quad.ALL);
					KamiTessellator.release();
				}
				if (box.getValue()) {
					KamiTessellator.prepare(GL11.GL_QUADS);
					KamiTessellator.drawBoundingBoxBlockPos(hole, width.getValue(), Red.getValue(), Green.getValue(),
							Blue.getValue(), alpha2.getValue());
					KamiTessellator.release();
				}
				if (bottom.getValue()) {
					KamiTessellator.prepare(GL11.GL_QUADS);
					KamiTessellator.drawBoxBottom(hole, Red.getValue(), Green.getValue(), Blue.getValue(),
							alpha.getValue());
					KamiTessellator.release();
				}
				if (bottomBox.getValue()) {
					KamiTessellator.prepare(GL11.GL_QUADS);
					KamiTessellator.drawBoundingBoxBottomBlockPos(hole, width.getValue(), Red.getValue(),
							Green.getValue(), Blue.getValue(), alpha2.getValue());
					KamiTessellator.release();
				}
			}
		}
	}

	private boolean IsHole(BlockPos blockPos) {
		BlockPos boost = blockPos.add(0, 1, 0);
		BlockPos boost2 = blockPos.add(0, 0, 0);
		BlockPos boost3 = blockPos.add(0, 0, -1);
		BlockPos boost4 = blockPos.add(1, 0, 0);
		BlockPos boost5 = blockPos.add(-1, 0, 0);
		BlockPos boost6 = blockPos.add(0, 0, 1);
		BlockPos boost7 = blockPos.add(0, 2, 0);
		BlockPos boost8 = blockPos.add(0.5, 0.5, 0.5);
		BlockPos boost9 = blockPos.add(0, -1, 0);
		if ((mc.world.getBlockState(boost).getBlock() == Blocks.AIR
				&& (mc.world.getBlockState(boost2).getBlock() == Blocks.AIR)
				&& (mc.world.getBlockState(boost7).getBlock() == Blocks.AIR)
				&& (mc.world.getBlockState(boost3).getBlock() != Blocks.AIR)// fakeandgay
				&& (mc.world.getBlockState(boost4).getBlock() != Blocks.AIR)
				&& (mc.world.getBlockState(boost9).getBlock() != Blocks.AIR)
				&& (mc.world.getBlockState(boost5).getBlock() != Blocks.AIR)
				&& (mc.world.getBlockState(boost8).getBlock() == Blocks.AIR)
				&& (mc.world.getBlockState(boost6).getBlock() != Blocks.AIR))) {
			return true;
		} else {
			return false;
		}
	}

	public static BlockPos getPlayerPos() {
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

	private List<BlockPos> findCrystalBlocks() {
		NonNullList<BlockPos> positions = NonNullList.create();
		positions.addAll(
				getSphere(getPlayerPos(), range.getValue().floatValue(), range.getValue().intValue(), false, true, 0)
						.stream().filter(this::IsHole).collect(Collectors.toList()));
		return positions;
	}

	public List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y) {
		List<BlockPos> circleblocks = new ArrayList<>();
		int cx = loc.getX();
		int cy = loc.getY();
		int cz = loc.getZ();
		for (int x = cx - (int) r; x <= cx + r; x++) {
			for (int z = cz - (int) r; z <= cz + r; z++) {
				for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++) {
					double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
					if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1))) {
						BlockPos l = new BlockPos(x, y + plus_y, z);
						circleblocks.add(l);
					}
				}
			}
		}
		return circleblocks;
	}

	@Override
	public void onDisable() {
		render = null;
	}
}
