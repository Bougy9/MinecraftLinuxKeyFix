package bougy.linuxkeyfix.mixins.early;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiControls.class)
public abstract class GuiControlsMixin {

    @Accessor
    abstract GameSettings getOptions();

    @Inject(method = "keyTyped", at = @At("HEAD"), cancellable = true)
    public void keyTyped(char typedChar, int keyCode, CallbackInfo info){
        GuiControls controls = (GuiControls)(Object)this;
        //MyMod.LOG.info("keyTyped ! {} {} {} {}", typedChar, (int)typedChar, keyCode, Keyboard.getEventKeyState());

        if (controls.buttonId != null){
            if (keyCode == 1){
                this.getOptions().setOptionKeyBinding(controls.buttonId, 0);
            }
            else if (keyCode != 0) {
                this.getOptions().setOptionKeyBinding(controls.buttonId, keyCode);
            }
            else if (typedChar > '\u0000'){
                this.getOptions().setOptionKeyBinding(controls.buttonId, typedChar + 256);
            }

            controls.buttonId = null;
            controls.field_152177_g = Minecraft.getSystemTime();
            KeyBinding.resetKeyBindingArrayAndHash();
            info.cancel();
        }
    }
}
