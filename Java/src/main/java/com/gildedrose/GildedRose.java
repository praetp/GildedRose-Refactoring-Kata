package com.gildedrose;

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    /**
     * Ages every item by one day, applying the rule of its {@link ItemCategory}.
     *
     * <p>Contract: every item has a non-null name and a quality of 0 to 50, except legendary items (Sulfuras), whose
     * quality is 80. The result for items outside this contract is undefined.
     */
    public void updateQuality() {
        for (Item item : items) {
            ItemCategory.of(item).update(item);
        }
    }
}
