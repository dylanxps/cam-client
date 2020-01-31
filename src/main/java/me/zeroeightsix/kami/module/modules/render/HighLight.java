package me.zeroeightsix.kami.module.modules.render;

import me.zeroeightsix.kami.module.*;
import me.zeroeightsix.kami.setting.*;
import net.minecraftforge.common.*;
import me.zeroeightsix.kami.event.events.*;
import net.minecraft.client.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.*;
import me.zeroeightsix.kami.util.*;
import net.minecraft.block.state.*;
import net.minecraft.util.math.*;
import net.minecraftforge.client.event.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.opengl.GL11;
import java.awt.Color;

@Module.Info(name = "BlockHighlight", description = "Happy Halloween <3", category = Module.Category.RENDER)
public class HighLight extends Module {
	private Setting<Boolean> boundingbox = register(Settings.b("Bouding Box", true));
	private Setting<Boolean> box = register(Settings.b("Full Block Highlight", false));
	private Setting<Double> width = register(Settings.d("Width", 3.0));
	private Setting<Integer> alpha = register(
			Settings.integerBuilder("Alpha").withMinimum(1).withMaximum(255).withValue(255));
	private Setting<Integer> alpha2 = register(
			Settings.integerBuilder("Bounding Box Alpha").withMinimum(1).withMaximum(255).withValue(20));

	public void onWorldRender(RenderEvent event) {
		final Minecraft mc = Minecraft.getMinecraft();
		final RayTraceResult ray = mc.objectMouseOver;
		if (ray.typeOfHit == RayTraceResult.Type.BLOCK) {

			final BlockPos blockpos = ray.getBlockPos();
			final IBlockState iblockstate = mc.world.getBlockState(blockpos);

			if (iblockstate.getMaterial() != Material.AIR && mc.world.getWorldBorder().contains(blockpos)) {
				if (boundingbox.getValue()) {
					KamiTessellator.prepare(GL11.GL_QUADS);
					KamiTessellator.drawBox(blockpos, HUD.red(), HUD.green(), HUD.blue(), alpha2.getValue(),
							GeometryMasks.Quad.ALL);
					KamiTessellator.release();
				}
				if (box.getValue()) {
					KamiTessellator.prepare(GL11.GL_QUADS);
					KamiTessellator.drawBoundingBoxBlockPos(blockpos, width.getValue().floatValue(), HUD.red(),
							HUD.green(), HUD.blue(), alpha.getValue());
					KamiTessellator.release();
				}
			}
		}
	}
}
