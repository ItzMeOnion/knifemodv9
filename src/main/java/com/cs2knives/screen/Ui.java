package com.cs2knives.screen;

import com.cs2knives.Knife;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;

/** Small drawing helpers shared by every screen. */
public final class Ui {

    public static final int BG = 0xFF0A0D12;
    public static final int PANEL = 0xE6141A22;
    public static final int GOLD = 0xFFE4AE33;
    public static final int TEXT = 0xFFE6E9EE;
    public static final int MUTED = 0xFF8A94A3;
    public static final int ICON_TEX_SIZE = 64;

    private Ui() {
    }

    /** Draws a knife icon, stretched to w x h (used for the spin illusion). */
    public static void icon(GuiGraphics g, Knife knife, int x, int y, int w, int h) {
        if (w <= 0 || h <= 0) {
            return;
        }
        g.blit(RenderPipelines.GUI_TEXTURED, knife.texture(), x, y, 0, 0, w, h,
                ICON_TEX_SIZE, ICON_TEX_SIZE);
    }

    /** Filled panel with a 1 px coloured border. */
    public static void panel(GuiGraphics g, int x, int y, int w, int h, int border) {
        g.fill(x, y, x + w, y + h, border);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, PANEL);
    }

    public static void centered(GuiGraphics g, Font font, String text, int cx, int y, int color) {
        g.drawString(font, text, cx - font.width(text) / 2, y, color, true);
    }

    public static boolean hovered(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }

    /** Cheap cubic ease-out used by the case roulette. */
    public static double easeOutCubic(double t) {
        double inv = 1.0 - Math.min(1.0, Math.max(0.0, t));
        return 1.0 - inv * inv * inv;
    }
}
