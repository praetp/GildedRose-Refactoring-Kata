package com.gildedrose;

import java.util.Objects;

/**
 * The kinds of items the inn sells, each with its own daily update rule. An item's category follows from its name,
 * so {@link Item} itself stays untouched.
 */
enum ItemCategory {

    /** Loses quality every day, twice as fast once the sell-by date has passed. */
    NORMAL {
        @Override
        void update(Item item) {
            ageOneDay(item);
            decreaseQuality(item, isPastSellByDate(item) ? 2 : 1);
        }
    },

    /** Gains quality as it ages, twice as fast once the sell-by date has passed. */
    AGED_BRIE {
        @Override
        void update(Item item) {
            ageOneDay(item);
            increaseQuality(item, isPastSellByDate(item) ? 2 : 1);
        }
    },

    /** Gains quality as the concert approaches, and becomes worthless once it has taken place. */
    BACKSTAGE_PASS {
        @Override
        void update(Item item) {
            int daysUntilConcert = item.sellIn;
            ageOneDay(item);
            if (isPastSellByDate(item)) {
                item.quality = 0;
            } else if (daysUntilConcert <= 5) {
                increaseQuality(item, 3);
            } else if (daysUntilConcert <= 10) {
                increaseQuality(item, 2);
            } else {
                increaseQuality(item, 1);
            }
        }
    },

    /** Never has to be sold and never changes in quality. */
    LEGENDARY {
        @Override
        void update(Item item) {
            // Legendary items never change.
        }

        @Override
        void validate(Item item) {
            if (item.quality != LEGENDARY_QUALITY) {
                throw new IllegalArgumentException(
                        "Quality of a legendary item must be " + LEGENDARY_QUALITY + ": " + item);
            }
        }
    };

    private static final int MIN_QUALITY = 0;
    private static final int MAX_QUALITY = 50;
    private static final int LEGENDARY_QUALITY = 80;

    /** Applies one day of this category's rule to the item. */
    abstract void update(Item item);

    /**
     * Checks that the item's quality is valid for this category: 0 to 50, unless a category overrides this.
     *
     * @throws IllegalArgumentException if it is not
     */
    void validate(Item item) {
        if (item.quality < MIN_QUALITY || item.quality > MAX_QUALITY) {
            throw new IllegalArgumentException(
                    "Quality must be " + MIN_QUALITY + " to " + MAX_QUALITY + ": " + item);
        }
    }

    /**
     * Returns the category of the item, based on its name.
     *
     * @throws NullPointerException if the item or its name is null
     */
    static ItemCategory of(Item item) {
        Objects.requireNonNull(item, "item must not be null");
        Objects.requireNonNull(item.name, () -> "name must not be null: " + item);
        return switch (item.name) {
            case "Aged Brie" -> AGED_BRIE;
            case "Backstage passes to a TAFKAL80ETC concert" -> BACKSTAGE_PASS;
            case "Sulfuras, Hand of Ragnaros" -> LEGENDARY;
            default -> NORMAL;
        };
    }

    private static void ageOneDay(Item item) {
        item.sellIn--;
    }

    private static boolean isPastSellByDate(Item item) {
        return item.sellIn < 0;
    }

    /** Raises quality by {@code amount}, but not above {@link #MAX_QUALITY}. */
    private static void increaseQuality(Item item, int amount) {
        item.quality = Math.min(MAX_QUALITY, item.quality + amount);
    }

    /** Lowers quality by {@code amount}, but not below {@link #MIN_QUALITY}. */
    private static void decreaseQuality(Item item, int amount) {
        item.quality = Math.max(MIN_QUALITY, item.quality - amount);
    }
}
