package com.cydeo.enums;

public enum Months {
    JANUARY(1,"January"),
    FEBRUARY(2,"February"),
    MARCH(3,"March"),
    APRIL(4,"April"),
    MAY(5,"May"),
    JUNE(6,"June"),
    JULY(7,"July"),
    AUGUST(8,"August"),
    SEPTEMBER(9,"September"),
    OCTOBER(10,"October"),
    NOVEMBER(11,"November"),
    DECEMBER(12,"December");

    private final String value;
    private final int id;

    Months(int id,String value) {
        this.value = value;
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
