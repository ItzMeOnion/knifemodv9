package com.cs2knives.screen;

import com.cs2knives.Inventory;
import com.cs2knives.Knife;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Shows one copy of a knife large, slowly turning, with its stats. */
public class InspectScreen extends Screen {

    private static final int ICON = 168;
    private static final DateTimeFormatter DATE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault());

    private final Screen parent;
    private final Knife knife;
    private final long openedAt = System.currentTimeMillis();
    private List<Inventory.Item> copies;
    private int copy;

    public InspectScreen(Screen parent, Knife knife, int copy) {
        super(Component.literal("Inspect"));
        this.parent = parent;
        this.knife = knife;
        this.copy = copy;
    }

    @Override
    protected void init() {
        copies = Inventory.copiesOf(knife.id());
        if (copies.isEmpty()) {
            minecraft.setScreen(parent);
            return;
        }
        copy = Math.min(copy, copies.size() - 1);

        int cx = width / 2;
        int y = height / 2 + 100;
        Button prev = addRenderableWidget(Button.builder(Component.literal("< Copy"),
                b -> { copy = (copy - 1 + copies.size()) % copies.size(); })
                .bounds(cx - 150, y, 90, 20).build());
        Button next = addRenderableWidget(Button.builder(Component.literal("Copy >"),
                b -> { copy = (copy + 1) % copies.size(); })
                .bounds(cx + 60, y, 90, 20).build());
        prev.active = copies.size() > 1;
        next.active = copies.size() > 1;
        addRenderableWidget(Button.builder(Component.literal("Back"),
                b -> minecraft.setScreen(parent))
                .bounds(cx - 40, y + 26, 80, 20).build());
    }

    @Override
    protected void renderBackground(GuiGraphics g, int mx, int my, float partialTick) {
        g.fill(0, 0, width, height, Ui.BG);
        int cx = width / 2;
        int cy = height / 2 - 20;
        // Soft grade-coloured glow behind the knife.
        int glow = (0x30 << 24) | (knife.grade().color & 0xFFFFFF);
        g.fill(cx - 130, cy - 110, cx + 130, cy + 110, glow);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        super.render(g, mx, my, partialTick);
        if (copies == null || copies.isEmpty()) {
            return;
        }
        Inventory.Item item = copies.get(copy);
        int cx = width / 2;
        int cy = height / 2 - 20;

        // Fake a turntable spin: squash the icon width with a cosine.
        double t = (System.currentTimeMillis() - openedAt) / 1000.0;
        double spin = Math.cos(t * 1.4);
        int w = Math.max(6, (int) Math.round(ICON * Math.abs(spin)));
        int tilt = (int) Math.round(Math.max(-8, Math.min(8, (my - cy) * 0.03)));
        Ui.icon(g, knife, cx - w / 2, cy - ICON / 2 + tilt, w, ICON);

        // Name and grade under the knife.
        Ui.centered(g, font, knife.name(), cx, cy + ICON / 2 + 10, Ui.TEXT);
        Ui.centered(g, font, knife.grade().label, cx, cy + ICON / 2 + 22, knife.grade().color);

        // Stats panel on the right.
        int px = cx + 170;
        int py = height / 2 - 90;
        Ui.panel(g, px, py, 190, 170, knife.grade().color);
        g.drawString(font, "STATS", px + 12, py + 10, Ui.GOLD, true);

        g.drawString(font, "Wear", px + 12, py + 30, Ui.MUTED, false);
        g.drawString(font, Inventory.wearName(item.wear), px + 12, py + 42, Ui.TEXT, false);

        g.drawString(font, "Float  " + String.format("%.4f", item.wear), px + 12, py + 62, Ui.MUTED, false);
        int barX = px + 12;
        int barY = py + 76;
        g.fill(barX, barY, barX + 166, barY + 6, 0xFF2A313C);
        g.fill(barX, barY, barX + (int) (166 * item.wear), barY + 6, knife.grade().color);

        g.drawString(font, "Pattern  #" + item.seed, px + 12, py + 92, Ui.MUTED, false);
        g.drawString(font, "Copy  " + (copy + 1) + " of " + copies.size(), px + 12, py + 110, Ui.MUTED, false);
        g.drawString(font, "Obtained  " + DATE.format(Instant.ofEpochMilli(item.obtained)),
                px + 12, py + 128, Ui.MUTED, false);
        g.drawString(font, "Client-side only", px + 12, py + 150, 0xFF5C6573, false);
    }
}
