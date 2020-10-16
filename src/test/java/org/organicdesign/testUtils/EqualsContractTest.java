package org.organicdesign.testUtils;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.organicdesign.testUtils.EqualsContract.equalsDistinctHashCode;
import static org.organicdesign.testUtils.EqualsContract.equalsSameHashCode;

public class EqualsContractTest {
    static class Point2d {
        final int x;
        final int y;
        Point2d(int theX, int theY) { x = theX; y = theY; }

        @Override public int hashCode() {
            return x + y;
        }

        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( !(o instanceof Point2d) ) { return false; }

            Point2d that = (Point2d) o;
            return this.x == that.x &&
                   this.y == that.y;
        }
    }

    static class Point3d extends Point2d {
        final int z;
        Point3d(int theX, int theY, int theZ) {
            super(theX, theY);
            z = theZ;
        }

        @Override public int hashCode() {
            return super.hashCode() + z;
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

    private final Point2d p2d = new Point2d(1, 2);
    private final Point3d p3d = new Point3d(1, 2, 3);

    @Test public void testEqualsHashcode() {

        equalsSameHashCode(p2d,
                           new Point2d(1, 2),
                           new Point2d(1, 2),
                           new Point2d(2, 1));

        equalsDistinctHashCode(p3d,
                               new Point3d(1, 2, 3),
                               new Point3d(1, 2, 3),
                               new Point3d(1, 2, 4));

        assertEquals(p2d.hashCode(), new Point3d(1, 2, 0).hashCode());

        assertTrue(p2d.equals(p3d));

        assertFalse(p3d.equals(p2d));
    }

    @Test(expected = AssertionError.class)
    public void testEqualsHashBoom1() {
        equalsSameHashCode(p2d,
                           new Point2d(1, 2),
                           new Point3d(1, 2, 0),
                           new Point2d(2, 1));

    }

    @Test(expected = AssertionError.class)
    public void testEqualsHashBoom2() {
        equalsSameHashCode(p2d,
                           new Point2d(1, 2),
                           new Point2d(1, 2),
                           new Point3d(1, 2, 0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEqualsHashBoom3() {
        equalsSameHashCode(p2d,
                           p2d,
                           new Point2d(1, 2),
                           new Point3d(1, 2, 0));
    }

    // Example with a surrogate-key database entity
    static class User {
        private long id;
        private String name;
        private int age;
        User(long theId, String theName, int theAge) {
            id = theId; name = theName; age = theAge;
        }

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }

        @Override public int hashCode() { return (int) id; }

        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if ( !(o instanceof User) ) { return false; }

            User that = (User) o;
            // This is possible, but rarely a good idea.
            // Can you meaningfully compare users that aren't fully created?
            // Or users that aren't saved in the database?  Usually, the answer is no.
//            if (id != 0) {
//                return id == that.id;
//            } else {
//                return name.equals(that.name) &&
//                       age == that.age;
//            }

            // Calling getId() instead of ide here.
            // Many ORM's don't initialize objects until you call getSomething()
            // Or they can give you a surrogate object that behaves strangely.
            long thatId = that.getId();
            return (id != 0L) &&
                   (thatId != 0L) &&
                   id == thatId;

        }
    }

    private final User sally = new User(1L, "Sally", 24);
    private final User fred = new User(2L, "Fred", 23);

    @Test public void testEqHash() {
        equalsDistinctHashCode(sally,
                               new User(1L, "Sally", 24),
                               new User(1L, "Sally", 24),
                               fred);
    }

}