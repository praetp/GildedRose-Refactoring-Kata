package com.gildedrose;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests of the daily update rules. They started as characterization tests that pinned down the legacy
 * {@code updateQuality()} before it was refactored (ADR 0008); the Conjured tests were added test-first for the new
 * feature (ADR 0013). Each case is one day: (sellIn, quality) before the update -> (sellIn, quality) after it.
 */
class GildedRoseTest {

    private static final String NORMAL = "+5 Dexterity Vest";
    private static final String AGED_BRIE = "Aged Brie";
    private static final String SULFURAS = "Sulfuras, Hand of Ragnaros";
    private static final String BACKSTAGE_PASSES = "Backstage passes to a TAFKAL80ETC concert";
    private static final String CONJURED = "Conjured Mana Cake";

    private static Item updatedOnce(String name, int sellIn, int quality) {
        Item item = new Item(name, sellIn, quality);
        new GildedRose(new Item[] {item}).updateQuality();
        return item;
    }

    private static void assertUpdate(String name, int sellIn, int quality, int expectedSellIn, int expectedQuality) {
        Item item = updatedOnce(name, sellIn, quality);
        assertAll(
                () -> assertEquals(name, item.name, "name"),
                () -> assertEquals(expectedSellIn, item.sellIn, "sellIn"),
                () -> assertEquals(expectedQuality, item.quality, "quality"));
    }

    @Nested
    @DisplayName("Normal item")
    class NormalItem {

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("loses 1 quality per day before the sell-by date")
        @CsvSource({
            "10, 20, 9, 19",
            " 1, 20, 0, 19",
            " 1,  1, 0,  0",
        })
        void degradesByOneBeforeSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(NORMAL, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("loses 2 quality per day once the sell-by date has passed")
        @CsvSource({
            "  0, 20,  -1, 18",
            " -1, 20,  -2, 18",
            "-10, 20, -11, 18",
        })
        void degradesTwiceAsFastAfterSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(NORMAL, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("never drops below 0 quality")
        @CsvSource({
            " 5, 0,  4, 0",
            " 0, 1, -1, 0",
            " 0, 0, -1, 0",
            "-1, 1, -2, 0",
        })
        void qualityNeverNegative(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(NORMAL, sellIn, quality, expectedSellIn, expectedQuality);
        }
    }

    @Nested
    @DisplayName("Aged Brie")
    class AgedBrie {

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("gains 1 quality per day before the sell-by date")
        @CsvSource({
            "2, 0, 1, 1",
            "1, 10, 0, 11",
        })
        void increasesByOneBeforeSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(AGED_BRIE, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("gains 2 quality per day once the sell-by date has passed (intended rule, see ADR 0008)")
        @CsvSource({
            " 0, 10, -1, 12",
            "-5, 10, -6, 12",
        })
        void increasesByTwoAfterSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(AGED_BRIE, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("never exceeds 50 quality")
        @CsvSource({
            " 5, 50,  4, 50",
            " 0, 49, -1, 50",
            " 0, 50, -1, 50",
        })
        void qualityNeverAboveFifty(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(AGED_BRIE, sellIn, quality, expectedSellIn, expectedQuality);
        }
    }

    @Nested
    @DisplayName("Sulfuras")
    class Sulfuras {

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> unchanged")
        @DisplayName("never changes sellIn or quality")
        @CsvSource({
            " 0, 80",
            " 5, 80",
            "-1, 80",
        })
        void neverChanges(int sellIn, int quality) {
            assertUpdate(SULFURAS, sellIn, quality, sellIn, quality);
        }
    }

    @Nested
    @DisplayName("Backstage passes")
    class BackstagePasses {

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("gain 1 quality when the concert is more than 10 days away")
        @CsvSource({
            "15, 20, 14, 21",
            "11, 20, 10, 21",
        })
        void increaseByOneMoreThanTenDaysBefore(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(BACKSTAGE_PASSES, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("gain 2 quality when the concert is 10 to 6 days away")
        @CsvSource({
            "10, 20, 9, 22",
            " 6, 20, 5, 22",
        })
        void increaseByTwoTenDaysOrLess(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(BACKSTAGE_PASSES, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("gain 3 quality when the concert is 5 to 1 days away")
        @CsvSource({
            "5, 20, 4, 23",
            "1, 20, 0, 23",
        })
        void increaseByThreeFiveDaysOrLess(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(BACKSTAGE_PASSES, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("drop to 0 quality after the concert")
        @CsvSource({
            " 0, 20, -1, 0",
            " 0, 50, -1, 0",
            "-1, 20, -2, 0",
        })
        void dropToZeroAfterConcert(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(BACKSTAGE_PASSES, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("never exceed 50 quality")
        @CsvSource({
            "15, 50, 14, 50",
            "10, 49,  9, 50",
            " 5, 48,  4, 50",
            " 5, 49,  4, 50",
            " 1, 50,  0, 50",
        })
        void qualityNeverAboveFifty(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(BACKSTAGE_PASSES, sellIn, quality, expectedSellIn, expectedQuality);
        }
    }

    @Nested
    @DisplayName("Conjured item")
    class ConjuredItem {

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("loses 2 quality per day before the sell-by date")
        @CsvSource({
            "3,  6, 2,  4",
            "1, 10, 0,  8",
        })
        void degradesByTwoBeforeSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(CONJURED, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("loses 4 quality per day once the sell-by date has passed")
        @CsvSource({
            " 0, 10, -1, 6",
            "-1, 10, -2, 6",
        })
        void degradesByFourAfterSellByDate(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(CONJURED, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "sellIn {0}, quality {1} -> expected sellIn {2}, expected quality {3}")
        @DisplayName("never drops below 0 quality")
        @CsvSource({
            "5, 1,  4, 0",
            "0, 3, -1, 0",
            "0, 0, -1, 0",
        })
        void qualityNeverNegative(int sellIn, int quality, int expectedSellIn, int expectedQuality) {
            assertUpdate(CONJURED, sellIn, quality, expectedSellIn, expectedQuality);
        }

        @ParameterizedTest(name = "{0} loses {1} quality")
        @DisplayName("is any item whose name starts with \"Conjured\" (case-sensitive)")
        @CsvSource({
            "'Conjured Mana Cake',  2",
            "'Conjured Sword',      2",
            "'Conjured',            2",
            "'conjured Mana Cake',  1",
            "'Mana Cake Conjured',  1",
            "' Conjured Mana Cake', 1",
        })
        void recognisedByNamePrefix(String name, int expectedLoss) {
            Item item = updatedOnce(name, 5, 20);

            assertEquals(20 - expectedLoss, item.quality);
        }
    }

    @Nested
    @DisplayName("Inventory")
    class Inventory {

        @Test
        @DisplayName("updates every item independently")
        void updatesEveryItem() {
            Item normal = new Item(NORMAL, 10, 20);
            Item brie = new Item(AGED_BRIE, 2, 0);
            Item sulfuras = new Item(SULFURAS, 0, 80);
            Item passes = new Item(BACKSTAGE_PASSES, 5, 20);
            GildedRose app = new GildedRose(new Item[] {normal, brie, sulfuras, passes});

            app.updateQuality();

            assertAll(
                    () -> assertEquals("+5 Dexterity Vest, 9, 19", normal.toString()),
                    () -> assertEquals("Aged Brie, 1, 1", brie.toString()),
                    () -> assertEquals("Sulfuras, Hand of Ragnaros, 0, 80", sulfuras.toString()),
                    () -> assertEquals("Backstage passes to a TAFKAL80ETC concert, 4, 23", passes.toString()));
        }

        @Test
        @DisplayName("handles an empty inventory")
        void emptyInventory() {
            GildedRose app = new GildedRose(new Item[0]);

            app.updateQuality();

            assertEquals(0, app.items.length);
        }

        @Test
        @DisplayName("applies the rules again on each day")
        void updatesOverSeveralDays() {
            Item passes = new Item(BACKSTAGE_PASSES, 2, 40);
            GildedRose app = new GildedRose(new Item[] {passes});

            app.updateQuality(); // 2 days left: +3 -> 43
            app.updateQuality(); // 1 day left: +3 -> 46
            app.updateQuality(); // concert has passed: 0

            assertAll(
                    () -> assertEquals(-1, passes.sellIn),
                    () -> assertEquals(0, passes.quality));
        }
    }
}
