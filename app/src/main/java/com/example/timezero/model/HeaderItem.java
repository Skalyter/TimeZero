package com.example.timezero.model;

public class HeaderItem extends ListItem {
    private String header;

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public int getType() {
        return TYPE_HEADER;
    }
}
