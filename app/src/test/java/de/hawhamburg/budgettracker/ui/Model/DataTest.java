package de.hawhamburg.budgettracker.ui.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class DataTest {

    private Data testData;

    //Neues Objekt vor jedem Test
    @Before
    public void setup() {
        testData = new Data(100, "Essen", "Mittag bei Subway", "abc123", "2025-07-13");
    }

    // Getter und Setter testen
    @Test
    public void testGetAmount() {
        // Arrange
        int expected = 100;

        // Act
        int actual = testData.getAmount();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    public void testGetType() {
        String expected = "Essen";
        String actual = testData.getType();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetNote() {
        String expected = "Mittag bei Subway";
        String actual = testData.getNote();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetId() {
        String expected = "abc123";
        String actual = testData.getId();
        assertEquals(expected, actual);
    }

    @Test
    public void testGetDate() {
        String expected = "2025-07-13";
        String actual = testData.getDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetAmount() {

        int expected = 250;
        testData.setAmount(expected);
        int actual = testData.getAmount();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetType() {
        String expected = "Miete";
        testData.setType(expected);
        String actual = testData.getType();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetNote() {
        String expected = "Juli-Miete";
        testData.setNote(expected);
        String actual = testData.getNote();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetId() {
        String expected = "xyz456";
        testData.setId(expected);
        String actual = testData.getId();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetDate() {
        String expected = "2025-08-01";
        testData.setDate(expected);
        String actual = testData.getDate();
        assertEquals(expected, actual);
    }


    // Edge-Cases testen
    @Test
    public void testSetTypeAllowsNull() {
        testData.setType(null);
        assertNull(testData.getType());
    }

    @Test
    public void testSetNegativeAmount() {
        int expected = -999;
        testData.setAmount(expected);
        int actual = testData.getAmount();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetAmountToZero() {
        int expected = 0;
        testData.setAmount(expected);
        int actual = testData.getAmount();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetAmountToMaxInteger() {
        int expected = Integer.MAX_VALUE;
        testData.setAmount(expected);
        int actual = testData.getAmount();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetAmountToMinInteger() {
        int expected = Integer.MIN_VALUE;
        testData.setAmount(expected);
        int actual = testData.getAmount();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetEmptyType() {
        String expected = "";
        testData.setType(expected);
        String actual = testData.getType();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetLongNote() {
        String expected = "a".repeat(1000);
        testData.setNote(expected);
    }

    @Test
    public void testSetNullNote() {
        String expected = null;
        testData.setNote(expected);
        String actual = testData.getNote();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetEmptyDate() {
        String expected = "";
        testData.setDate(expected);
        String actual = testData.getDate();
        assertEquals(expected, actual);
    }

    @Test
    public void testSetMalformedDate() {
        String expected = "not-a-date";
        testData.setDate(expected);
        String actual = testData.getDate();
        assertEquals(expected, actual);
    }
}







