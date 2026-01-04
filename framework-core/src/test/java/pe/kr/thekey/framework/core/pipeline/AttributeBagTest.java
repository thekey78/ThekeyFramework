package pe.kr.thekey.framework.core.pipeline;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for the {@link AttributeBag#put(String, Object)} method.
 * <p>
 * This method stores a key-value pair in the internal concurrent map.
 */
public class AttributeBagTest {

    @Test
    void testPutAddsNewKeyValuePair() {
        // Arrange
        AttributeBag attributeBag = new AttributeBag();
        String key = "testKey";
        String value = "testValue";

        // Act
        attributeBag.put(key, value);

        // Assert
        assertEquals(value, attributeBag.get(key), "The value for the given key should match the value put into the bag.");
    }

    @Test
    void testPutOverridesValueForExistingKey() {
        // Arrange
        AttributeBag attributeBag = new AttributeBag();
        String key = "existingKey";
        String oldValue = "oldValue";
        String newValue = "newValue";

        attributeBag.put(key, oldValue);

        // Act
        attributeBag.put(key, newValue);

        // Assert
        assertEquals(newValue, attributeBag.get(key), "The value for the given key should be updated to the new value.");
    }

    @Test
    void testPutHandlesNullKey() {
        // Arrange
        AttributeBag attributeBag = new AttributeBag();
        String value = "valueForNullKey";

        // Act
        assertThrows(NullPointerException.class, () -> attributeBag.put(null, value));
    }

    @Test
    void testPutHandlesNullValue() {
        // Arrange
        AttributeBag attributeBag = new AttributeBag();
        String key = "keyForNullValue";

        // Act
        assertThrows(NullPointerException.class, () -> attributeBag.put(key, null));
    }

    @Test
    void testPutUpdatesInternalMap() {
        // Arrange
        AttributeBag attributeBag = new AttributeBag();
        String key = "internalMapKey";
        String value = "internalMapValue";

        // Act
        attributeBag.put(key, value);
        Map<String, Object> internalMap = attributeBag.asMap();

        // Assert
        assertTrue(internalMap.containsKey(key), "The internal map should contain the key put into the bag.");
        assertEquals(value, internalMap.get(key), "The internal map should contain the correct value for the key.");
    }

}