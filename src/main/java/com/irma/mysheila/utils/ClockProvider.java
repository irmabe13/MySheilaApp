package com.irma.mysheila.utils;

import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class ClockProvider {
    public Clock getClock() { return Clock.systemUTC(); }
}
