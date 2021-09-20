package com.as.eventalertandroid.enums;

public enum AppTab {

    HOME(0),
    CREATOR(1),
    NOTIFICATIONS(2),
    PROFILE(3),
    ADMIN(4);

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
                return NOTIFICATIONS;
            case 3:
                return PROFILE;
            case 4:
                return ADMIN;
        }
    }

}
