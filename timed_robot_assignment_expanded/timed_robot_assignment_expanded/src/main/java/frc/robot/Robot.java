/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.geometry.Pose2d;
import edu.wpi.first.wpilibj.geometry.Rotation2d;
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Timer;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  // motor & motor group definitions
  private final Spark m_frontLeft = new Spark(1);
  private final Spark m_rearLeft = new Spark(2);
  private final SpeedControllerGroup m_gleft = new SpeedControllerGroup(m_frontLeft, m_rearLeft);

  private final Spark m_frontRight = new Spark(3);
  private final Spark m_rearRight = new Spark(4);
  private final SpeedControllerGroup m_gright = new SpeedControllerGroup(m_frontRight, m_rearRight);

  private final Spark m_redline = new Spark(5);

  // Differential drive definition
  private final DifferentialDrive m_drive = new DifferentialDrive(m_gleft, m_gright);

  // Kinematics, Odometry & Ultrasonic
  /* --------------------- */
  private Pose2d m_pose = new Pose2d(5.0, 13.5, new Rotation2d());
  /**
   * Odometry definiton:
   * This part is for track the position of our robot and a more accurate drive.
   */
  // starting positions
  private DifferentialDriveOdometry m_odometry =
   new DifferentialDriveOdometry(new Rotation2d(0, 0), m_pose);

  // Gyro definition
  private final ADXRS450_Gyro m_gyro = new ADXRS450_Gyro();

  // Encoder definitons
  private final Encoder m_leftEncoder = new Encoder(5,6);
  private final Encoder m_rightEncoder = new Encoder(7, 8);  

  // Ultrasonic definiton
  private final Ultrasonic m_ultrasonic = new Ultrasonic(1, 2);
  /* -------------------- */

  // general options
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  // drive options
  private static final boolean squareInputs = true;
  private static final double 
  leftSpeed = 0.8, 
  rightSpeed = 0.8;
  private static final double
   targetDistance = 5.0, maxOut = 0.7;
  private boolean isUltrasonicOK = true;

  // other stuffs
  private final Timer m_timer = new Timer();
  private final Joystick m_stick = new Joystick(0);

  @Override
  public void robotInit() {
    m_drive.setExpiration(0.1);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);

    // set ultrasonic to automatic mode for best experience (docs.wpilib.org says) 
    m_ultrasonic.setAutomaticMode(true);

    // set max output for 4 motors
    m_drive.setMaxOutput(maxOut);
  }

  @Override
  public void robotPeriodic() {
    var gyroAngle = Rotation2d.fromDegrees(-m_gyro.getAngle());
    m_pose = m_odometry.update(gyroAngle, m_leftEncoder.getDistance(), m_rightEncoder.getDistance());

    // periodicly checking and logging ultrasonic
    if(m_ultrasonic.getRangeMM() > 100) isUltrasonicOK = true;
    else isUltrasonicOK = false;
  }

  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    m_timer.start();
    m_drive.setSafetyEnabled(false);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        
        break;

      case kDefaultAuto:
        // traveled distance calculation
        double leftDist = m_leftEncoder.getDistance();
        double rightDist = m_rightEncoder.getDistance();
        double t_distance = (leftDist + rightDist)/2;
        // double estimatedDist = Math.sqrt((x - s_X)*(x - s_X) + (y - s_Y)*(y - s_Y));

        // autonomous tank drive to the specified distance : for this practice it's 5 meters.
        if (t_distance <= targetDistance && isUltrasonicOK) 
        {
          m_drive.tankDrive(leftSpeed, rightSpeed, squareInputs);
        } else m_drive.tankDrive(0, 0, squareInputs);
        break;

      default:
        break;
    }
  }

  @Override
  public void teleopInit() {
    m_drive.setSafetyEnabled(true);
  }

  @Override
  public void teleopPeriodic() {
    // tank drive with joystick controller and check the ultrasonic
    if(isUltrasonicOK) 
      m_drive.tankDrive(m_stick.getRawAxis(1), m_stick.getRawAxis(3));

    // when clicked button 3, run redline motor on 0.65 voltage
    if (m_stick.getRawButton(3) && isUltrasonicOK) {
      m_redline.setSpeed(0.65);
    } else m_redline.setSpeed(0);
  }

  @Override
  public void disabledInit() {
  }

  @Override
  public void disabledPeriodic() {
  }

  @Override
  public void testInit() {
  }

  @Override
  public void testPeriodic() {
  }

}
