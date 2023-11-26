package org.jabref.model.entry.field;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.jabref.model.entry.types.BiblatexApaEntryType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FieldFactoryTest {
    @Test
    void testOrFieldsTwoTerms() {
        assertEquals("Aaa/Bbb", FieldFactory.serializeOrFields(new UnknownField("aaa"), new UnknownField("bbb")));
        assertEquals("comment-user1", FieldFactory.serializeOrFields(new UserSpecificCommentField("user1")));
    }

    @Test
    void serializeOrFieldsList() {
        Set<OrFields> fields = new HashSet<>();
        fields.add(new OrFields(new UnknownField("Field1"),new UnknownField("Field2"),new UnknownField("Field3")));
        assertEquals("Field1/Field2/Field3",FieldFactory.serializeOrFieldsList(fields));
    }

    @Test
    public void testGetNotTextFieldNames() {
        List<Field> notTextFields = FieldFactory.getNotTextFieldNames();
        List<Field> expectedNotTextFields = Arrays.asList(
                StandardField.DOI, StandardField.FILE, StandardField.URL,
                StandardField.URI, StandardField.ISBN, StandardField.ISSN,
                StandardField.MONTH, StandardField.DATE, StandardField.YEAR
        );
        assertEquals(expectedNotTextFields, notTextFields);
    }

    @Test
    public void testGetIdentifierFieldNames() {
        List<Field> identifierFields = FieldFactory.getIdentifierFieldNames();
        List<Field> expectedIdentifierFields = Arrays.asList(
                StandardField.DOI, StandardField.EPRINT, StandardField.PMID
        );
        assertEquals(expectedIdentifierFields, identifierFields);
    }

    @Test
    void testOrFieldsThreeTerms() {
        assertEquals("Aaa/Bbb/Ccc", FieldFactory.serializeOrFields(new UnknownField("aaa"), new UnknownField("bbb"), new UnknownField("ccc")));
    }

    private static Stream<Arguments> fieldsWithoutFieldProperties() {
        return Stream.of(
                // comment fields
                Arguments.of(new UserSpecificCommentField("user1"), "comment-user1"),
                Arguments.of(new UserSpecificCommentField("other-user-id"), "comment-other-user-id"),
                // unknown field
                Arguments.of(new UnknownField("cased", "cAsEd"), "cAsEd")
        );
    }

    @ParameterizedTest
    @MethodSource
    void fieldsWithoutFieldProperties(Field expected, String name) {
        assertEquals(expected, FieldFactory.parseField(name));
    }

    @Test
    void testDoesNotParseApaFieldWithoutEntryType() {
        assertNotEquals(BiblatexApaField.ARTICLE, FieldFactory.parseField("article"));
    }

    @Test
    void testDoesParseApaFieldWithEntryType() {
        assertEquals(BiblatexApaField.ARTICLE, FieldFactory.parseField(BiblatexApaEntryType.Constitution, "article"));
    }

    @Test
    void testParseOrFields() {
        String fieldNames = "Field1/Field2/Field3";
        OrFields result = FieldFactory.parseOrFields(fieldNames);

        Set<Field> expectedFields = new LinkedHashSet<>();
        expectedFields.add(new UnknownField("Field1"));
        expectedFields.add(new UnknownField("Field2"));
        expectedFields.add(new UnknownField("Field3"));

        assertEquals(new OrFields(expectedFields), result);
    }

    @Test
    void testParseOrFieldsList() {
        String fieldNames = "Field1/Field2;Field3/Field4;Field5";
        Set<OrFields> result = FieldFactory.parseOrFieldsList(fieldNames);

        Set<OrFields> expectedOrFields = new LinkedHashSet<>();
        expectedOrFields.add(new OrFields(new UnknownField("Field1"), new UnknownField("Field2")));
        expectedOrFields.add(new OrFields(new UnknownField("Field3"), new UnknownField("Field4")));
        expectedOrFields.add(new OrFields(new UnknownField("Field5")));

        assertEquals(expectedOrFields, result);
    }

    @ParameterizedTest
    @MethodSource("generateFieldLists")
    void parseFieldList(List<Field> expected, String input) {
        assertEquals(expected.stream().collect(Collectors.toSet()), FieldFactory.parseFieldList(input));
    }

    @ParameterizedTest
    @MethodSource("generateFieldLists")
    void serializeFieldsList(List<Field> fields, String expected) {
        assertEquals(expected, FieldFactory.serializeFieldsList(fields));
    }
    private static Stream<Arguments> generateFieldLists() {
        Field unknownField1 = new UnknownField("Field1");
        Field unknownField2 = new UnknownField("Field2");
        List<Field> unknownFields = Arrays.asList(unknownField1, unknownField2);

        Field userField1 = new UserSpecificCommentField("user1");
        Field userField2 = new UserSpecificCommentField("user2");
        List<Field> userFields = Arrays.asList(userField1, userField2);

        return Stream.of(
                Arguments.of(unknownFields, "Field1;Field2"),
                Arguments.of(userFields, "comment-user1;comment-user2")
        );
    }

    @Test
    public void testGetAllFields() {
        Set<Field> allFields = FieldFactory.getAllFields();

        assertTrue(allFields.containsAll(EnumSet.allOf(BiblatexApaField.class)));
        assertTrue(allFields.containsAll(EnumSet.allOf(BiblatexSoftwareField.class)));
        assertTrue(allFields.containsAll(EnumSet.allOf(IEEEField.class)));
        assertTrue(allFields.containsAll(EnumSet.allOf(InternalField.class)));
        assertTrue(allFields.containsAll(EnumSet.allOf(SpecialField.class)));
        assertTrue(allFields.containsAll(EnumSet.allOf(StandardField.class)));

        assertTrue(allFields.stream().noneMatch(field -> field instanceof UserSpecificCommentField));
    }

    @Test
    public void testGetCommonFields() {
        Set<Field> commonFields = FieldFactory.getCommonFields();
        EnumSet<StandardField> allStandardFields = EnumSet.allOf(StandardField.class);
        assertTrue(commonFields.containsAll(allStandardFields));

        assertTrue(commonFields.contains(InternalField.INTERNAL_ALL_FIELD));
        assertTrue(commonFields.contains(InternalField.INTERNAL_ALL_TEXT_FIELDS_FIELD));
        assertTrue(commonFields.contains(InternalField.KEY_FIELD));

        assertEquals(allStandardFields.size() + 3, commonFields.size());
    }

    @Test
    public void testGetStandardFieldsWithCitationKey() {
        Set<Field> fieldsWithCitationKey = FieldFactory.getStandardFieldsWithCitationKey();
        EnumSet<StandardField> allStandardFields = EnumSet.allOf(StandardField.class);
        assertTrue(fieldsWithCitationKey.containsAll(allStandardFields));

        assertTrue(fieldsWithCitationKey.contains(InternalField.KEY_FIELD));

        assertEquals(allStandardFields.size() + 1, fieldsWithCitationKey.size());
    }

    @Test
    public void testGetFieldsFiltered() {
        Predicate<Field> textFieldsSelector = field -> field.getDisplayName().toLowerCase().contains("text");
        Set<Field> filteredFields = FieldFactory.getFieldsFiltered(textFieldsSelector);
        assertTrue(filteredFields.stream().allMatch(textFieldsSelector));
        assertFalse(filteredFields.stream().anyMatch(field -> !textFieldsSelector.test(field)));
        assertTrue(filteredFields.size() <= FieldFactory.getAllFields().size());
    }
}
