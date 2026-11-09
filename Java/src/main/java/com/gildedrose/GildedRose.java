package com.gildedrose;

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    /** Ages every item by one day, applying the rule of its {@link ItemCategory}. */
    public void updateQuality() {
        for (Item item : items) {
            ItemCategory.of(item).update(item);
        }
    }
}
