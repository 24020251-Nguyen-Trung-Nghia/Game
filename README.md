## Arkanoid

e.g. `/home/YOUR_USERNAME/akanoid`
```properties
highscore=0
ball_speed=3.0
bonus_block_speed=3.0
enemy_speed=3.0
```

### Gameplay
- Space key to start the game or shoot (when laser paddle)
- Move paddle to the left: Arrow left or A key
- Move paddle to the right: Arrow right or D key
- Shoot: Space bar
- Paddle can also be moved by dragging it with the mouse
- The ball can get some spin when the paddle is moved when they have contact

### Bonus Blocks
- C (color lime)        -> catch ball (for next ball)
- D (color cyan)        -> additional balls (max 3)
- F (color dark blue)   -> wide paddle (for 30 seconds)
- L (color red)         -> laser paddle (for 30 seconds)
- S (color dark yellow) -> slow down (for 30 seconds)
- B (color magenta)     -> opens door to next level (for 5 seconds)
- P (color gray)        -> additional life (max 5)

### Blocks
- white    -> 50 points 
- orange   -> 60 points 
- cyan     -> 70 points 
- lime     -> 80 points 
- red      -> 90 points
- blue     -> 100 points 
- magenta  -> 110 points
- yellow   -> 120 points
- gray     -> needs 2 hits to destroy it (increasing by 1 ever 8 levels) -> 50 x level
- gold     -> can't be destroyed
