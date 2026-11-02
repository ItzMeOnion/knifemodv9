package com.cs2knives.screen;

import com.cs2knives.CaseDef;
import com.cs2knives.Inventory;
import com.cs2knives.Knife;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Pick a case, press Open, watch the roulette, and get a knife added to your inventory. */
public class CasesScreen extends Screen {

    private static final int CELL = 86;
    private static final int STRIP_LEN = 60;
    private static final int WINNER_INDEX = 48;
    private static final long SPIN_MS = 4500;

    private final Screen parent;
    private final Random random = new Random();
    private CaseDef selected = CaseDef.ALL.get(0);
    private Button openButton;
    private Button inspectButton;

    private long spinStart = -1;
    private List<String> strip = List.of();
    private Knife result;
    private Inventory.Item resultItem;

    public CasesScreen(Screen parent) {
        super(Component.literal("Cases"));
        this.parent = parent;
    }

    private int panelX() {
        return 180;
    }

    @Override
    protected void init() {
        int y = 60;
        for (int i = 0; i < CaseDef.ALL.size(); i++) {
            CaseDef c = CaseDef.ALL.get(i);
            addRenderableWidget(Button.builder(Component.literal(c.name()),
                    b -> {
                        if (!spinning()) {
                            selected = c;
                            result = null;
                        }
                    })
                    .bounds(20, y + i * 26, 140, 20).build());
        }

        openButton = addRenderableWidget(Button.builder(Component.literal("Open case"),
                b -> startOpening())
                .bounds(panelX() + 20, height - 60, 120, 20).build());
        inspectButton = addRenderableWidget(Button.builder(Component.literal("Inspect result"),
                b -> minecraft.setScreen(new InspectScreen(this, result,
                        Math.max(0, Inventory.copiesOf(result.id()).size() - 1))))
                .bounds(panelX() + 150, height - 60, 120, 20).build());
        inspectButton.visible = false;

        addRenderableWidget(Button.builder(Component.literal("Back"),
                b -> minecraft.setScreen(parent))
                .bounds(20, height - 30, 80, 20).build());
    }

    private boolean spinning() {
        return spinStart >= 0 && System.currentTimeMillis() - spinStart < SPIN_MS;
    }

    private void startOpening() {
        if (spinning()) {
            return;
        }
        result = Inventory.roll(selected, random);
        resultItem = Inventory.add(result.id());

        List<String> reel = new ArrayList<>(STRIP_LEN);
        List<Knife> pool = selected.knives();
        for (int i = 0; i < STRIP_LEN; i++) {
            reel.add(pool.get(random.nextInt(pool.size())).id());
        }
        reel.set(WINNER_INDEX, result.id());
        strip = reel;
        spinStart = System.currentTimeMillis();
    }

    @Override
    protected void renderBackground(GuiGraphics g, int mx, int my, float partialTick) {
        g.fill(0, 0, width, height, Ui.BG);
    }

    @Override
    public void render(GuiGraphics g, int mx, int my, float partialTick) {
        super.render(g, mx, my, partialTick);

        g.drawCenteredString(font, Component.literal("CASES"), width / 2, 12, Ui.GOLD);
        g.drawString(font, selected.name(), 20, 40, Ui.TEXT, true);

        int px = panelX();
        int pw = width - px - 20;
        int winY = 50;
        int winH = 96;
        Ui.panel(g, px, winY, pw, winH + 20, Ui.GOLD);

        // Reel window
        int winX = px + 10;
        int winW = pw - 20;
        int reelY = winY + 10;
        boolean spin = spinning();
        if (!strip.isEmpty()) {
            double t = spin ? (System.currentTimeMillis() - spinStart) / (double) SPIN_MS : 1.0;
            double offset = Ui.easeOutCubic(t) * WINNER_INDEX * CELL;
            g.enableScissor(winX, reelY, winX + winW, reelY + winH);
            for (int i = 0; i < strip.size(); i++) {
                int cellX = winX + (winW - CELL) / 2 + i * CELL - (int) Math.round(offset);
                if (cellX + CELL < winX || cellX > winX + winW) {
                    continue;
                }
                g.fill(cellX + 3, reelY + 3, cellX + CELL - 3, reelY + winH - 3, 0xFF1C232D);
                Ui.icon(g, Knife.byId(strip.get(i)), cellX + 11, reelY + 11, CELL - 22, CELL - 22);
            }
            g.disableScissor();
        }
        // Centre marker
        g.fill(winX + winW / 2 - 1, reelY - 4, winX + winW / 2 + 1, reelY + winH + 4, Ui.GOLD);

        openButton.active = !spin;
        inspectButton.visible = result != null && !spin;
        inspectButton.active = result != null && !spin;

        if (result != null && !spin) {
            Ui.centered(g, font, "You got: " + result.name() + "  (" + result.grade().label + ")",
                    px + pw / 2, winY + winH + 28, result.grade().color);
            Ui.centered(g, font, "Wear: " + Inventory.wearName(resultItem.wear)
                    + "  float " + String.format("%.4f", resultItem.wear),
                    px + pw / 2, winY + winH + 40, Ui.MUTED);
        }

        // Drop table
        int ty = winY + winH + 64;
        g.drawString(font, "Possible drops", px, ty, Ui.MUTED, false);
        int total = selected.totalWeight();
        List<Knife> knives = selected.knives();
        for (int i = 0; i < knives.size(); i++) {
            Knife k = knives.get(i);
            int col = i % 2;
            int row = i / 2;
            int x = px + col * (pw / 2);
            int y = ty + 16 + row * 30;
            Ui.icon(g, k, x, y, 26, 26);
            g.drawString(font, k.name(), x + 32, y + 2, k.grade().color, false);
            double pct = 100.0 * k.weight() / total;
            g.drawString(font, String.format("%.1f%%  owned: %d", pct, Inventory.count(k.id())),
                    x + 32, y + 14, Ui.MUTED, false);
        }
    }
}
