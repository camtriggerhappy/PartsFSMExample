// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsytems;

import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Shooter extends SubsystemBase {


  
  private double desiredVelocity = 3200; // RPM
  public enum ShooterStates{
    ready,
    notready
  }

  public enum DesiredAction{
    shoot,
    nothing;
  }

  static Shooter mShooter = new Shooter();
  public double previousRPM = 0;
  CANSparkMax shooterMotor = new CANSparkMax(1, MotorType.kBrushless);
  private DesiredAction desiredAction = DesiredAction.nothing;
  /** Creates a new Shooter. */
  public Shooter() {}


  public boolean isReady(){
    return shooterMotor.getEncoder().getVelocity() >  desiredVelocity;
  }

  public static Shooter getInstance(){
    return mShooter;
  }

  public double getAcceleration(){
    double currentRPMPM = (shooterMotor.getEncoder().getVelocity() - previousRPM)/(.020 * 60); // .020 * 60 gives the number of minutes since the last nominal loop
    previousRPM = shooterMotor.getEncoder().getVelocity();
    return currentRPMPM;
  }

  public DoubleSupplier accelerationSupplier(){
    return () -> getAcceleration();
  }

  public boolean isStuck(){
    //if its barely spinning,  not getting faster, pulling a good amount of power, and trying to move its probably stuck;
    if((Math.abs(shooterMotor.getEncoder().getVelocity()) < 100) && 
    (Math.abs(shooterMotor.getOutputCurrent()) > 4) && (Math.abs(getAcceleration()) > 30) && (shooterMotor.getAppliedOutput() > .03)){
      return true; 
    }
    return false;
  }

  @Override
  public void periodic() {
    getAcceleration(); // this needs to be updated constantly since it relies on the change in time being constant
    switch (desiredAction) {
      case nothing:
      this.getCurrentCommand().cancel();
      case shoot:
        break;
    }
  }


  public void setAction(DesiredAction newDesire) {
    desiredAction = newDesire;
    
  }
}
