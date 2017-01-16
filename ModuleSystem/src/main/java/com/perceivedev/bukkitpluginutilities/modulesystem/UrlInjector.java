package com.perceivedev.bukkitpluginutilities.modulesystem;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Objects;

import com.google.common.base.Throwables;

/**
 * Injects URLs in an {@link URLClassLoader}
 */
class UrlInjector {

    private WeakReference<URLClassLoader> classLoader;

    // I am not sure if the weak reference is needed, this will work most likely though
    private WeakReference<Method> addURLMethod;

    /**
     * Injects URLs in an {@link URLClassLoader}
     *
     * @param classLoader The {@link URLClassLoader} to inject them to
     *
     * @throws RuntimeException wrapping any {@link ReflectiveOperationException} that may occur.
     */
    UrlInjector(URLClassLoader classLoader) {
        Objects.requireNonNull(classLoader, "classLoader can not be null!");

        this.classLoader = new WeakReference<>(classLoader);

        try {
            Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            addURL.setAccessible(true);

            addURLMethod = new WeakReference<>(addURL);
        } catch (ReflectiveOperationException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Adds an {@link URL} to the {@link URLClassLoader}
     *
     * @param url The {@link URL} to add
     *
     * @throws RuntimeException wrapping any {@link ReflectiveOperationException} that may occur.
     */
    void addUrl(URL url) {
        Objects.requireNonNull(url, "url can not be null!");

        try {
            Method method = addURLMethod.get();
            if (method != null) {
                method.invoke(classLoader.get(), url);
            }
        } catch (ReflectiveOperationException e) {
            throw Throwables.propagate(e);
        }
    }
}
