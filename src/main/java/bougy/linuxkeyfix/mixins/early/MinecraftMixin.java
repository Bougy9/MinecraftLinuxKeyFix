package bougy.linuxkeyfix.mixins.early;

import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Invoker("updateDebugProfilerName")
    public abstract void updateDebugProfilerName(int key);

    @Accessor
    public abstract long getField_83002_am();

    @Accessor("field_83002_am")
    public abstract void setField_83002_am(long value);

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Keyboard;next()Z", remap = false))
    private boolean keyboardCheck(){
        Minecraft minecraft = (Minecraft)(Object)this;
        //MyMod.LOG.info("TEST! ");
        boolean flag;
        while (Keyboard.next()) {
            int keyVal = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
            KeyBinding.setKeyBindState(keyVal, Keyboard.getEventKeyState());
            //MyMod.LOG.info("TEST! {}", keyVal);
            if (Keyboard.getEventKeyState()) {
                KeyBinding.onTick(keyVal);
            }

            if (getField_83002_am() > 0L) {
                if (Minecraft.getSystemTime() - getField_83002_am() >= 6000L) {
                    throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                }

                if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                    this.setField_83002_am(-1L);
                }
            }
            else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                this.setField_83002_am(Minecraft.getSystemTime());
            }

            minecraft.func_152348_aa();
            if (Keyboard.getEventKeyState()) {
                if (keyVal == 62 && minecraft.entityRenderer != null) {
                    minecraft.entityRenderer.deactivateShader();
                }

                if (minecraft.currentScreen != null) {
                    minecraft.currentScreen.handleKeyboardInput();
                }
                else {
                    if (keyVal == 1) {
                        minecraft.displayInGameMenu();
                    }

                    if (keyVal == 31 && Keyboard.isKeyDown(61)) {
                        minecraft.refreshResources();
                    }

                    if (keyVal == 20 && Keyboard.isKeyDown(61)) {
                        minecraft.refreshResources();
                    }

                    if (keyVal == 33 && Keyboard.isKeyDown(61)) {
                        flag = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                        minecraft.gameSettings.setOptionValue(GameSettings.Options.RENDER_DISTANCE, flag ? -1 : 1);
                    }

                    if (keyVal == 30 && Keyboard.isKeyDown(61)) {
                        minecraft.renderGlobal.loadRenderers();
                    }

                    if (keyVal == 35 && Keyboard.isKeyDown(61)) {
                        minecraft.gameSettings.advancedItemTooltips = !minecraft.gameSettings.advancedItemTooltips;
                        minecraft.gameSettings.saveOptions();
                    }

                    if (keyVal == 48 && Keyboard.isKeyDown(61)) {
                        RenderManager.debugBoundingBox = !RenderManager.debugBoundingBox;
                    }

                    if (keyVal == 25 && Keyboard.isKeyDown(61)) {
                        minecraft.gameSettings.pauseOnLostFocus = !minecraft.gameSettings.pauseOnLostFocus;
                        minecraft.gameSettings.saveOptions();
                    }

                    if (keyVal == 59) {
                        minecraft.gameSettings.hideGUI = !minecraft.gameSettings.hideGUI;
                    }

                    if (keyVal == 61) {
                        minecraft.gameSettings.showDebugInfo = !minecraft.gameSettings.showDebugInfo;
                        minecraft.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                    }

                    if (minecraft.gameSettings.keyBindTogglePerspective.isPressed()) {
                        ++minecraft.gameSettings.thirdPersonView;

                        if (minecraft.gameSettings.thirdPersonView > 2) {
                            minecraft.gameSettings.thirdPersonView = 0;
                        }
                    }

                    if (minecraft.gameSettings.keyBindSmoothCamera.isPressed()) {
                        minecraft.gameSettings.smoothCamera = !minecraft.gameSettings.smoothCamera;
                    }
                }

                if (minecraft.gameSettings.showDebugInfo && minecraft.gameSettings.showDebugProfilerChart) {
                    if (keyVal == 11) {
                        this.updateDebugProfilerName(0);
                    }

                    for (int j = 0; j < 9; ++j) {
                        if (keyVal == 2 + j) {
                            this.updateDebugProfilerName(j + 1);
                        }
                    }
                }
            }
            FMLCommonHandler.instance().fireKeyInput();
        }

        return false;
    }

    @ModifyVariable(method = "func_152348_aa", at = @At(value = "STORE", ordinal = 0, remap = false))
    public int dispatchKeypresses(int original){
        return Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() : Keyboard.getEventKey();
    }
}
