
package com.perceivedev.perceivecore.config;

import java.util.Map;

public interface ISerializationHandler<T extends Object> {

	/**
	 * DO NOT EVER MODIFY OR USE THIS. THIS IS ESSENTIAL FOR SERIALIZATION TO
	 * WORK.
	 * 
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public default Map<String, Object> _serialize(Object obj) {

		return serialize((T) obj);

	}

	public Map<String, Object> serialize(T obj);

	public T deserialize(Map<String, Object> map);

	default int i(Map<String, Object> map, String key) {
		try {
			return Integer.valueOf(map.get(key).toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	default double d(Map<String, Object> map, String key) {
		try {
			return Double.valueOf(map.get(key).toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	default float f(Map<String, Object> map, String key) {
		try {
			return Float.valueOf(map.get(key).toString());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

}
