package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

import java.awt.Image;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(800);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 15)
                .followTrajectorySequence(drive ->
                        drive.trajectorySequenceBuilder(new Pose2d(0, 60, 0))
                                .splineTo(new Vector2d(49, 24), Math.PI / 2)
                                .splineTo(new Vector2d(56, 56), Math.PI / 2)
                                .splineTo(new Vector2d(60, 24), Math.PI / 2)
                                .turn(Math.toRadians(90))
                                .lineTo(new Vector2d(50,24))
                                .splineTo(new Vector2d(56, 56), Math.PI / 2)
                                .splineTo(new Vector2d(60, 24), Math.PI / 2)
                                .turn(Math.toRadians(-90))
                                .turn(Math.toRadians(180))
                                .lineTo(new Vector2d(50,24))
                                .splineTo(new Vector2d(56, 56), Math.PI / 2)


                                .build()
                );

        Image img = loadCustomBackgroundFromResource("/field-2024-juice-dark.png");
        if (img != null) {
            meepMeep.setBackground(img);
        }
        else {
            meepMeep.setBackground(MeepMeep.Background.FIELD_CENTERSTAGE_JUICE_DARK);
        }
        meepMeep.setDarkMode(true).setBackgroundAlpha(0.95f).addEntity(myBot).start();


    }

    @SuppressWarnings("SameParameterValue")
    private static Image loadCustomBackgroundFromResource(String name) {
        try {
            InputStream is = MeepMeepTesting.class.getResourceAsStream(name);
            assert is != null;
            return ImageIO.read(is);
        }
        catch (Exception e) {
            System.err.println("Unable to load custom background: " + e);
            return null;
        }
    }
}