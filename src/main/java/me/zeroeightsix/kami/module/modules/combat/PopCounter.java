// 
// Decompiled by Procyon v0.5.36
// 

package me.zeroeightsix.kami.module.modules.combat;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import me.zeroeightsix.kami.KamiMod;
import net.minecraft.world.World;
import net.minecraft.network.play.server.SPacketEntityStatus;
import java.util.function.Predicate;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import me.zeroeightsix.kami.event.events.TotemPopEvent;
import me.zero.alpine.listener.Listener;
import java.util.HashMap;
import me.zeroeightsix.kami.module.Module;

@Module.Info(name = "TotemPopCounter", description = "Counts the times your enemy pops", category = Module.Category.COMBAT)
public class PopCounter extends Module
{
    private HashMap<String, Integer> popList;
    @EventHandler
    public Listener<TotemPopEvent> totemPopEvent;
    @EventHandler
    public Listener<PacketEvent.Receive> totemPopListener;

    public PopCounter() {
        this.popList = new HashMap<String, Integer>();
        final int[] popCounter = new int[1];
        final int[] newPopCounter = new int[1];
        this.totemPopEvent = new Listener<TotemPopEvent>(event -> {
            if (this.popList == null) {
                this.popList = new HashMap<String, Integer>();
            }
            if (this.popList.get(event.getEntity().getName()) == null) {
                this.popList.put(event.getEntity().getName(), 1);
                Command.sendChatMessage("&d" + event.getEntity().getName() + " popped " + 1 + " totem!");
            }
            else if (this.popList.get(event.getEntity().getName()) != null) {
                popCounter[0] = this.popList.get(event.getEntity().getName());
                newPopCounter[0] = ++popCounter[0];
                this.popList.put(event.getEntity().getName(), newPopCounter[0]);
                Command.sendChatMessage("&d" + event.getEntity().getName() + " popped " + newPopCounter[0] + " totems!");
            }
            return;
        }, (Predicate<TotemPopEvent>[])new Predicate[0]);
        final SPacketEntityStatus[] packet = new SPacketEntityStatus[1];
        final Entity[] entity = new Entity[1];
        this.totemPopListener = new Listener<PacketEvent.Receive>(event -> {
            if (PopCounter.mc.world != null && PopCounter.mc.player != null) {
                if (event.getPacket() instanceof SPacketEntityStatus) {
                    packet[0] = (SPacketEntityStatus)event.getPacket();
                    if (packet[0].getOpCode() == 35) {
                        entity[0] = packet[0].getEntity((World) PopCounter.mc.world);
                        KamiMod.EVENT_BUS.post(new TotemPopEvent(entity[0]));
                    }
                }
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }

    @Override
    public void onUpdate() {
        for (final EntityPlayer player : PopCounter.mc.world.playerEntities) {
            if (player.getHealth() <= 0.0f && this.popList.containsKey(player.getName())) {
                Command.sendChatMessage("&d" + player.getName() + " died after popping " + this.popList.get(player.getName()) + " totems!");
                this.popList.remove(player.getName(), this.popList.get(player.getName()));
            }
        }
    }
}
