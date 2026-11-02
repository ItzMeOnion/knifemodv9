package com.cs2knives.screen;

import com.cs2knives.Inventory;
import com.cs2knives.Knife;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Lists every knife you own, one card per knife with a copy count. */
public class InventoryScreen extends Screen {

    private static final int CARD_W = 116;
    private static final int CARD_H = 124;
    private static final int GAP = 10;
    private static final int TOP = 44;

    private final Screen parent;
    private List<Knife> owned = List.of();
    private int cols;
    private int rows;
    private int page;
    private Button prev;
    private Button next;

    public InventoryScreen(Screen parent) {
        super(Component.literal("Inventory"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        owned = Knife.ALL.stream().filter(k -> Inventory.count(k.id()) > 0).toList();
        cols = Math.max(1, Math.min(5, (width - 40 + GAP) / (CARD_W + GAP)));
        rows = Math.max(1, (height - TOP - 70) / (CARD_H + GAP));
        int perPage = cols * rows;
        int pages = Math.max(1, (owned.size() + perPage - 1) / perPage);
        page = Math.min(page, pages - 1);

        int gridW = cols * CARD_W + (cols - 1) * GAP;
        int x0 = width / 2 - gridW / 2;
        int from = page * perPage;
        for (int i = 0; i < perPage && from + i < owned.size(); i++) {
            Knife knife = owned.get(from + i);
            int x = x0 + (i % cols) * (CARD_W + GAP);
            int y = TOP + (i / cols) * (CARD_H + GAP);
            addRenderableWidget(Button.builder(Component.empty(),
                    b -> minecraft.setScreen(new InspectScreen(this, knife, 0)))
                    .bounds(x, y, CARD_W, CARD_H).build());
        }

        prev = addRenderableWidget(Button.builder(Component.literal("< Prev"),
                b -> { page--; rebuild(); })
                .bounds(width / 2 - 150, height - 40, 80, 20).build());
        prev.active = page > 0;
        next = addRenderableWidget(Button.builder(Component.literal("Next >"),
                b -> { page++; rebuild(); })
                .bounds(width / 2 + 70, height - 40, 80, 20).build());
        next.active = page < pages - 1;
        addRenderableWidget(Button.builder(Component.literal("Back"),
                b -> minecraft.setScreen(parent))
                .bounds(width / 2 - 40, height - 40, 80, 20).build());
    }

    private void rebuild() {
        clearWidgets();
        init();
    }

    @Override
    protected void renderBackground(GuiGraphics g, int mx, int my, float partialTick) {
        g.fill(0, 0, width, height, Ui.BG);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        super.render(g, mx, my, partialTick);

        int total = 0;
        for (Knife k : owned) {
            total += Inventory.count(k.id());
        }
        g.drawCenteredString(font, Component.literal("INVENTORY"), width / 2, 12, Ui.GOLD);
        Ui.centered(g, font, owned.size() + " unique knives  |  " + total + " total",
                width / 2, 26, Ui.MUTED);

        if (owned.isEmpty()) {
            Ui.centered(g, font, "No knives yet. Open some cases!", width / 2, height / 2 - 4, Ui.TEXT);
            return;
        }

        int gridW = cols * CARD_W + (cols - 1) * GAP;
        int x0 = width / 2 - gridW / 2;
        int perPage = cols * rows;
        int from = page * perPage;
        for (int i = 0; i < perPage && from + i < owned.size(); i++) {
            Knife knife = owned.get(from + i);
            int x = x0 + (i % cols) * (CARD_W + GAP);
            int y = TOP + (i / cols) * (CARD_H + GAP);
            boolean hover = Ui.hovered(mx, my, x, y, CARD_W, CARD_H);

            Ui.panel(g, x, y, CARD_W, CARD_H, hover ? Ui.GOLD : knife.grade().color);
            Ui.icon(g, knife, x + (CARD_W - 76) / 2, y + 8, 76, 76);
            Ui.centered(g, font, knife.name(), x + CARD_W / 2, y + 92, Ui.TEXT);
            Ui.centered(g, font, knife.grade().label, x + CARD_W / 2, y + 104, knife.grade().color);
            Ui.centered(g, font, "x" + Inventory.count(knife.id()), x + CARD_W / 2, y + 112, Ui.GOLD);
        }

        int pages = Math.max(1, (owned.size() + perPage - 1) / perPage);
        Ui.centered(g, font, "Page " + (page + 1) + " / " + pages, width / 2, height - 36, Ui.MUTED);
    }
}
