package org.organicdesign.testUtils;

enum BreakNullSafety {
    INSTANCE;
    public void compareToNull(Comparable<?> comp) {
        comp.compareTo(null);
    }
}
