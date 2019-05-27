package com.example.eventbusdemo;

/**
 * @author: lilinjie
 * @date: 2019-05-23 09:27
 * @description:
 */
public class Event {
    private String name;

    public Event(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
