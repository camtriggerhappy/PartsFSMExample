// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Subsytems;

import java.util.function.BooleanSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.filter.Debouncer;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase {
  /** Creates a new Intake. */

  DigitalInput noteDetector = new DigitalInput(0); // think photoeye or something like that
  Debouncer noteDebouncer = new Debouncer(.02, DebounceType.kBoth); // this makes sure that it doesn't flicker on and
                                                                    // off a ton of times. Especially important for
                                                                    // something like a limit switch.
  IntakeStates currentState = IntakeStates.unknown;
  DesiredAction desiredAction = DesiredAction.nothing;
  CANSparkMax intakeMotor = new CANSparkMax(0, MotorType.kBrushless);

  private static Intake mintake = new Intake();

  public enum IntakeStates { // these states represent whether the Intake already has something in it or not
    Unloaded,
    Loaded,
    unknown

  }

  public enum DesiredAction {
    Shooting,
    Intaking,
    outTaking,
    holding,
    nothing
  }

  public Intake() {

  }

  public static Intake getInstance() {
    return mintake;
  }

  public boolean hasNote() {
    return noteDebouncer.calculate(noteDetector.get());
  }

  public BooleanSupplier hasNoteSupplier() {
    BooleanSupplier s = () -> hasNote();
    return s;
  }

  public Command pickup() {
    return this.run( // use this. so that the command will require the intake subsystem
        () -> {

          intakeMotor.set(1);
        }

    );
  }

  public Command outtake() {
    return this.run(// use this. so that the command will require the intake subsystem
        () -> intakeMotor.set(-1));
  }

  public Command unstickShooter() {
    return this.run(// use this. so that the command will require the intake subsystem
        () -> intakeMotor.set(-1)).withTimeout(.080).andThen(hold());// this is roughly 4 rioloops
  }

  /**
   * 
   * This command applies a small motor power to secure the note in the intake
   */
  public Command hold() {
    return this.run(// use this. so that the command will require the intake subsystem
        () -> intakeMotor.set(.01));
  }

  public void setAction(DesiredAction desired) {
    desiredAction = desired;

  }

  @Override
  public void periodic() {
    /**
     * we always need to know what state the intake is in so we put it in periodic
     */
    if (hasNote()) {
      currentState = IntakeStates.Loaded;
    } else if (!hasNote()) {
      currentState = IntakeStates.Unloaded;
    }

    /**
     * we need to put a state manager here that checks what's going on and decides
     * what needs to happen
     */

    switch (desiredAction) {
      case Intaking:
        if (hasNote()) {
          desiredAction = DesiredAction.holding;
        } else {
          pickup().schedule();
        }
        break;
      case Shooting:
        if (Shooter.getInstance().isReady()) {
          pickup().schedule();
        } else if (Shooter.getInstance().isStuck()) {
          // maybe backup the note bc it could be blocking the wheels? you could add
          // current detection to see if the motors are drawing power and not spinning.
          unstickShooter().schedule();
        }
        break;
      case holding:
        break;
      case nothing:
        this.run(null);
        break;
      case outTaking:
        outtake().schedule();
        break;
    }
  }
}
