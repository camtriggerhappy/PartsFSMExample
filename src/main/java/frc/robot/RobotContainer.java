// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Subsytems.Intake;
import frc.robot.Subsytems.Shooter;
import frc.robot.Subsytems.Intake.DesiredAction;

public class RobotContainer {
  CommandXboxController driverController = new CommandXboxController(0);
  CommandXboxController operator = new CommandXboxController(1);
  Intake intake = Intake.getInstance();

  public Command shoot(){
    return new ParallelCommandGroup(
      new InstantCommand(() -> Intake.getInstance().setAction(DesiredAction.Shooting)),
      new InstantCommand(() -> Shooter.getInstance().setAction(frc.robot.Subsytems.Shooter.DesiredAction.shoot))
    );
  }

  public RobotContainer() {
    configureBindings();
  }

  private void configureBindings() {

      operator.a().onTrue(new InstantCommand(() -> intake.setAction(DesiredAction.Intaking)));
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
