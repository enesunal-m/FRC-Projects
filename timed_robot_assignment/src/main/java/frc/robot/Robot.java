/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2020 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the TimedRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  // motor definitions
  private final Spark m_frontLeft = new Spark(1);
  private final Spark m_rearLeft = new Spark(2);
  private final SpeedControllerGroup m_left = new SpeedControllerGroup(m_frontLeft, m_rearLeft);

  private final Spark m_frontRight = new Spark(3);
  private final Spark m_rearRight = new Spark(4);
  private final SpeedControllerGroup m_right = new SpeedControllerGroup(m_frontRight, m_rearRight);

  private final DifferentialDrive m_drive = new DifferentialDrive(m_left, m_right);
  
  // drive options
  private static final boolean squareInputs = true;
  private static final double 
  leftSpeed = 0.5, 
  rightSpeed = 0.5;

  // general options
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  // other stuffs
  private final Joystick m_stick = new Joystick(0);
  private final Timer m_timer = new Timer();

  private final Solenoid m_solenoid_1 = new Solenoid(1);
  
  /**
   * This function is run when the robot is first started up and should be
   * used for any initialization code.
   */
  @Override
  public void robotInit() {
    m_drive.setExpiration(0.1);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use
   * this for items like diagnostics that you want ran during disabled,
   * autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before
   * LiveWindow and SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different autonomous modes using the dashboard. The sendable
   * chooser code works with the Java SmartDashboard. If you prefer the
   * LabVIEW Dashboard, remove all of the chooser code and uncomment the
   * getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to
   * the switch structure below with additional strings. If using the
   * SendableChooser make sure to add them to the chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    m_timer.start();
    m_drive.setSafetyEnabled(false);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /**
   * This function is called periodically during autonomous.
   */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        break;
      case kDefaultAuto:
      default:
        // tank drive until timer value = 4
        if (m_timer.get() <= 4) 
        {
          m_drive.tankDrive(leftSpeed, rightSpeed, squareInputs);
        } else m_drive.tankDrive(0, 0, squareInputs);
        break;
    }
  }

  /**
   * This function is called once when teleop is enabled.
   */
  @Override
  public void teleopInit() {
    m_drive.setSafetyEnabled(true);
  }

  /**
   * This function is called periodically during operator control.
   */
  @Override
  public void teleopPeriodic() {
    // tank drive with joystick controller
    m_drive.tankDrive(m_stick.getRawAxis(1), m_stick.getRawAxis(3));
    // when cilcked button 3, open solenoid_1
    if (m_stick.getRawButton(3)) {
      m_solenoid_1.set(true);
      // if buttons 3 and 4 are clicked together, start only frontleft motor
      if (m_stick.getRawButton(4)) {
        m_frontLeft.set(1);
      }
      
    } else m_solenoid_1.set(false);
  }

  /**
   * This function is called once when the robot is disabled.
   */
  @Override
  public void disabledInit() {
  }

  /**
   * This function is called periodically when disabled.
   */
  @Override
  public void disabledPeriodic() {
  }

  /**
   * This function is called once when test mode is enabled.
   */
  @Override
  public void testInit() {
  }

  /**
   * This function is called periodically during test mode.
   */
  @Override
  public void testPeriodic() {
  }
  
  
}
