package com.perceivedev.perceivecore.config;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * A basic test for the {@link SerializationManager}
 */
public class SerializationManagerTest {

    private SerializationManager serializationManager = new SerializationManager();

    @Test
    public void addSerializationProxy() throws Exception {
        String string = "this is a test";
        Map<String, Object> serialize = serializationManager.serialize(new SerializableString(string));
        SerializableString deserialize = serializationManager.deserialize(SerializableString.class, serialize);
        Assert.assertEquals(string, deserialize.getData());

        SerializationManager.addSerializationProxy(String.class, new StringProxy());

        serialize = serializationManager.serialize(new SerializableString(string));
        deserialize = serializationManager.deserialize(SerializableString.class, serialize);

        Assert.assertEquals(string + "-proxy", deserialize.getData());

        // clean up
        SerializationManager.removeSerializationProxy(String.class);
    }

    @Test
    public void removeSerializationProxy() throws Exception {
        String string = "this is a test";
        SerializationManager.addSerializationProxy(String.class, new StringProxy());

        Map<String, Object> serialize = serializationManager.serialize(new SerializableString(string));
        SerializableString deserialize = serializationManager.deserialize(SerializableString.class, serialize);

        Assert.assertEquals(string + "-proxy", deserialize.getData());

        SerializationManager.removeSerializationProxy(String.class);

        serialize = serializationManager.serialize(new SerializableString(string));
        deserialize = serializationManager.deserialize(SerializableString.class, serialize);

        Assert.assertEquals(string, deserialize.getData());
    }

    @Test
    public void serialize() throws Exception {
        String testString = "testString";
        byte testByte = 30;
        short testShort = -1000;
        int testInt = 68657;
        long testLong = 5656565656L;
        float testFloat = (float) (253 + Math.pow(2, -4));
        double testDouble = 253 + Math.pow(2, -8);

        String testTransient = "transient :)";

        SerializingTestObject.NestedObjectClass nestedObjectClass = new SerializingTestObject.NestedObjectClass("nested test");
        SerializingTestObject.ConfigurationTest configurationTest = new SerializingTestObject.ConfigurationTest("I Al Istannen", 99);

        SerializingTestObject object = new SerializingTestObject(testString, testByte, testShort, testInt, testLong, testFloat, testDouble, testTransient,
                  nestedObjectClass, configurationTest);

        Map<String, Object> serialized = serializationManager.serialize(object);
        SerializingTestObject deserialized = serializationManager.deserialize(SerializingTestObject.class, serialized);

        Assert.assertEquals(object.cloneWithoutTransient(), deserialized);
    }

    @Test
    public void deserialize() throws Exception {
        // Not much to do here, as it is tested in the serialize method. Just validate edge cases.

        Map<String, Object> serialize = serializationManager.serialize(new UnSerializableString("nothing"));
        Assert.assertNull("No default constructor given", serializationManager.deserialize(UnSerializableString.class, serialize));

        serializationManager.serialize(null);
    }

    private static class UnSerializableString implements ConfigSerializable {

        private UnSerializableString(@SuppressWarnings("UnusedParameters") String data) {
        }
    }

    private static class SerializableString implements ConfigSerializable {
        private String data;

        @SuppressWarnings("unused") // used by serialization
        public SerializableString() {
        }

        private SerializableString(String data) {
            this.data = data;
        }

        private String getData() {
            return data;
        }
    }

    private static class StringProxy implements SerializationProxy<String> {

        @Override
        public Map<String, Object> serialize(String object) {
            Map<String, Object> map = new HashMap<>();
            map.put("proxied", object + "-proxy");
            return map;
        }

        @Override
        public String deserialize(Map<String, Object> data) {
            return (String) data.get("proxied");
        }
    }
}