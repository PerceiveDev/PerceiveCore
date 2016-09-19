
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

}
