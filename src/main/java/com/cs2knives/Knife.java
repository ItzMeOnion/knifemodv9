package com.cs2knives;

import java.util.List;
import net.minecraft.resources.ResourceLocation;

/**
 * One knife model from the CS2 knife pack.
 * {@code id} matches the texture file name in assets/cs2knives/textures/gui/knife/.
 * {@code weight} is the relative drop chance inside a case.
 */
public record Knife(String id, String name, Grade grade, int weight) {

    public enum Grade {
        EXTRAORDINARY("Extraordinary", 0xFFE4AE33),
        RARE("Rare Special", 0xFF8847FF);

        public final String label;
        public final int color;

        Grade(String label, int color) {
            this.label = label;
            this.color = color;
        }
    }

    public static final List<Knife> ALL = List.of(
            new Knife("karambit", "Karambit", Grade.EXTRAORDINARY, 1),
            new Knife("butterfly", "Butterfly Knife", Grade.EXTRAORDINARY, 1),
            new Knife("m9", "M9 Bayonet", Grade.EXTRAORDINARY, 1),
            new Knife("talon", "Talon Knife", Grade.EXTRAORDINARY, 1),
            new Knife("skeleton", "Skeleton Knife", Grade.EXTRAORDINARY, 2),
            new Knife("bayonet", "Bayonet", Grade.RARE, 3),
            new Knife("css", "Classic Knife", Grade.RARE, 3),
            new Knife("stiletto", "Stiletto Knife", Grade.RARE, 3),
            new Knife("tactical", "Huntsman Knife", Grade.RARE, 3),
            new Knife("push", "Shadow Daggers", Grade.RARE, 3)
    );

    public ResourceLocation texture() {
        return ResourceLocation.fromNamespaceAndPath(
                Cs2KnivesClient.MOD_ID, "textures/gui/knife/" + id + ".png");
    }

    public static Knife byId(String id) {
        for (Knife k : ALL) {
            if (k.id.equals(id)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Unknown knife: " + id);
    }
}
