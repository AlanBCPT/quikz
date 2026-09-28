# Quikz — Spring Boot edition

A server-authoritative Qix-style territory game. Spring Boot owns the grid, the
roaming star ("qix"), the border patrollers ("sparx"), movement, collision, and
the flood-fill claiming rule. The browser client is a thin renderer: it sends
input over WebSocket and draws whatever state the server pushes back.

## Run it

Requires JDK 17+ and Maven.

```bash
mvn spring-boot:run
```

Then open http://localhost:8080 in a browser. Arrow keys / WASD to move,
space to pause (only while a field is in play). Touch controls appear
automatically on touch devices.

```bash
mvn test
```

runs the suite and fails the build if line coverage drops below 85%.

## How it's structured

```
src/main/java/com/example/quikz/
  QuikzApplication.java
  config/                          Spring beans and /ws/game registration
  protocol/                        client JSON in and out
  session/                         one player's commands and clock
  ws/GameWebSocketHandler.java     WebSocket adapter around a GameSession
  game/                            rules, field, player, qix, sparx, flood fill,
                                   claimed coastline patrol

src/main/resources/
  static/index.html                canvas renderer + WebSocket client, no game logic
  application.properties           port 8080
```

Each WebSocket connection gets its own `GameSession`. A scheduled tick loop
(~30 Hz, `GameRules.TICK_PERIOD_MS` = 33) advances that session and pushes
state. Session mutation is synchronized so the message thread and the tick
thread never overlap.

## Play

- Draw a loop out from the claimed border and back. Empty regions that do not
  contain the qix are claimed. Reach 75% claimed to clear the field.
- Three lives. The qix kills you if it touches your open trail; a sparx kills
  you if it shares your cell while you are on claimed ground and not drawing.
- On fields 1 and 2, sparx always walk the original outer rectangle, even after
  you claim land. From field 3 onward, each closed loop rebuilds their path as
  the claimed coastline (claimed cells that touch empty space). If a sparx was
  on a wall that is no longer coastline, it keeps walking the old path in the
  direction it was already going until it meets a cell still on the new coast
  — it does not jump onto the new wall. They do not chase an unfinished trail.
- Each new field (start, continue, retry) picks a random theme: `orange`,
  `blue`, or `green`, and never repeats the previous field's colour. Claimed
  ground is painted in that colour; sparx use a contrasting colour (cyan on
  orange, orange on blue, magenta on green).
- The qix grows 2 pixels every two levels, capped at +10 (levels 11+).
  Inner and outer radii both grow, so the star stays the same shape.

## Protocol

Plain JSON over one WebSocket at `/ws/game`.

**Client → server**
```json
{"type":"start"}
{"type":"dir","dx":1,"dy":0}
{"type":"pause"}
{"type":"next"}
{"type":"retry"}
```

`start` and `retry` lay out a new run from level 1 and start the tick loop.
`next` lays out the next level. `pause` toggles only between `playing` and
`paused`.

**Server → client**, once per field:
```json
{
  "type":"init",
  "cols":64,"rows":44,"cell":10,
  "claimed":[0,1,2],
  "theme":"blue",
  "targetPct":75,
  "tickMs":33
}
```

`theme` is `orange`, `blue`, or `green`. `claimed` is a list of flat cell
indices (`row * cols + column`).

**Server → client**, every tick:
```json
{
  "type":"state",
  "phase":"playing",
  "score":120,"lives":3,"level":1,
  "theme":"blue","pct":18.4,
  "player":{"x":32,"y":40,"px0":32,"py0":41,"drawing":false,"invuln":false},
  "trail":[[32,40],[33,40]],
  "qix":{"x":320.4,"y":210.1,"outerRadius":13,"innerRadius":5},
  "sparx":[[10,0],[54,43]],
  "newlyClaimed":[441,442],
  "event":null
}
```

`phase` is `idle`, `playing`, `paused`, `levelup`, or `over`. `event` is
`null`, `"death"`, `"levelup"`, or `"gameover"`. `newlyClaimed` is only the
cells claimed this tick so the client can paint them incrementally.

`player.px0` / `py0` are the previous cell, used to interpolate motion between
ticks. `qix.outerRadius` / `innerRadius` are in pixels and already include
level growth.

## Notes

- Collision and win/loss are decided on the server. The client does not run
  game rules.
- Movement is 4-directional. Score is flat per claimed cell (no slow-draw
  bonus).
- Not included: persistence (scores reset on restart), auth, or multiplayer
  (each connection is an isolated game).
