package com.cs2knives;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import net.fabricmc.loader.api.FabricLoader;

/**
 * Client-side knife inventory, saved to config/cs2knives/inventory.json.
 * Nothing here is sent to a server.
 */
public final class Inventory {

    /** One physical knife you own. */
    public static final class Item {
        public String knife;
        public float wear;   // 0.0 (Factory New) .. 1.0 (Battle-Scarred)
        public int seed;     // cosmetic "pattern" number
        public long obtained;

        public Item() {
        }

        Item(String knife, float wear, int seed, long obtained) {
            this.knife = knife;
            this.wear = wear;
            this.seed = seed;
            this.obtained = obtained;
        }
    }

    private static final class Data {
        List<Item> items = new ArrayList<>();
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir()
            .resolve("cs2knives").resolve("inventory.json");
    private static final Random RNG = new Random();
    private static final List<Item> ITEMS = new ArrayList<>();
    private static boolean loaded;

    private Inventory() {
    }

    public static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }
        loaded = true;
        if (!Files.exists(FILE)) {
            return;
        }
        try {
            Data data = GSON.fromJson(Files.readString(FILE), Data.class);
            if (data != null && data.items != null) {
                ITEMS.addAll(data.items);
            }
        } catch (Exception e) {
            Cs2KnivesClient.LOGGER.error("Could not read {}, starting empty", FILE, e);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Data data = new Data();
            data.items = ITEMS;
            Files.writeString(FILE, GSON.toJson(data));
        } catch (IOException e) {
            Cs2KnivesClient.LOGGER.error("Could not save inventory", e);
        }
    }

    public static synchronized List<Item> all() {
        ensureLoaded();
        return Collections.unmodifiableList(new ArrayList<>(ITEMS));
    }

    public static synchronized int count(String knifeId) {
        ensureLoaded();
        int n = 0;
        for (Item item : ITEMS) {
            if (item.knife.equals(knifeId)) {
                n++;
            }
        }
        return n;
    }

    public static synchronized List<Item> copiesOf(String knifeId) {
        ensureLoaded();
        List<Item> copies = new ArrayList<>();
        for (Item item : ITEMS) {
            if (item.knife.equals(knifeId)) {
                copies.add(item);
            }
        }
        return copies;
    }

    public static synchronized Item add(String knifeId) {
        ensureLoaded();
        Item item = new Item(knifeId, RNG.nextFloat(), RNG.nextInt(1000), System.currentTimeMillis());
        ITEMS.add(item);
        save();
        return item;
    }

    /** Gives one copy of every knife. */
    public static synchronized void grantAll() {
        ensureLoaded();
        for (Knife knife : Knife.ALL) {
            add(knife.id());
        }
    }

    /** Picks a knife from the case using each knife's weight. */
    public static Knife roll(CaseDef caseDef, Random random) {
        List<Knife> pool = caseDef.knives();
        int pick = random.nextInt(caseDef.totalWeight());
        for (Knife knife : pool) {
            pick -= knife.weight();
            if (pick < 0) {
                return knife;
            }
        }
        return pool.get(0);
    }

    public static Random random() {
        return RNG;
    }

    public static String wearName(float wear) {
        if (wear < 0.07f) return "Factory New";
        if (wear < 0.15f) return "Minimal Wear";
        if (wear < 0.38f) return "Field-Tested";
        if (wear < 0.45f) return "Well-Worn";
        return "Battle-Scarred";
    }
}
