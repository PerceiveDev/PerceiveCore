package com.perceivedev.perceivecore.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

/**
 * Created by Julian on 21.09.2016.
 */
@SuppressWarnings("unused")
public class SerializerTest implements ConfigSerializable {

    private static String staticTest = ThreadLocalRandom.current().nextInt() + "";
    private transient String transientTest;

    private String            test;
    private String            testNull;
    private TestTwo           two;
    private SerializableClass serializableClass;

    protected SerializerTest() {
    }

    public SerializerTest(String test) {
        this.test = test;
        two = new TestTwo(test + " copy");
        serializableClass = new SerializableClass("Serializable: " + test);
        transientTest = "transient";
    }

    @Override
    public String toString() {
        return "TestClass\n{"
                  + "\n\ttest='" + test + '\''
                  + "\n\t, testNull='" + testNull + '\''
                  + "\n\t, two=" + two
                  + "\n\t, serializableClass=" + serializableClass
                  + "\n\t, transientTest="
                  + transientTest + "\n\t, staticTest=" + staticTest + "\n}";
    }

    private static class TestTwo implements ConfigSerializable {

        private String otherData;

        protected TestTwo() {
        }

        public TestTwo(String otherData) {
            this.otherData = otherData;
        }

        @Override
        public String toString() {
            return "TestTwo{" + "otherData='" + otherData + '\'' + '}';
        }
    }

    private static class SerializableClass implements ConfigurationSerializable {

        private String string;

        public SerializableClass(String string) {
            this.string = string;
        }

/*
        public SerializableClass(Map<String, Object> map) {
            string = (String) map.get("string");
        }
*/

        @Override
        public Map<String, Object> serialize() {
            Map<String, Object> map = new HashMap<>();
            map.put("string", string);
            return map;
        }

/*
        public static SerializableClass deserialize(Map<String, Object> map) {
            return new SerializableClass((String) map.get("string"));
        }
*/

        public static SerializableClass valueOf(Map<String, Object> map) {
            return new SerializableClass((String) map.get("string"));
        }

        @Override
        public String toString() {
            return "SerializableClass{" + "string='" + string + '\'' + '}';
        }
    }

    public static void main(String[] args) {
        SerializationManager serializationManager = new SerializationManager();
        SerializerTest testClass = new SerializerTest("This is cool");

        serializationManager.addSerializationProxy(String.class, new SerializationProxy<String>() {
            @Override
            public Map<String, Object> serialize(String object) {
                Map<String, Object> map = new HashMap<>();
                map.put("string", object + " proxy serialized");
                return map;
            }

            @Override
            public String deserialize(Map<String, Object> data) {
                return ((String) data.get("string")).replace(" proxy serialized", " proxy deserialized");
            }
        });

        Map<String, Object> serialize = serializationManager.serialize(testClass);

        System.out.println();
        System.out.println("==== BEFORE ====");
        System.out.println(testClass);
        System.out.println();

        System.out.println("==== SERIALIZED ====");
        System.out.println(serialize);
        System.out.println();

        System.out.println();
        System.out.println("==== AFTER ====");
        System.out.println(serializationManager.deserialize(SerializerTest.class, serialize));
    }
}
