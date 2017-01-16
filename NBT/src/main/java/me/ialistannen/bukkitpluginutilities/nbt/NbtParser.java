package me.ialistannen.bukkitpluginutilities.nbt;

import java.lang.reflect.Method;
import java.util.Optional;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import me.ialistannen.bukkitpluginutilities.nbt.NBTWrappers.INBTBase;
import me.ialistannen.bukkitpluginutilities.nbt.NBTWrappers.NBTTagCompound;
import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil;
import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.MethodPredicate;
import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.ReflectResponse;
import me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.ReflectResponse.ResultType;

import static me.ialistannen.bukkitpluginutilities.reflection.ReflectionUtil.NameSpace.NMS;


/**
 * A wrapper for the MojangsonParser used for parsing NBT
 */
public class NbtParser {

    private static final Logger LOGGER = Logger.getLogger("NbtParser");

    private static final Method PARSE_METHOD;
    private static boolean error = false;

    static {
        Optional<Class<?>> mojangsonParserClass = ReflectionUtil.getClass(NMS, "MojangsonParser");

        if (!mojangsonParserClass.isPresent()) {
            LOGGER.warning(
                    "Can't find the class MojangsonParser: "
                            + Bukkit.getServer().getClass().getName()
            );
            error = true;
            PARSE_METHOD = null;
        }
        else {
            ReflectResponse<Method> parseMethod = ReflectionUtil.getMethod(mojangsonParserClass.get(), new
                    MethodPredicate()
                    .withName("parse")
                    .withParameters(String.class));

            if (parseMethod.isValuePresent()) {
                PARSE_METHOD = parseMethod.getValue();
            }
            else {
                LOGGER.warning(
                        "Can't find MojangsonParser's parse method: "
                                + mojangsonParserClass.get().getName()
                );
                error = true;
                PARSE_METHOD = null;
            }
        }
    }

    /**
     * @throws IllegalStateException If {@link #error} is true
     */
    private static void ensureNoError() {
        if (error) {
            throw new IllegalStateException("A critical, non recoverable error occurred earlier.");
        }
    }

    /**
     * Parses a String to an {@link NBTTagCompound}
     *
     * @param nbt The nbt to parse
     *
     * @return The parsed NBTTagCompound
     *
     * @throws NbtParseException if an error occurred while parsing the NBT
     *                           tag
     */
    @SuppressWarnings("unused")
    public static NBTTagCompound parse(String nbt) throws NbtParseException {
        ensureNoError();

        ReflectResponse<Object> response = ReflectionUtil.invokeMethod(PARSE_METHOD, null, nbt);

        if (!response.isSuccessful()) {
            if (response.getResultType() == ResultType.ERROR) {
                throw new NbtParseException(response.getException().getCause().getMessage(), response.getException()
                        .getCause());
            }
        }

        // is defined by the method and the only one making sense
        return (NBTTagCompound) INBTBase.fromNBT(response.getValue());
    }

    /**
     * An exception occurred while parsing a NBT tag. Checked.
     */
    @SuppressWarnings("WeakerAccess")
    public static class NbtParseException extends Exception {
        /**
         *
         */
        private static final long serialVersionUID = -8761176772930529828L;
        
        private NbtParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
