package com.as.eventalertandroid.enums;

public enum AppTab {

    HOME(0),
    CREATOR(1),
    ADMIN(2),
    PROFILE(3);

    private int index;

    AppTab(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public static AppTab getByIndex(int id) {
        switch (id) {
            default:
                return HOME;
            case 1:
                return CREATOR;
            case 2:
                return ADMIN;
            case 3:
                return PROFILE;
        }
    }

}
