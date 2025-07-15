package de.hawhamburg.budgettracker.ui.Model;


import junit.framework.TestCase;

import org.junit.Test;

public class DataTest extends TestCase {

    @Test
    public void testConstructorAndGetters() {
        Data data = new Data(50, "Einkauf", "Lebensmittel", "1", "2025-07-14");

        assertEquals(50, data.getAmount());
        assertEquals("Einkauf", data.getType());
        assertEquals("Lebensmittel", data.getNote());
        assertEquals("1", data.getId());
        assertEquals("2025-07-14", data.getDate());
    }

    @Test
    public void testSetters() {
        Data data = new Data();
        data.setAmount(50);
        data.setType("Rent");
        data.setNote("-");
        data.setId("2");
        data.setDate("2025-01-01");

        assertEquals(50, data.getAmount());
        assertEquals("Rent", data.getType());
        assertEquals("-", data.getNote());
        assertEquals("2", data.getId());
        assertEquals("2025-01-01", data.getDate());
    }
    @Test
    public void testSetTypeAllowsNull() {
        Data data = new Data();
        data.setType(null);
        assertNull(data.getType());
    }

}