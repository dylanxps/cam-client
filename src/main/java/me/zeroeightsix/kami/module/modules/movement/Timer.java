package me.zeroeightsix.kami.module.modules.movement;
import me.zeroeightsix.kami.command.Command;
import me.zeroeightsix.kami.module.Module;
import me.zeroeightsix.kami.module.ModuleManager;
import me.zeroeightsix.kami.setting.Setting;
import me.zeroeightsix.kami.setting.Settings;




@Module.Info(name = "Timer", category = Module.Category.MOVEMENT, description = "timer. my nigga")
public class Timer extends Module {

    private int tickWait = 0;

    private Setting<Float> speedUsual = register(Settings.floatBuilder("Speed").withMinimum(0f).withMaximum(10f).withValue(4.2f));
    private Setting<Float> fastUsual = register(Settings.floatBuilder("Fast Speed").withMinimum(0f).withMaximum(200f).withValue(10f));
    private Setting<Float> tickToFast = register(Settings.floatBuilder("Tick To Fast").withMinimum(0f).withMaximum(20f).withValue(4f));
    private Setting<Float> tickToNoFast = register(Settings.floatBuilder("Tick To Disable Fast").withMinimum(0f).withMaximum(20f).withValue(7f));
    private Setting<Boolean> infoMessage = register(Settings.b("Info Message", false));

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50;
        if (infoMessage.getValue() && mc.world != null) {
            Command.sendChatMessage(" \u00A7eTimer \u00A7aOFF");
        }
    }

    @Override
    public void onUpdate() {
        if (tickWait == tickToFast.getValue()) {
            mc.timer.tickLength = 50.0f / fastUsual.getValue();
           // this.setHudInfo(fastUsual.getValue().toString());
        }
        if (tickWait >= tickToNoFast.getValue()) {
            tickWait = 0;
            mc.timer.tickLength = 50.0f / speedUsual.getValue();
           // this.setHudInfo(speedUsual.getValue().toString());
        }
        tickWait++;
    }
    @Override
    protected void onEnable() {
        if (infoMessage.getValue() && mc.world != null) {
            Command.sendChatMessage(" \u00A7eTimer \u00A7aON");
        }
    }}
