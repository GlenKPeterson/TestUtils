package org.organicdesign.testUtils;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;

public class EqualsContractTest {
    class Point2d {
        final float x;
        final float y;
        Point2d(float theX, float theY) { x = theX; y = theY; }

        @Override public int hashCode() {
            return Float.hashCode(x) +
                   Float.hashCode(y);
        }

        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( !(o instanceof Point2d) ) { return false; }

            Point2d that = (Point2d) o;
            return this.x == that.x &&
                   this.y == that.y;
        }
    }

    class Point3d extends Point2d {
        final float z;
        Point3d(float theX, float theY, float theZ) {
            super(theX, theY);
            z = theZ;
        }

        @Override public int hashCode() {
            return super.hashCode() +
                   Float.hashCode(z);
        }

        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( !(o instanceof Point3d) ) { return false; }

            Point3d that = (Point3d) o;
            return this.x == that.x &&
                   this.y == that.y &&
                   this.z == that.z;
        }
    }

    private final Point2d p2d = new Point2d(1.2f, 3.5f);
    private final Point3d p3d = new Point3d(1.2f, 3.5f, 2.7f);

    @Test public void testEqualsDistinctHashcode() {

        equalsDistinctHashCode(p2d,
                               new Point2d(1.2f, 3.5f),
                               new Point2d(1.2f, 3.5f),
                               new Point2d(1.2f, 3.6f));

        equalsDistinctHashCode(p3d,
                               new Point3d(1.2f, 3.5f, 2.7f),
                               new Point3d(1.2f, 3.5f, 2.7f),
                               new Point3d(1.2f, 3.6f, 2.7f));

        assertTrue(p2d.equals(p3d));

        assertFalse(p3d.equals(p2d));
    }

    @Test(expected = AssertionError.class)
    public void testEqualsDistinctHashBoom1() {
        equalsDistinctHashCode(p2d,
                               new Point2d(1.2f, 3.5f),
                               new Point3d(1.2f, 3.5f, 0f),
                               new Point2d(1.2f, 3.6f));

    }

    @Test(expected = AssertionError.class)
    public void testEqualsDistinctHashBoom2() {
        equalsDistinctHashCode(p2d,
                               new Point2d(1.2f, 3.5f),
                               new Point2d(1.2f, 3.5f),
                               p3d);
    }

}