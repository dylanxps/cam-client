package me.zeroeightsix.kami.module.modules.combat;

import me.zero.alpine.listener.EventHandler;
import me.zero.alpine.listener.Listener;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zeroeightsix.kami.event.events.RenderEvent;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;
import me.zeroeightsix.kami.util.*;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static me.zeroeightsix.kami.util.EntityUtil.calculateLookAt;

@Module.Info(name = "CamiAura", category = Module.Category.COMBAT)
public class AutoCrystal extends Module {

	private Setting<Boolean> autoSwitch = register(Settings.b("Auto Switch"));
	private Setting<Boolean> antiSuicide = register(Settings.b("Anti Suicide", true));
	private Setting<Boolean> ignoreMin = register(Settings.b("Ingore Min Dmg if Lethal", true));
	private Setting<Double> range = register(Settings.d("Range", 6));
	private Setting<Integer> hitSpeed = register(
			Settings.integerBuilder("Hit Speed").withMinimum(0).withMaximum(20).withValue(20));
	private Setting<Integer> placeDelay = register(
			Settings.integerBuilder("Place Delay").withMinimum(1).withMaximum(30).withValue(0));
	private Setting<Integer> minDmg = register(
			Settings.integerBuilder("Min Damage").withMinimum(0).withMaximum(16).withValue(1));
	private Setting<Integer> targetDistance = register(
			Settings.integerBuilder("Target Distance").withMinimum(1).withMaximum(13).withValue(9));
	private Setting<Integer> red = register(
			Settings.integerBuilder("Red").withMinimum(1).withMaximum(255).withValue(255));
	private Setting<Integer> green = register(
			Settings.integerBuilder("Green").withMinimum(1).withMaximum(255).withValue(1));
	private Setting<Integer> blue = register(
			Settings.integerBuilder("Blue").withMinimum(1).withMaximum(255).withValue(1));

	private BlockPos render;
	private long systemTimeBreak = -1;
	private long systemTimePlace = -1;
	private boolean switchCooldown = false;
	private int newSlot;
	private double lastDmg = 0;

	@Override
	public void onUpdate() {
		EntityEnderCrystal crystal = mc.world.loadedEntityList.stream()
				.filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> (EntityEnderCrystal) entity)
				.min(Comparator.comparing(c -> mc.player.getDistance(c))).orElse(null);
		if (crystal != null && mc.player.getDistance(crystal) <= range.getValue()) {
			if (((System.nanoTime() / 1000000) - systemTimeBreak) >= 400 - hitSpeed.getValue() * 20) {
				lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);
				mc.playerController.attackEntity(mc.player, crystal);
				mc.player.swingArm(EnumHand.MAIN_HAND);
				systemTimeBreak = System.nanoTime() / 1000000;
			}
			if (lastDmg < 10) {
				return;
			}
		} else {
			resetRotation();
		}

		int crystalSlot = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL
				? mc.player.inventory.currentItem
				: -1;
		if (crystalSlot == -1) {
			for (int l = 0; l < 9; ++l) {
				if (mc.player.inventory.getStackInSlot(l).getItem() == Items.END_CRYSTAL) {
					crystalSlot = l;
					break;
				}
			}
		}

		boolean offhand = false;
		if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
			offhand = true;
		} else if (crystalSlot == -1) {
			return;
		}

		List<BlockPos> blocks = findCrystalBlocks();
		List<Entity> entities = new ArrayList<>();
		entities.addAll(mc.world.playerEntities.stream()
				.filter(entityPlayer -> !Friends.isFriend(entityPlayer.getName())).collect(Collectors.toList()));
		BlockPos q = null;
		double damage = .5;
		for (Entity entity : entities) {
			if (entity == mc.player || ((EntityLivingBase) entity).getHealth() <= 0) {
				continue;
			}
			if(entity.getName().equals("69hr") || entity.getName().equals("Warl") || entity.getName().equals("Ndw") || entity.getName().equals("Blinkinq") || entity.getName().equals("Newfaq")) {
				continue;
			}
			if (mc.player.getDistance(entity) > targetDistance.getValue()) {
				continue;
			}
			for (BlockPos blockPos : blocks) {
				double b = entity.getDistanceSq(blockPos);
				if (b >= 36) {
					continue;
				}
				double d = calculateDamage(blockPos.x + .5, blockPos.y + 1, blockPos.z + .5, entity);
				if (d < minDmg.getValue()) {
					if (ignoreMin.getValue()) {
						if (((EntityLivingBase) entity).getHealth() +((EntityLivingBase) entity).getAbsorptionAmount() > 10) {
							continue;
						}
					} else
						continue;
				}
				if (d > damage) {
					if (antiSuicide.getValue()) {
						double self = calculateDamage(blockPos.x + .5, blockPos.y + 1, blockPos.z + .5, mc.player);
						if (self > d || self - .5 > mc.player.getHealth() + mc.player.getAbsorptionAmount()) {
							continue;
						}
					}
					damage = d;
					lastDmg = d;
					q = blockPos;
				}
			}
		}
		if (damage == .5) {
			render = null;
			resetRotation();
			return;
		}
		render = q;

		if (!offhand && mc.player.inventory.currentItem != crystalSlot) {
			if (autoSwitch.getValue()) {
				mc.player.inventory.currentItem = crystalSlot;
				resetRotation();
				switchCooldown = true;
			}
			return;
		}
		lookAtPacket(q.x + .5, q.y - .5, q.z + .5, mc.player);
		RayTraceResult result = mc.world.rayTraceBlocks(
				new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ),
				new Vec3d(q.x + .5, q.y - .5d, q.z + .5));
		EnumFacing f;
		if (result == null || result.sideHit == null) {
			f = EnumFacing.UP;
		} else {
			f = result.sideHit;
		}
		if (switchCooldown) {
			switchCooldown = false;
			return;
		}
		if (((System.nanoTime() / 1000000) - systemTimeBreak) >= placeDelay.getValue() * 15) {
			mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(q, f,
					offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0, 0, 0));
		}
	}

	@Override
	public void onWorldRender(RenderEvent event) {
		if (render != null) {
			KamiTessellator.prepare(GL11.GL_QUADS);
			KamiTessellator.drawBox(render, red.getValue(), green.getValue(), blue.getValue(), 80, GeometryMasks.Quad.ALL);
			KamiTessellator.release();
			KamiTessellator.prepare(GL11.GL_QUADS);
			KamiTessellator.drawBoundingBoxBlockPos(render, .88f, red.getValue(), green.getValue(), blue.getValue(), 255);
			KamiTessellator.release();
		}
	}

	private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
		double[] v = calculateLookAt(px, py, pz, me);
		setYawAndPitch((float) v[0], (float) v[1]);
	}

	private boolean canPlaceCrystal(BlockPos blockPos) {
		BlockPos boost = blockPos.add(0, 1, 0);
		BlockPos boost2 = blockPos.add(0, 2, 0);
		return (mc.world.getBlockState(blockPos).getBlock() == Blocks.BEDROCK
				|| mc.world.getBlockState(blockPos).getBlock() == Blocks.OBSIDIAN)
				&& mc.world.getBlockState(boost).getBlock() == Blocks.AIR
				&& mc.world.getBlockState(boost2).getBlock() == Blocks.AIR
				&& mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty()
				&& mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost2)).isEmpty();
	}

	public static BlockPos getPlayerPos() {
		return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
	}

	private List<BlockPos> findCrystalBlocks() {
		NonNullList<BlockPos> positions = NonNullList.create();
		positions.addAll(
				getSphere(getPlayerPos(), range.getValue().floatValue(), range.getValue().intValue(), false, true, 0)
						.stream().filter(this::canPlaceCrystal).collect(Collectors.toList()));
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

	public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
		float doubleExplosionSize = 6.0F * 2.0F;
		double distancedsize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
		Vec3d vec3d = new Vec3d(posX, posY, posZ);
		double blockDensity = (double) entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
		double v = (1.0D - distancedsize) * blockDensity;
		float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
		double finald = 1;
		/*
		 * if (entity instanceof EntityLivingBase) finald =
		 * getBlastReduction((EntityLivingBase) entity,getDamageMultiplied(damage));
		 */
		if (entity instanceof EntityLivingBase) {
			finald = getBlastReduction((EntityLivingBase) entity, getDamageMultiplied(damage),
					new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
		}
		return (float) finald;
	}

	public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
		if (entity instanceof EntityPlayer) {
			EntityPlayer ep = (EntityPlayer) entity;
			DamageSource ds = DamageSource.causeExplosionDamage(explosion);
			damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(),
					(float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

			int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
			float f = MathHelper.clamp(k, 0.0F, 20.0F);
			damage = damage * (1.0F - f / 25.0F);

			if (entity.isPotionActive(Potion.getPotionById(11))) {
				damage = damage - (damage / 4);
			}

			damage = Math.max(damage, 0.0F);
			return damage;
		}
		damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(),
				(float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
		return damage;
	}

	private static float getDamageMultiplied(float damage) {
		int diff = mc.world.getDifficulty().getId();
		return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
	}

	public static float calculateDamage(EntityEnderCrystal crystal, Entity entity) {
		return calculateDamage(crystal.posX, crystal.posY, crystal.posZ, entity);
	}

	private static boolean isSpoofingAngles;
	private static double yaw;
	private static double pitch;
	private static int madeByKrts;
	private static int akalgbttqqiaap;

	private static void setYawAndPitch(float yaw1, float pitch1) {
		yaw = yaw1;
		pitch = pitch1;
		isSpoofingAngles = true;
	}

	private static void resetRotation() {
		if (isSpoofingAngles) {
			yaw = mc.player.rotationYaw;
			pitch = mc.player.rotationPitch;
			isSpoofingAngles = false;
		}
	}

	@EventHandler
	private Listener<PacketEvent.Send> packetListener = new Listener<>(event -> {
		Packet packet = event.getPacket();
		if (packet instanceof CPacketPlayer) {
			if (isSpoofingAngles) {
				((CPacketPlayer) packet).yaw = (float) yaw;
				((CPacketPlayer) packet).pitch = (float) pitch;
			}
		}
	});

	@Override
	public void onDisable() {
		render = null;
		resetRotation();
	}
}
