// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsytems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {

  private double desiredVelocity = 3200; // RPM
  public enum ShooterStates{
    ready,
    notready
  }

  public enum desiredAction{
    shoot,
    nothing;
  }

  CANSparkMax shooterMotor = new CANSparkMax(1, MotorType.kBrushless);
  /** Creates a new Shooter. */
  public Shooter() {}


  public boolean isReady(){
    return shooterMotor.getEncoder().getVelocity() >  desiredVelocity;
  }

  @Override
  public void periodic() {
    //TODO: add settign the state ready or not

    // This method will be called once per scheduler run
  }
}
