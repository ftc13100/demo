package org.firstinspires.ftc.teamcode.opModes.teleOp

import com.arcrobotics.ftclib.command.Command
import com.arcrobotics.ftclib.command.CommandOpMode
import com.arcrobotics.ftclib.command.ConditionalCommand
import com.arcrobotics.ftclib.command.InstantCommand
import com.arcrobotics.ftclib.command.ParallelCommandGroup
import com.arcrobotics.ftclib.command.SelectCommand
import com.arcrobotics.ftclib.command.SequentialCommandGroup
import com.arcrobotics.ftclib.command.WaitUntilCommand
import com.arcrobotics.ftclib.command.button.Trigger
import com.arcrobotics.ftclib.gamepad.GamepadEx
import com.arcrobotics.ftclib.gamepad.GamepadKeys
import com.arcrobotics.ftclib.kotlin.extensions.gamepad.and
import com.qualcomm.robotcore.eventloop.opmode.TeleOp
import com.qualcomm.robotcore.util.ElapsedTime
import org.firstinspires.ftc.teamcode.commands.arm.ArmCommand
import org.firstinspires.ftc.teamcode.commands.arm.DefaultArmCommand
import org.firstinspires.ftc.teamcode.commands.arm.OpenArmCommand
import org.firstinspires.ftc.teamcode.commands.arm.SetArmTargetCommand
import org.firstinspires.ftc.teamcode.commands.drive.DriveCommand
import org.firstinspires.ftc.teamcode.commands.elevator.ElevatorCommand
import org.firstinspires.ftc.teamcode.commands.elevator.SpinDownCommand
import org.firstinspires.ftc.teamcode.commands.elevator.SpinUpCommand
import org.firstinspires.ftc.teamcode.commands.intake.IntakeBeltCommand
import org.firstinspires.ftc.teamcode.commands.intake.IntakeCommand
import org.firstinspires.ftc.teamcode.commands.intake.SlowIntakeBeltCommand
import org.firstinspires.ftc.teamcode.commands.intake.ThrowItBackCommand
import org.firstinspires.ftc.teamcode.subsystems.arm.ArmSubsystem
import org.firstinspires.ftc.teamcode.subsystems.drive.DriveSubsystem
import org.firstinspires.ftc.teamcode.subsystems.intake.IntakeSubsystem
import org.firstinspires.ftc.teamcode.subsystems.slides.ElevatorSubsystem

@TeleOp
class MainTeleOp : CommandOpMode() {


    private lateinit var driveCommand: Command


    private lateinit var timer: ElapsedTime

    private lateinit var driver: GamepadEx
    private var driverMode = DRIVER_MODE.SPEED

    private lateinit var operator: GamepadEx


    override fun initialize() {
//        telemetry = MultipleTelemetry(FtcDashboard.getInstance().telemetry, telemetry)

        driver = GamepadEx(gamepad1)
        operator = GamepadEx(gamepad2)

        DriveSubsystem.initialize(hardwareMap)

        timer = ElapsedTime()

        driver.getGamepadButton(GamepadKeys.Button.B).whenPressed(
            InstantCommand({ operatorMode = operatorMode.toggle(operatorMode)})
        )

        driver.getGamepadButton(GamepadKeys.Button.Y).toggleWhenPressed(
            InstantCommand({
                DriveSubsystem.driveMultiplier = 0.35
                driverMode = driverMode.toggle(driverMode)
            }),
            InstantCommand({
                DriveSubsystem.driveMultiplier = 0.9
                driverMode = driverMode.toggle(driverMode)
            })
        )

        DriveSubsystem.defaultCommand = driveCommand
    }

    override fun runOpMode() {
        initialize()

        waitForStart()

        // run the scheduler
        while (!isStopRequested && opModeIsActive()) {
            timer.reset()

            run()

            telemetry.addData("Driver Mode", driverMode)

            telemetry.addData("Time Elapsed", timer.milliseconds())

            telemetry.update()
        }

        reset()
    }

    internal enum class DRIVER_MODE {
        SPEED,
        SLOW;

        fun toggle(mode: DRIVER_MODE) : DRIVER_MODE =
            when (mode) {
                SPEED -> SLOW
                SLOW -> SPEED
            }
    }
}