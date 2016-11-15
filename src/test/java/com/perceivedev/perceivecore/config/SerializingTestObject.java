package com.perceivedev.perceivecore.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/** A small testobject */
public class SerializingTestObject implements ConfigSerializable {

    private String              testString;
    private byte                testByte;
    private short               testShort;
    private int                 testInt;
    private long                testLong;
    private float               testFloat;
    private double              testDouble;

    private transient String    transientString;

    private Map<Object, Object> testMap;
    private NestedObjectClass   nestedObjectClass;
    private ConfigurationTest   configurationTest;

    private UUID                testUUID;
    private TestEnum            testEnum;

    public SerializingTestObject() {
    }

    public SerializingTestObject(String testString, byte testByte, short testShort, int testInt, long testLong,
            float testFloat, double testDouble, String transientString, Map<Object, Object> testMap,
            NestedObjectClass nestedObjectClass, ConfigurationTest configurationTest, UUID testUUID,
            TestEnum testEnum) {
        this.testString = testString;
        this.testByte = testByte;
        this.testShort = testShort;
        this.testInt = testInt;
        this.testLong = testLong;
        this.testFloat = testFloat;
        this.testDouble = testDouble;
        this.transientString = transientString;
        this.testMap = testMap;
        this.nestedObjectClass = nestedObjectClass;
        this.configurationTest = configurationTest;
        this.testUUID = testUUID;
        this.testEnum = testEnum;
    }

    public String getTestString() {
        return testString;
    }

    public byte getTestByte() {
        return testByte;
    }

    public short getTestShort() {
        return testShort;
    }

    public int getTestInt() {
        return testInt;
    }

    public long getTestLong() {
        return testLong;
    }

    public float getTestFloat() {
        return testFloat;
    }

    public double getTestDouble() {
        return testDouble;
    }

    public String getTransientString() {
        return transientString;
    }

    public Map<Object, Object> getTestMap() {
        return testMap;
    }

    public NestedObjectClass getNestedObjectClass() {
        return nestedObjectClass;
    }

    public ConfigurationTest getConfigurationTest() {
        return configurationTest;
    }

    public UUID getTestUUID() {
        return testUUID;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public SerializingTestObject cloneWithoutTransient() {
        return new SerializingTestObject(testString, testByte, testShort, testInt, testLong, testFloat, testDouble,
                null, testMap, nestedObjectClass, configurationTest, testUUID, testEnum);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SerializingTestObject)) {
            return false;
        }
        SerializingTestObject object = (SerializingTestObject) o;

        System.out.println("Equal SerializingTestObject: " +
                (testByte == object.testByte) + " " +
                (testShort == object.testShort) + " " +
                (testInt == object.testInt) + " " +
                (testLong == object.testLong) + " " +
                (Float.compare(object.testFloat, testFloat) == 0) + " " +
                (Double.compare(object.testDouble, testDouble) == 0) + " " +
                (Objects.equals(testString, object.testString)) + " " +
                (Objects.equals(transientString, object.transientString)) + " " +
                (Objects.equals(testMap, object.testMap)) + " " +
                (Objects.equals(nestedObjectClass, object.nestedObjectClass)) + " " +
                (Objects.equals(configurationTest, object.configurationTest)) + " " +
                (Objects.equals(testUUID, object.testUUID)) + " " +
                (Objects.equals(testEnum, object.testEnum)));

        return testByte == object.testByte &&
                testShort == object.testShort &&
                testInt == object.testInt &&
                testLong == object.testLong &&
                Float.compare(object.testFloat, testFloat) == 0 &&
                Double.compare(object.testDouble, testDouble) == 0 &&
                Objects.equals(testString, object.testString) &&
                Objects.equals(transientString, object.transientString) &&
                Objects.equals(testMap, object.testMap) &&
                Objects.equals(nestedObjectClass, object.nestedObjectClass) &&
                Objects.equals(configurationTest, object.configurationTest) &&
                Objects.equals(testUUID, object.testUUID) &&
                Objects.equals(testEnum, testEnum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testString, testByte, testShort, testInt, testLong, testFloat, testDouble, transientString,
                testMap, nestedObjectClass, configurationTest, testUUID, testEnum);
    }

    public static class NestedObjectClass implements ConfigSerializable {
        private String testStringNested;

        public NestedObjectClass() {
        }

        public NestedObjectClass(String testStringNested) {
            this.testStringNested = testStringNested;
        }

        public String getTestStringNested() {
            return testStringNested;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof NestedObjectClass)) {
                return false;
            }
            NestedObjectClass that = (NestedObjectClass) o;
            return Objects.equals(testStringNested, that.testStringNested);
        }

        @Override
        public int hashCode() {
            return Objects.hash(testStringNested);
        }
    }

    public static class ConfigurationTest implements ConfigurationSerializable {

        private String name;
        private int    age;

        public ConfigurationTest(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public ConfigurationTest(Map<String, Object> map) {
            this((String) map.get("name"), (Integer) map.get("age"));
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("name", name);
            map.put("age", age);
            return map;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ConfigurationTest)) {
                return false;
            }
            ConfigurationTest that = (ConfigurationTest) o;
            return age == that.age &&
                    Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, age);
        }
    }

    public enum TestEnum {
        ONE,
        TWO,
        THREE
    }
}
