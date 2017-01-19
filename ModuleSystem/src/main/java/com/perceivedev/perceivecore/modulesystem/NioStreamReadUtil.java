package com.perceivedev.perceivecore.modulesystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.common.base.Throwables;

/**
 * A util to help with reading NIO streams
 */
class NioStreamReadUtil {

    /**
     * Applies a function to a read zip entry. Closes the entry's {@link InputStream} again
     *
     * @param zipFile The {@link ZipFile} to read from
     * @param zipEntry The {@link ZipEntry} to read
     * @param function The function to apply to the resulting {@link InputStream}
     * @param <T> The type of the result of the function
     *
     * @return The result of the function
     */
    static <T> T doWithZipEntryStream(ZipFile zipFile, ZipEntry zipEntry, Function<InputStream, T> function) {
        try (InputStream inputStream = zipFile.getInputStream(zipEntry)) {
            return function.apply(inputStream);
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    /**
     * Applies a function to a read zip entry. Closes the entry's {@link InputStream} again
     *
     * @param zipFile The {@link ZipFile} to read from
     * @param zipEntry The {@link ZipEntry} to read
     * @param consumer The consumer to apply to the resulting {@link InputStream}
     */
    static void doWithZipEntryStreamReader(ZipFile zipFile, ZipEntry zipEntry,
                                           Consumer<InputStreamReader> consumer) {
        doWithZipEntryStream(zipFile, zipEntry, inputStream -> {
            try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
                consumer.accept(inputStreamReader);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
            return Void.TYPE;
        });
    }
}
