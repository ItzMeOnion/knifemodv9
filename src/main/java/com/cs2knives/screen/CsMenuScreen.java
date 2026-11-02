package com.cs2knives.screen;

import com.cs2knives.Inventory;
import com.cs2knives.Knife;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class CsMenuScreen extends Screen {

    private String status = "";

    public CsMenuScreen() {
        super(Component.literal("CS2 Knives"));
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 - 30;
        addRenderableWidget(Button.builder(Component.literal("Inventory"),
                b -> minecraft.setScreen(new InventoryScreen(this)))
                .bounds(cx - 110, y, 220, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Open Cases"),
                b -> minecraft.setScreen(new CasesScreen(this)))
                .bounds(cx - 110, y + 26, 220, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Grant one of every knife"),
                b -> {
                    Inventory.grantAll();
                    status = "Granted one of every knife.";
                })
                .bounds(cx - 110, y + 52, 220, 20).build());
        addRenderableWidget(Button.builder(Component.literal("Close"),
                b -> onClose())
                .bounds(cx - 110, y + 78, 220, 20).build());
    }

    @Override
    protected void renderBackground(GuiGraphics g, int mx, int my, float partialTick) {
        g.fill(0, 0, width, height, Ui.BG);
        // Thin gold rule, a simple accent that matches the case screens.
        g.fill(0, height / 2 - 62, width, height / 2 - 61, (0x40 << 24) | (Ui.GOLD & 0xFFFFFF));
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        super.render(g, mx, my, partialTick);

        int cx = width / 2;
        g.drawCenteredString(font, Component.literal("CS2 KNIVES"), cx, height / 2 - 90, Ui.GOLD);
        Ui.centered(g, font, "Press K to open this menu", cx, height / 2 - 78, Ui.MUTED);

        int unique = 0;
        int copies = 0;
        for (Knife k : Knife.ALL) {
            int n = Inventory.count(k.id());
            if (n > 0) {
                unique++;
            }
            copies += n;
        }
        Ui.centered(g, font, "Owned: " + unique + " / " + Knife.ALL.size()
                + " knives  (" + copies + " total)", cx, height / 2 + 108, Ui.TEXT);
        if (!status.isEmpty()) {
            Ui.centered(g, font, status, cx, height / 2 + 124, Ui.GOLD);
        }
    }
}
