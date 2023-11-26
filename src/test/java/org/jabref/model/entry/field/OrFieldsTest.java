package org.jabref.model.entry.field;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrFieldsTest {

    private Field field1;
    private Field field2;

    @BeforeEach
    void setUp() {
        field1 = new UnknownField("Field1");
        field2 = new UnknownField("Field2");
    }

    @Test
    void testSingleFieldConstructor() {
        OrFields orFields = new OrFields(field1);
        assertTrue(orFields.contains(field1));
        assertEquals(1, orFields.getFields().size());
    }

    @Test
    void testMultipleFieldConstructor() {
        OrFields orFields = new OrFields(field1, field2);
        assertTrue(orFields.contains(field1));
        assertTrue(orFields.contains(field2));
        assertEquals(2, orFields.getFields().size());
    }

    @Test
    void testCollectionFieldConstructor() {
        OrFields orFields = new OrFields(Arrays.asList(field1, field2));
        assertTrue(orFields.contains(field1));
        assertTrue(orFields.contains(field2));
        assertEquals(2, orFields.getFields().size());
    }

    @Test
    void testGetDisplayName() {
        OrFields orFields = new OrFields(field1, field2);
        assertEquals("Field1/Field2", orFields.getDisplayName());
    }

    @Test
    void testGetPrimary() {
        OrFields orFields = new OrFields(field1, field2);
        assertEquals(field1, orFields.getPrimary());
    }

    @Test
    void testContains() {
        OrFields orFields = new OrFields(field1, field2);
        assertTrue(orFields.contains(field1));
        assertFalse(orFields.contains(new UnknownField("NonExistentField")));
    }

    @Test
    void testCompareTo() {
        OrFields orFields1 = new OrFields(field1);
        OrFields orFields2 = new OrFields(field1, field2);
        assertTrue(orFields1.compareTo(orFields2) < 0);
    }

    @Test
    void testHasExactlyOne() {
        OrFields orFields1 = new OrFields(field1);
        assertTrue(orFields1.hasExactlyOne());

        OrFields orFields2 = new OrFields();
        assertFalse(orFields2.hasExactlyOne());

        OrFields orFields3 = new OrFields(field1, field2);
        assertFalse(orFields3.hasExactlyOne());
    }

    @Test
    void testIsEmpty() {
        OrFields orFields1 = new OrFields();
        assertTrue(orFields1.isEmpty());

        OrFields orFields2 = new OrFields(field1);
        assertFalse(orFields2.isEmpty());
    }

    @Test
    void testEquals() {
        OrFields orFields1 = new OrFields(field1);
        OrFields orFields2 = new OrFields(field1);
        OrFields orFields3 = new OrFields(field2);
        OrFields orFields4 = null;

        assertTrue(orFields1.equals(orFields1));
        assertFalse(orFields1.equals(orFields4));
        assertEquals(orFields1, orFields2);
        assertNotEquals(orFields1, orFields3);
    }

    @Test
    void testHashCode() {
        OrFields orFields1 = new OrFields(field1, field2);
        OrFields orFields2 = new OrFields(field1, field2);

        assertEquals(orFields1.hashCode(), orFields2.hashCode());
    }

    @Test
    void testToString() {
        OrFields orFields = new OrFields(field1, field2);
        assertEquals("OrFields{fields=[UnknownField{name='Field1'}, UnknownField{name='Field2'}]}", orFields.toString());
    }
}
