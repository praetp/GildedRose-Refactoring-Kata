package com.gildedrose;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests that {@code updateQuality()} enforces its contract: every item is non-null with a non-null name, and has a
 * quality of 0 to 50, except legendary items (Sulfuras), whose quality is exactly 80. See ADR 0012.
 */
class GildedRoseContractTest {

    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";

    private static void update(Item... items) {
        new GildedRose(items).updateQuality();
    }

    @ParameterizedTest(name = "{0} with quality {1}")
    @DisplayName("rejects a quality outside 0..50")
    @CsvSource({
        "'+5 Dexterity Vest',                         -1",
        "'+5 Dexterity Vest',                         51",
        "'Aged Brie',                                 -1",
        "'Aged Brie',                                 51",
        "'Backstage passes to a TAFKAL80ETC concert', -1",
        "'Backstage passes to a TAFKAL80ETC concert', 51",
        "'Conjured Mana Cake',                        51",
    })
    void rejectsQualityOutsideRange(String name, int quality) {
        Item item = new Item(name, 5, quality);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> update(item));

        assertEquals("Quality must be 0 to 50: " + item, e.getMessage());
    }

    @ParameterizedTest(name = "{0} with quality {1}")
    @DisplayName("accepts the quality limits 0 and 50")
    @CsvSource({
        "'+5 Dexterity Vest', 0",
        "'+5 Dexterity Vest', 50",
        "'Aged Brie',         0",
        "'Aged Brie',         50",
    })
    void acceptsQualityLimits(String name, int quality) {
        assertDoesNotThrow(() -> update(new Item(name, 5, quality)));
    }

    @ParameterizedTest(name = "Sulfuras with quality {0}")
    @DisplayName("rejects a legendary item whose quality is not 80")
    @CsvSource({"79", "81", "50", "0"})
    void rejectsLegendaryQualityOtherThanEighty(int quality) {
        Item item = new Item(SULFURAS, 0, quality);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> update(item));

        assertEquals("Quality of a legendary item must be 80: " + item, e.getMessage());
    }

    @Test
    @DisplayName("rejects a null item")
    void rejectsNullItem() {
        NullPointerException e = assertThrows(NullPointerException.class, () -> update((Item) null));

        assertEquals("item must not be null", e.getMessage());
    }

    @Test
    @DisplayName("rejects an item without a name")
    void rejectsNullName() {
        Item item = new Item(null, 5, 10);

        NullPointerException e = assertThrows(NullPointerException.class, () -> update(item));

        assertEquals("name must not be null: " + item, e.getMessage());
    }

    @Test
    @DisplayName("changes no item when any item is invalid")
    void allOrNothing() {
        Item valid = new Item("+5 Dexterity Vest", 10, 20);
        Item invalid = new Item("Aged Brie", 5, 60);

        assertThrows(IllegalArgumentException.class, () -> update(valid, invalid));

        assertAll(
                () -> assertEquals(10, valid.sellIn, "sellIn of the valid item"),
                () -> assertEquals(20, valid.quality, "quality of the valid item"),
                () -> assertEquals(60, invalid.quality, "quality of the invalid item"));
    }
}
