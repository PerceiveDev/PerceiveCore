package com.perceivedev.perceivecore.updater;

/**
 * A strategy to define what is the newest version
 */
public interface UpdateStrategy<T> {

    /**
     * Generates an identifier from an {@link UpdaterEntry}
     * 
     * @param entry The entry to get it from
     * @return The identifier or {@code null} if not possible.
     */
    T identifierFromEntry(UpdaterEntry entry);

    /**
     * Checks if a file is newer than the other
     * 
     * @param identifierFirst The identifier of the first one (Date, Name,
     *            Version, ...)
     * @param identifierSecond The version of the first second (Date, Name,
     *            Version, ...)
     * @return True if the first is newer than the second
     */
    boolean isNewer(T identifierFirst, T identifierSecond);
}
