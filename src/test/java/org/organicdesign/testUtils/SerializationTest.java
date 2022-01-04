package org.organicdesign.testUtils;


import org.junit.jupiter.api.Test;

import java.io.Serializable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.organicdesign.testUtils.Serialization.serializeDeserialize;

public class SerializationTest {
    public static class Hello implements Serializable {
        private static final long serialVersionUID = 20190313L;
        public String stuff;
    }

    @Test
    public void testBasics() {
        Hello hi = new Hello();
        hi.stuff = "Hello World!";
        Hello deserializedHi = serializeDeserialize(hi);
        assertNotSame(hi, deserializedHi);
        assertNotSame(hi.stuff, deserializedHi.stuff);
        assertEquals(hi.stuff, deserializedHi.stuff);
    }

}