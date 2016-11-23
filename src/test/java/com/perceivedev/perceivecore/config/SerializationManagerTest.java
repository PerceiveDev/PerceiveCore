package com.perceivedev.perceivecore.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.Assert;
import org.junit.Test;

import com.perceivedev.perceivecore.config.SerializingTestObject.TestEnum;

/** A basic test for the {@link SerializationManager} */
public class SerializationManagerTest {

    @Test
    public void addSerializationProxy() throws Exception {
        String string = "this is a test";
        Map<String, Object> serialize = SerializationManager.serialize(new SerializableString(string));
        SerializableString deserialize = SerializationManager.deserialize(SerializableString.class, serialize);
        Assert.assertEquals(string, deserialize.getData());

        SerializationManager.addSerializationProxy(String.class, new StringProxy());

        serialize = SerializationManager.serialize(new SerializableString(string));
        deserialize = SerializationManager.deserialize(SerializableString.class, serialize);

        Assert.assertEquals(string + "-proxy", deserialize.getData());

        // clean up
        SerializationManager.removeSerializationProxy(String.class);
    }

    @Test
    public void removeSerializationProxy() throws Exception {
        String string = "this is a test";
        SerializationManager.addSerializationProxy(String.class, new StringProxy());

        Map<String, Object> serialize = SerializationManager.serialize(new SerializableString(string));
        SerializableString deserialize = SerializationManager.deserialize(SerializableString.class, serialize);

        Assert.assertEquals(string + "-proxy", deserialize.getData());

        SerializationManager.removeSerializationProxy(String.class);

        serialize = SerializationManager.serialize(new SerializableString(string));
        deserialize = SerializationManager.deserialize(SerializableString.class, serialize);

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
        TestEnum testEnum = TestEnum.values()[ThreadLocalRandom.current().nextInt(TestEnum.values().length)];

        String testTransient = "transient :)";

        Map<Object, Object> testMap = new HashMap<>();
        testMap.put("value123", "test");
        testMap.put("integer", 30);
        testMap.put(200, "IntKey");
        testMap.put(new SerializableString("something"), "Serializable key");

        SerializingTestObject.NestedObjectClass nestedObjectClass = new SerializingTestObject.NestedObjectClass("nested test");
        SerializingTestObject.ConfigurationTest configurationTest = new SerializingTestObject.ConfigurationTest("I Al Istannen", 99);

        UUID testUUID = UUID.randomUUID();

        List<Object> testList = Arrays.asList("StringTest", 20);

        SerializingTestObject object = new SerializingTestObject(testString, testByte, testShort, testInt, testLong, testFloat, testDouble, testTransient, testMap, nestedObjectClass,
                configurationTest, testUUID, testEnum, testList);

        Map<String, Object> serialized = SerializationManager.serialize(object);
        YamlConfiguration configuration = new YamlConfiguration();
        configuration.createSection("test", serialized);

        System.out.println(configuration.saveToString());

        // SerializingTestObject deserialized =
        // SerializationManager.deserialize(SerializingTestObject.class,
        // serialized);
        SerializingTestObject deserialized = SerializationManager.deserialize(SerializingTestObject.class, configuration.getConfigurationSection("test"));

        Assert.assertEquals(object.cloneWithoutTransient(), deserialized);
        Assert.assertEquals(deserialized.getTestMap().get("value123").getClass(), String.class);
        Assert.assertEquals(deserialized.getTestMap().get("integer").getClass(), Integer.class);
        Assert.assertEquals(deserialized.getTestUUID(), testUUID);

        System.out.println("Test UUID: " + testUUID + "\nAfter deserialization: " + deserialized.getTestUUID());

    }

    @Test
    public void deserialize() throws Exception {
        // Not much to do here, as it is tested in the serialize method. Just
        // validate edge cases.

        Map<String, Object> serialize = SerializationManager.serialize(new UnSerializableString("nothing"));
        UnSerializableString unserializable = null;
        try {
            unserializable = SerializationManager.deserialize(UnSerializableString.class, serialize);
        } catch (IllegalArgumentException ignore) {

        }
        Assert.assertNull("No default constructor", unserializable);

        SerializationManager.serialize(null);
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

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof SerializableString))
                return false;
            SerializableString that = (SerializableString) o;
            return Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(data);
        }

        @Override
        public String toString() {
            return "SerializableString{" + "data='" + data + '\'' + '}';
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