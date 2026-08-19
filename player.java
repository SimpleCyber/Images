package game;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Player extends GameEntity implements Damageable {

    // ❤️ PLAYER HEALTH
    private int health = 100;

    // 🚶 PLAYER MOVEMENT
    private int walkSpeed = 3;
    private int runSpeed = 6;
    private boolean running = false;

    // 🎯 LAST PLAYER DIRECTION
    private int directionX = 1;
    private int directionY = 0;

    // 🔫 SHOOTING
    private boolean shooting = false;

    // 🔴 CURRENT BULLET
    private Bullet bullet;

    // 🖼️ PLAYER SPRITE SHEET
    private BufferedImage spriteSheet;

    // 🖼️ CURRENT PLAYER FRAME
    private BufferedImage currentFrame;

    // 🎬 CURRENT FRAME INDEX
    private int currentFrameIndex = 0;

    // ⏱️ ANIMATION TIMER
    private long lastFrameTime = 0;

    // ⏱️ FRAME DURATION
    private long frameDuration = 120;

    // 🚶 WALK FRAMES
    private BufferedImage[] walkFrames = new BufferedImage[7];

    // 🏃 RUN FRAMES
    private BufferedImage[] runFrames = new BufferedImage[6];

    // 🔫 SHOOT FRAMES
    private BufferedImage[] shootFrames = new BufferedImage[6];

    // 💥 HURT FRAMES
    private BufferedImage[] hurtFrames = new BufferedImage[4];

    // ☠️ DEAD FRAMES
    private BufferedImage[] deadFrames = new BufferedImage[4];

    // ⛽ COLLECT FRAMES
    private BufferedImage[] collectFrames = new BufferedImage[5];

    // 🎬 PLAYER ANIMATION STATES
    private enum AnimationState {
        WALK,
        RUN,
        SHOOT,
        HURT,
        DEAD,
        COLLECT
    }

    // 🎬 CURRENT ANIMATION STATE
    private AnimationState animationState = AnimationState.WALK;

    // 🔄 PREVIOUS ANIMATION STATE
    private AnimationState previousAnimationState = AnimationState.WALK;

    // 🏗️ PLAYER CONSTRUCTOR
    public Player(int x, int y) {

        super(x, y, 50, 50);

        // 🖼️ LOAD PLAYER SPRITE SHEET
        try {

            spriteSheet = ImageIO.read(
                Player.class.getResourceAsStream(
                    "/game/resources/player.png"
                )
            );

            System.out.println(
                "Player sprite loaded: "
                + (spriteSheet != null)
            );

        } catch (Exception e) {

            e.printStackTrace();
        }

        // =====================================================
        // 🖼️ SPRITE SHEET SETTINGS
        // =====================================================

        int frameWidth = 150;
        int frameHeight = 180;

        int walkY = 0;
        int runY = 180;
        int shootY = 360;
        int hurtY = 540;
        int deadY = 720;
        int collectY = 900;

        // 🚶 LOAD WALK FRAMES
        for (int i = 0; i < walkFrames.length; i++) {

            walkFrames[i] = spriteSheet.getSubimage(
                i * frameWidth,
                walkY,
                frameWidth,
                frameHeight
            );
        }

        // 🏃 LOAD RUN FRAMES
        for (int i = 0; i < runFrames.length; i++) {

            runFrames[i] = spriteSheet.getSubimage(
                i * frameWidth,
                runY,
                frameWidth,
                frameHeight
            );
        }

        // 🔫 LOAD SHOOT FRAMES
        for (int i = 0; i < shootFrames.length; i++) {

            shootFrames[i] = spriteSheet.getSubimage(
                i * frameWidth,
                shootY,
                frameWidth,
                frameHeight
            );
        }

        // 💥 LOAD HURT FRAMES
        for (int i = 0; i < hurtFrames.length; i++) {

            hurtFrames[i] = spriteSheet.getSubimage(
                i * frameWidth,
                hurtY,
                frameWidth,
                frameHeight
            );
        }

        // ☠️ LOAD DEAD FRAMES
        for (int i = 0; i < deadFrames.length; i++) {

            deadFrames[i] = spriteSheet.getSubimage(
                i * frameWidth,
                deadY,
                frameWidth,
                frameHeight
            );
        }

        // ⛽ LOAD COLLECT FRAMES
        for (int i = 0; i < collectFrames.length; i++) {

            collectFrames[i] = spriteSheet.getSubimage(
                i * frameWidth,
                collectY,
                frameWidth,
                frameHeight
            );
        }

        // 🖼️ START WITH FIRST WALK FRAME
        currentFrame = walkFrames[0];
    }

    // =====================================================
    // 🚶🏃 PLAYER UPDATE
    // =====================================================

    public void update(KeyHandler keyHandler) {

        // ☠️ DEAD PLAYER CANNOT MOVE
        if (health <= 0) {
            return;
        }

        // 🏃 CURRENT SPEED
        int currentSpeed = getMovementSpeed();

        // 🚶 MOVEMENT STATE
        boolean moving = false;

        // ⬆️ MOVE UP
        if (keyHandler.up) {

            y -= currentSpeed;

            directionX = 0;
            directionY = -1;

            moving = true;
        }

        // ⬇️ MOVE DOWN
        if (keyHandler.down) {

            y += currentSpeed;

            directionX = 0;
            directionY = 1;

            moving = true;
        }

        // ⬅️ MOVE LEFT
        if (keyHandler.left) {

            x -= currentSpeed;

            directionX = -1;
            directionY = 0;

            moving = true;
        }

        // ➡️ MOVE RIGHT
        if (keyHandler.right) {

            x += currentSpeed;

            directionX = 1;
            directionY = 0;

            moving = true;
        }

        // 🚶🏃 SELECT MOVEMENT ANIMATION
        if (!shooting && animationState != AnimationState.HURT) {

            if (moving) {

                if (running) {
                    animationState = AnimationState.RUN;
                } else {
                    animationState = AnimationState.WALK;
                }
            }
        }

        // 🔴 UPDATE BULLET
        if (bullet != null) {
            bullet.update();
        }
    }

    // =====================================================
    // 🚶🏃 RUN TOGGLE
    // =====================================================

    public void toggleRun() {

        running = !running;

        System.out.println(
            running ? "RUN MODE" : "WALK MODE"
        );
    }

    // 🏃 GET CURRENT MOVEMENT SPEED
    public int getMovementSpeed() {

        if (running) {
            return runSpeed;
        }

        return walkSpeed;
    }

    // =====================================================
    // 🔫 SHOOT
    // =====================================================

    public void shoot() {

        if (health <= 0) {
            return;
        }

        // 🔫 START SHOOT ANIMATION
        animationState = AnimationState.SHOOT;

        shooting = true;

        // 🔴 CREATE BULLET
        bullet = new Bullet(
            x + width / 2,
            y + height / 2,
            directionX,
            directionY
        );

        System.out.println("PLAYER SHOOT");
    }

    // =====================================================
    // 🎬 UPDATE PLAYER ANIMATION
    // =====================================================

    public void updateAnimation() {

        long currentTime = System.currentTimeMillis();

        // 🔄 STATE CHANGED
        if (animationState != previousAnimationState) {

            currentFrameIndex = 0;
            lastFrameTime = currentTime;

            previousAnimationState = animationState;
        }

        // 🎬 SELECT FRAME ARRAY
        BufferedImage[] frames;

        switch (animationState) {

            case RUN:
                frames = runFrames;
                break;

            case SHOOT:
                frames = shootFrames;
                break;

            case HURT:
                frames = hurtFrames;
                break;

            case DEAD:
                frames = deadFrames;
                break;

            case COLLECT:
                frames = collectFrames;
                break;

            default:
                frames = walkFrames;
                break;
        }

        // 🎞️ ADVANCE FRAME
        if (currentTime - lastFrameTime >= frameDuration) {

            currentFrameIndex++;

            // 🔄 ANIMATION FINISHED
            if (currentFrameIndex >= frames.length) {

                // ☠️ DEAD STAYS ON LAST FRAME
                if (animationState == AnimationState.DEAD) {

                    currentFrameIndex = frames.length - 1;

                } else {

                    currentFrameIndex = 0;

                    // 🔫 SHOOT FINISHED
                    if (animationState == AnimationState.SHOOT) {

                        shooting = false;

                        if (running) {
                            animationState = AnimationState.RUN;
                        } else {
                            animationState = AnimationState.WALK;
                        }
                    }

                    // 💥 HURT FINISHED
                    else if (animationState == AnimationState.HURT) {

                        if (running) {
                            animationState = AnimationState.RUN;
                        } else {
                            animationState = AnimationState.WALK;
                        }
                    }
                }
            }

            currentFrame = frames[currentFrameIndex];

            lastFrameTime = currentTime;
        }
    }

    // =====================================================
    // 💥 PLAYER TAKES DAMAGE
    // =====================================================

    @Override
    public void takeDamage(int damage) {

        if (health <= 0) {
            return;
        }

        health -= damage;

        System.out.println(
            "Player Health: " + health
        );

        // ☠️ PLAYER DIES
        if (health <= 0) {

            health = 0;

            animationState = AnimationState.DEAD;

            return;
        }

        // 💥 PLAYER HURT
        animationState = AnimationState.HURT;
    }

    // ❤️ GET PLAYER HEALTH
    @Override
    public int getHealth() {

        return health;
    }

    // ☠️ CHECK PLAYER DEATH
    @Override
    public boolean isDead() {

        return health <= 0;
    }

    // =====================================================
    // 🎨 DRAW PLAYER
    // =====================================================

    @Override
    public void draw(Graphics g) {

        // 🧑 DRAW PLAYER SPRITE
        if (currentFrame != null) {

            g.drawImage(
                currentFrame,
                x,
                y,
                width,
                80,
                null
            );
        }

        // 🔴 DRAW BULLET
        if (bullet != null) {

            bullet.draw(g);
        }
    }

    // ❤️ GETTER
    public int getHealthCount() {
        return health;
    }

    // 🎯 GET DIRECTION X
    public int getDirectionX() {
        return directionX;
    }

    // 🎯 GET DIRECTION Y
    public int getDirectionY() {
        return directionY;
    }
}