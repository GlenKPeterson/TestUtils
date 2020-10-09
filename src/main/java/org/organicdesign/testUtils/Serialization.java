package org.organicdesign.testUtils;

import org.jetbrains.annotations.NotNull;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serialization {
    /**
     Serializes and deserializes the passed object.  Note: Lambdas and anonymous classes are NOT
     serializable in Java 8.  Only enums and classes that implement Serializable are.  This might
     be the best reason to use enums for singletons.
     @param obj the item to serialize and deserialize
     @return whatever's left after serializing and deserializing the original item.  Sometimes
     things throw (unchecked) exceptions.
     */

    @SuppressWarnings("unchecked")
    public static <T> T serializeDeserialize(
            @NotNull T obj
    ) {

        // This method was started by sblommers.  Thanks for your help!
        // Mistakes are Glen's.
        // https://github.com/GlenKPeterson/Paguro/issues/10#issuecomment-242332099

        // Write
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);

            final byte[] data = baos.toByteArray();

            // Read
            ByteArrayInputStream baip = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(baip);
            return (T) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
