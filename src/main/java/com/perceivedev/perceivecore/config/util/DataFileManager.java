/**
 * 
 */
package com.perceivedev.perceivecore.config.util;

import java.nio.file.Path;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import com.perceivedev.perceivecore.config.ConfigSerializable;

/**
 * @author Rayzr
 * @param <K>
 * @param <V>
 *
 */
public class DataFileManager<K, V extends ConfigSerializable> extends DataManager<K, V> {

    public DataFileManager(Path path, Class<V> dataClass, Map<K, V> map) {
        super(path, dataClass, map);
    }

    public DataFileManager(Path path, Class<V> dataClass) {
        super(path, dataClass);
    }

    public DataFileManager(Plugin plugin, String path, Class<V> dataClass) {
        super(plugin, path, dataClass);
    }

    @Override
    public boolean validatePath(Path path) {
        return false;
    }

    @Override
    public void save() {
    }

    @Override
    public void load() {
    }

}
