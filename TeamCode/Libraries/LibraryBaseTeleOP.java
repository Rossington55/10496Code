package org.firstinspires.ftc.teamcode.Libraries;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;

/**
 * Created by Ari on 10-10-17.
 */

public abstract class LibraryBaseTeleOP extends OpMode {

    // Define the base motors. "null" is used to clear any potential cache's of motors.
    public static DcMotor topLeft     = null;
    public static DcMotor topRight    = null;
    public static DcMotor backLeft    = null;
    public static DcMotor backRight   = null;

    // Define other motors.
    public static DcMotor conveyor    = null;
    public static DcMotor intake      = null;
    public static DcMotor relicExtend = null;
    public static DcMotor relicRaise  = null;
    public static Servo   relicClaw        = null;


    // Gyro stuff.
    private static BNO055IMU imu       = null;
    public static Orientation angles;
    public static double gyroyaw       = 0;

    // Quick way of checking if the motors *should* be init'ed.
    private boolean baseInited         = false;

    // Axial / Lateral / Yaw values.
    private double driveAxial, driveLateral, driveYaw;




    // Init the base, and reset all the motors.
    public void initBase() {
        telemetry.addData("STATUS: ", "Initialising Base.");
        topLeft   = hardwareMap.dcMotor.get("topLeft");
        topRight  = hardwareMap.dcMotor.get("topRight");
        backLeft  = hardwareMap.dcMotor.get("backLeft");
        backRight = hardwareMap.dcMotor.get("backRight");

        topLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        topRight.setDirection(DcMotorSimple.Direction.FORWARD);
        backLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        backRight.setDirection(DcMotorSimple.Direction.FORWARD);

        resetAllBaseMotors(true);
        baseInited = true;

    }

    public void initConveyor(){
        telemetry.addData("STATUS: ", "Initialising Conveyor.");
        conveyor = hardwareMap.dcMotor.get("conveyor");
        //intake = hardwareMap.dcMotor.get("intake");

        conveyor.setDirection(DcMotorSimple.Direction.FORWARD);
        //intake.setDirection(DcMotorSimple.Direction.FORWARD);

        resetMotor(conveyor, false);
        //resetMotor(intake, true);
    }

    public void initRelic(){
        telemetry.addData("STATUS: ", "Initialising Relic Arms.");
        relicExtend = hardwareMap.dcMotor.get("relicArm");
        relicRaise  = hardwareMap.dcMotor.get("relicRaise");
        relicClaw   = hardwareMap.servo.get("relicClaw");

        relicExtend.setDirection(DcMotorSimple.Direction.FORWARD);
        relicRaise.setDirection(DcMotorSimple.Direction.FORWARD);
        relicClaw.setPosition(0.1);

        resetMotor(relicExtend, false);
        resetMotor(relicRaise, true);

    }

    public void setMoveRobot(double axial, double lateral, double yaw ){
        setAxial(axial);
        setLateral(lateral);
        setYaw(yaw);
        moveRobot();
    }

    public void moveRobot() {
        // Calculate the required motor speeds to achieve axis motions.
        // Remember, motors spin CCW.

        double topLeftPower = driveYaw - driveAxial - driveLateral; // Top left motor.
        double topRightPower = driveYaw + driveAxial - driveLateral; // Top right motor.
        double backLeftPower = driveYaw - driveAxial + driveLateral; // Bottom left motor.
        double backRightPower = driveYaw + driveAxial + driveLateral; // Bottom right power.


        // Normalise all the motor speeds to no values exceed 100% (which is really 1.0)
        // The /= compound operator means divide the left value by the right, and assign that number into the left value.
        double max = Math.max(Math.abs(topLeftPower), Math.abs(topRightPower)); // See which value is larger: topLeft or topRight.
        max = Math.max(max, Math.abs(backLeftPower)); // See which value is larger: The above max or bottomLeftPower
        max = Math.max(max, Math.abs(backRightPower)); // See which value is larger: The above max or bottomRightPower
        if (max > 1.0){ // If the max value is greater than 1, then for each variable, divide it by the "max" and set it to that value.
            topLeftPower /= max;
            topRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }

        topLeft.setPower(topLeftPower);
        topRight.setPower(topRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);

        telemetry.addData("topLeft: ", topLeft.getPower());
        telemetry.addData("topRight: ", topRight.getPower());
        telemetry.addData("backLeft: ", backLeft.getPower());
        telemetry.addData("backRight: ", backRight.getPower());
    }

    public void moveRobotEncoders(double Axial, double Lateral, double Yaw){
        double topLeftPower   = Yaw - Axial - Lateral;
        double topRightPower  = Yaw + Axial - Lateral;
        double backLeftPower  = Yaw - Axial + Lateral;
        double backRightPower = Yaw + Axial + Lateral;

        // Normalise all the motor speeds to no values exceed 100% (which is really 1.0)
        // The /= compound operator means divide the left value by the right, and assign that number into the left value.
        double max = Math.max(Math.abs(topLeftPower), Math.abs(topRightPower)); // See which value is larger: topLeft or topRight.
        max = Math.max(max, Math.abs(backLeftPower)); // See which value is larger: The above max or bottomLeftPower
        max = Math.max(max, Math.abs(backRightPower)); // See which value is larger: The above max or bottomRightPower
        if (max > 1.0){ // If the max value is greater than 1, then for each variable, divide it by the "max" and set it to that value.
            topLeftPower /= max;
            topRightPower /= max;
            backLeftPower /= max;
            backRightPower /= max;
        }




        topLeft.setPower(topLeftPower);
        topRight.setPower(topRightPower);
        backLeft.setPower(backLeftPower);
        backRight.setPower(backRightPower);




        telemetry.addData("topLeft: ", topLeft.getPower());
        telemetry.addData("topRight: ", topRight.getPower());
        telemetry.addData("backLeft: ", backLeft.getPower());
        telemetry.addData("backRight: ", backRight.getPower());

    }

    // Create the functions that we use to set variables. Also, sets the range that the variable can be set to.
    public void setAxial(double axial){
        driveAxial = Range.clip(axial, -1, 1);}

    public void setLateral(double lateral){
        driveLateral = Range.clip(lateral, -1, 1); }

    public void setYaw(double yaw){
        driveYaw = Range.clip(yaw, -1, 1); }


    // Quick way of resetting a motor.
    public void resetMotor(DcMotor motor, boolean withEncoder){
        motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    // Reset all the motors for the holonomic base.
    public void resetAllBaseMotors(boolean withEncoder) {
        if (baseInited){

            resetMotor(topLeft, true);
            resetMotor(topRight, true);
            resetMotor(backLeft, true);
            resetMotor(backRight, true);

        }
        else {
            telemetry.addData("ERROR: ", "Motors not defined. Failed resetting.");
            telemetry.update();
        }
    }


    public void initGyro() {
        telemetry.addData("STATUS: ", "Initialising Gyro.");
        BNO055IMU.Parameters gyroParam = new BNO055IMU.Parameters();
        gyroParam.angleUnit = BNO055IMU.AngleUnit.DEGREES;
        gyroParam.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        gyroParam.calibrationDataFile = "AdafruitIMUCalibration.json";
        gyroParam.loggingEnabled = true;
        gyroParam.loggingTag = "Gyro";
        gyroParam.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(gyroParam);
    }

    // updateGyro must be called every update loop.
    public void updateGyro(){
        angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);
    }

    public void moveConveyor(double power){
        conveyor.setPower(power);
        telemetry.addData("Conveyor Power: ", power);
    }

    public void moveIntake(double power){
        intake.setPower(power);
        telemetry.addData("Intake Power:", power);
    }

    public void grabRelic(boolean shouldGrab){
        if (shouldGrab){
            relicClaw.setPosition(0.9);
        }
        else{
            relicClaw.setPosition(0.1);
        }
    }

    public void moveRelicArm(double power){
        relicExtend.setPower(power);
    }

    public void raiseRelicArm(double power){
        relicRaise.setPower(power);
    }


}
