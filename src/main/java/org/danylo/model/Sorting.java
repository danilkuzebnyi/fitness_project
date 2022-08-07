package org.danylo.model;

public enum Sorting {
    DEFAULT(""),
    EXPERIENCE("EXPERIENCE"),
    RATING("RATING"),
    SMALL_PRICE("SMALL_PRICE"),
    BIG_PRICE("BIG_PRICE");

    private final String sorting;

    Sorting(String sorting) {
        this.sorting = sorting;
    }

    public String getSorting() {
        return sorting;
    }

    public static Sorting fromValue(String value) {
        for (Sorting sorting : values()) {
            if (sorting.getSorting().equalsIgnoreCase(value)) {
                return sorting;
            }
        }
        return DEFAULT;
    }
}
