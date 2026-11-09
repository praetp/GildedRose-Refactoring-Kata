package com.gildedrose;

class GildedRose {
    Item[] items;

    public GildedRose(Item[] items) {
        this.items = items;
    }

    /**
     * Ages every item by one day, applying the rule of its {@link ItemCategory}.
     *
     * <p>Contract: every item is non-null, has a non-null name and a quality of 0 to 50, except legendary items
     * (Sulfuras), whose quality is exactly 80. All items are checked before any is changed, so if one breaks the
     * contract, no item is updated.
     *
     * @throws NullPointerException if an item or its name is null
     * @throws IllegalArgumentException if an item's quality breaks the contract
     */
    public void updateQuality() {
        for (Item item : items) {
            ItemCategory.of(item).validate(item);
        }
        for (Item item : items) {
            ItemCategory.of(item).update(item);
        }
    }
}
