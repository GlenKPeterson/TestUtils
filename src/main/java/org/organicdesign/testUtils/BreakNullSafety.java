package org.organicdesign.testUtils;

enum BreakNullSafety {
    INSTANCE;
    @SuppressWarnings({"ResultOfMethodCallIgnored", "ConstantConditions"})
    public void compareToNull(Comparable<?> comp) {
        comp.compareTo(null);
    }
}
