package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@TeleOp(name = "Sensor: HuskyLens", group = "Sensor")
public class SensorHuskyLens extends LinearOpMode {

    private final int READ_PERIOD = 1;
    private HuskyLens huskyLens;
    private ElapsedTime nClock = new ElapsedTime();  // Initialize the ElapsedTime clock

    // Constants for screen dimensions and crosshair
    private final int SCREEN_WIDTH = 320;
    private final int SCREEN_HEIGHT = 240;
    private final int CROSSHAIR_X = SCREEN_WIDTH / 2;   // 160
    private final int CROSSHAIR_Y = SCREEN_HEIGHT / 2;  // 120

    @Override
    public void runOpMode() {
        // Initialize HuskyLens, ensure it's configured correctly in the Control Hub
        huskyLens = hardwareMap.get(HuskyLens.class, "huskylens");

        // Set the read rate limit for reading blocks from the sensor
        Deadline rateLimit = new Deadline(READ_PERIOD, TimeUnit.SECONDS);
        rateLimit.expire(); // Expire immediately so the first read happens

        // Check communication with the HuskyLens
        if (!huskyLens.knock()) {
            telemetry.addData(">>", "Problem communicating with " + huskyLens.getDeviceName());
        } else {
            telemetry.addData(">>", "Press start to continue");
        }

        // Set the HuskyLens to recognize colors
        huskyLens.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);

        telemetry.update();
        waitForStart();

        // Main loop for detecting objects or colors
        while (opModeIsActive()) {
            if (!rateLimit.hasExpired()) {
                continue;
            }
            rateLimit.reset();  // Reset the rate limit for the next reading

            // Start the clock and try to find blocks for up to 5 seconds
            nClock.reset();
            while (nClock.seconds() < 5 && opModeIsActive()) {
                HuskyLens.Block[] blocks = huskyLens.blocks();  // Read detected blocks

                // If blocks are detected, log details of each block
                if (blocks.length > 0) {
                    telemetry.addData("Block count", blocks.length);

                    for (int i = 0; i < blocks.length; i++) {
                        // Only process the block with ID 3 (Yellow)
                        if (blocks[i].id == 3) {
                            int centerX = blocks[i].x;  // Center x of the block
                            int centerY = blocks[i].y;  // Center y of the block
                            int width = blocks[i].width;
                            int height = blocks[i].height;

                            // Calculate the top-left and bottom-right corners of the bounding box
                            int x1 = centerX - (width / 2);   // Top-left x
                            int y1 = centerY - (height / 2);  // Top-left y
                            int x2 = centerX + (width / 2);   // Bottom-right x
                            int y2 = centerY + (height / 2);  // Bottom-right y

                            // Determine orientation: Horizontal or Vertical
                            String orientation = (width > height) ? "Horizontal" : "Vertical";

                            // Calculate the distance from the center of the block to the crosshair
                            int distanceX = CROSSHAIR_X - centerX;
                            int distanceY = CROSSHAIR_Y - centerY;

                            // Display bounding box, orientation, and distance from crosshair
                            telemetry.addData("Block Info", "Center: (%d, %d), Orientation: %s", centerX, centerY, orientation);
                            telemetry.addData("Bounding Box", "Top-Left: (%d, %d), Bottom-Right: (%d, %d)", x1, y1, x2, y2);
                            telemetry.addData("Distance from Crosshair", "X: %d, Y: %d", distanceX, distanceY);

                            // Check if the block is centered under the crosshair
                            if (Math.abs(distanceX) < 10 && Math.abs(distanceY) < 10) {
                                telemetry.addData("Status", "Block is centered under the crosshair!");
                            } else {
                                telemetry.addData("Status", "Block is off-center. Adjust by X: %d, Y: %d", distanceX, distanceY);
                            }

                        }
                    }
                } else {
                    telemetry.addData("No blocks detected", "");
                }

                telemetry.update();
            }
        }
    }
}
