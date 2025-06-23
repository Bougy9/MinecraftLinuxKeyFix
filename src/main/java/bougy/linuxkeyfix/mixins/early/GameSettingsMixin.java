package bougy.linuxkeyfix.mixins.early;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameSettings.class)
public class GameSettingsMixin {

    @Inject(method = "getKeyDisplayString", at = @At("HEAD"), cancellable = true)
    private static void getKeyDisplayString(int key, CallbackInfoReturnable<String> ci){
       // MyMod.LOG.info("TEST MIXIN: {}", key);
        String value;

        if (key < 0) {
            value = I18n.format("key.mouseButton", key + 101);
        }
        else if (key < 256) {
            value = Keyboard.getKeyName(key);
        }
        else {
            value =  String.format("%c", (char)(key - 256)).toUpperCase();
        }

        ci.setReturnValue(value);
    }
}
