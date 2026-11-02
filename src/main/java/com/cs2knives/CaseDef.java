package com.cs2knives;

import java.util.List;

/** A openable case. Drop odds come from each knife's weight inside the case. */
public record CaseDef(String id, String name, List<String> knifeIds) {

    public static final List<CaseDef> ALL = List.of(
            new CaseDef("gamma", "Gamma Case", List.of(
                    "karambit", "butterfly", "m9", "talon", "skeleton",
                    "bayonet", "css", "stiletto", "tactical", "push")),
            new CaseDef("shadow", "Shadow Case", List.of(
                    "bayonet", "css", "stiletto", "tactical", "push"))
    );

    public List<Knife> knives() {
        return knifeIds.stream().map(Knife::byId).toList();
    }

    public int totalWeight() {
        return knives().stream().mapToInt(Knife::weight).sum();
    }
}
