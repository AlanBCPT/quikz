package com.example.quikz.game;

import java.util.List;

public record FieldLayout(
        Grid grid,
        Qix qix,
        Perimeter perimeter,
        List<Sparx> patrollers,
        FieldTheme theme) {}
