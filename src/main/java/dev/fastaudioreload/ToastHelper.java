package dev.fastaudioreload;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
//? if <1.19 {
/*import net.minecraft.network.chat.TranslatableComponent;
*///?}

public final class ToastHelper {
    private ToastHelper() {
    }

    public static void showReloaded(Minecraft minecraft, long elapsedMs) {
        //? if >=1.19 {
        Component title = Component.translatable("fastaudioreload.toast.title");
        Component subtitle = Component.translatable("fastaudioreload.toast.duration", elapsedMs);
        //?} else {
        /*Component title = new TranslatableComponent("fastaudioreload.toast.title");
        Component subtitle = new TranslatableComponent("fastaudioreload.toast.duration", elapsedMs);
        *///?}

        //? if >=26.2 {
        SystemToast.addOrUpdate(minecraft.gui.toastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, subtitle);
        //?} else if >=1.21.2 {
        /*SystemToast.addOrUpdate(minecraft.getToastManager(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, subtitle);
        *///?} else if >=1.20.3 {
        /*SystemToast.addOrUpdate(minecraft.getToasts(), SystemToast.SystemToastId.PERIODIC_NOTIFICATION, title, subtitle);
        *///?} else {
        /*SystemToast.addOrUpdate(minecraft.getToasts(), SystemToast.SystemToastIds.TUTORIAL_HINT, title, subtitle);
        *///?}
    }
}
