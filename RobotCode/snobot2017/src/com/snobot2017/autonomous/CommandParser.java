package com.snobot2017.autonomous;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.snobot.lib.autonomous.ACommandParser;
import com.snobot.lib.motion_profile.ISetpointIterator;
import com.snobot.lib.motion_profile.PathConfig;
import com.snobot.lib.motion_profile.PathGenerator;
import com.snobot.lib.motion_profile.PathSetpoint;
import com.snobot.lib.motion_profile.StaticSetpointIterator;
import com.snobot2017.Properties2017;
import com.snobot2017.SmartDashBoardNames;
import com.snobot2017.Snobot2017;
import com.snobot2017.autonomous.AutonomousFactory.StartingPositions;
import com.snobot2017.autonomous.commands.DriveStraightADistance;
import com.snobot2017.autonomous.commands.DriveToPegUsingVision;
import com.snobot2017.autonomous.commands.GoToPositionInSteps;
import com.snobot2017.autonomous.commands.GoToPositionSmoothly;
import com.snobot2017.autonomous.commands.RaiseGear;
import com.snobot2017.autonomous.commands.Replay;
import com.snobot2017.autonomous.commands.LowerGear;
import com.snobot2017.autonomous.commands.StupidDriveStraight;
import com.snobot2017.autonomous.commands.TrajectoryWithVisionOverride;
import com.snobot2017.autonomous.commands.TurnWithDegrees;
import com.snobot2017.autonomous.commands.position_based.DriveStraightPathWithGyroFromStartingPosition;
import com.snobot2017.autonomous.commands.position_based.TurnToPegAfterPathFromStartingPosition;
import com.snobot2017.autonomous.path.DriveStraightPath;
import com.snobot2017.autonomous.path.DriveStraightPathWithGyro;
import com.snobot2017.autonomous.path.DriveTurnPath;
import com.snobot2017.autonomous.trajectory.TrajectoryPathCommand;
import com.team254.lib.trajectory.Path;
import com.team254.lib.trajectory.io.TextFileDeserializer;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

/**
 * Creates commands from a file path and adds them to a CommandGroup.
 * 
 * @author Alec/Andrew
 *
 */
public class CommandParser extends ACommandParser
{
    private static final double sEXPECTED_DT = .02;
    protected Snobot2017 mSnobot;
    protected SendableChooser<StartingPositions> mPositionChooser;

    /**
     * Creates a CommandParser object.
     * 
     * @param aSnobot
     *            The robot using the CommandParser.
     * @param aPositionChooser
     * @param aStartPosition
     */
    public CommandParser(Snobot2017 aSnobot, SendableChooser<StartingPositions> aPositionChooser)
    {
        super(NetworkTable.getTable(SmartDashBoardNames.sAUTON_TABLE_NAME), SmartDashBoardNames.sROBOT_COMMAND_TEXT,
                SmartDashBoardNames.sSUCCESFULLY_PARSED_AUTON, " ", "#");
        mSnobot = aSnobot;
        mPositionChooser = aPositionChooser;

    }

    /**
     * Takes a list of Strings and creates a Command.
     * 
     * @param args
     *            The command's name and parameters.
     */
    @Override
    protected Command parseCommand(List<String> args)
    {
        String commandName = args.get(0);
        Command newCommand = null;

        try
        {
            switch (commandName)
            {
            case AutonomousCommandNames.sPARALLEL_COMMAND:
            {
                newCommand = parseParallelCommand(args);
                break;
            }
            case AutonomousCommandNames.sWAIT_COMMAND:
            {
                newCommand = parseWaitCommand(args);
                break;
            }
            case AutonomousCommandNames.sSTUPID_DRIVE_STRAIGHT_COMMAND:
            {
                newCommand = parseStupidDriveStraightCommand(args);
                break;
            }
            case AutonomousCommandNames.sPLACE_GEAR_COMMAND:
            {
                newCommand = parsePlaceGearCommand(args);
                break;
            }
            case AutonomousCommandNames.sRAISE_GEAR_COMMAND:
            {
                newCommand = parseRaiseGearCommand(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_STRAIGHT_A_DISTANCE:
            {
                newCommand = parseDriveStraightADistance(args);
                break;
            }
            case AutonomousCommandNames.sTURN_WITH_DEGREES:
            {
                newCommand = parseTurnWithDegrees(args);
                break;
            }
            case AutonomousCommandNames.sTURN_TO_PEG_AFTER_PATH_FROM_STARTING_POSITION:
            {
                newCommand = parseTurnToPegAfterPathFromStartingPosition(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_PATH_STRAIGHT:
            {
                newCommand = createDrivePathCommand(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_PATH_TURN:
            {
                newCommand = createTurnPathCommand(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_TRAJECTORY:
            {
                newCommand = createTrajectoryCommand(args.get(1));
                break;
            }
            case AutonomousCommandNames.sREPLAY:
            {
                newCommand = parseReplayCommand(args);
                break;
            }
            case AutonomousCommandNames.sGO_TO_POSITION_SMOOTH_IN_STEPS:
            {
                newCommand = parseGoToPositionInStepsCommand(args);
                break;
            }
            case AutonomousCommandNames.sGO_TO_POSITION_SMOOTHLY:
            {
                newCommand = parseGoToPositionSmoothlyCommand(args);
                break;
            }
            case AutonomousCommandNames.sSCORE_GEAR_WITH_CAMERA_AND_GYRO_NO_TRAJ:
            {
                newCommand = parseScoreGearWithCameraAndGyroNoTraj(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_TO_PEG_USING_VISION:
            {
                newCommand = parseDriveToPegUsingVisionCommand(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_STRAIGHT_PATH_WITH_GYRO:
            {
                newCommand = parseDriveStraightPathWithGyro(args);
                break;
            }
            case AutonomousCommandNames.sDRIVE_STRAIGHT_PATH_WITH_GYRO_FROM_STARTING_POSITION:
            {
                newCommand = parseDriveStraightPathWithGyroFromStartingPosition(args);
                break;
            }
            case AutonomousCommandNames.sDUMP_HOPPER:
            {
                newCommand = createGetHoppersWithTrajectoryCommand(args);
                break;
            }

            // Score Gear with Trajectory
            case AutonomousCommandNames.sSCORE_GEAR:
            {
                newCommand = createScoreGearWithTrajectoryCommand(false, args);
                break;
            }
            case AutonomousCommandNames.sSCORE_GEAR_DUMP_HOPPER:
            {
                newCommand = createScoreGearDumpHopper(false, args);
                break;
            }
            case AutonomousCommandNames.sSCORE_GEAR_SCORE_FUEL:
            {
                newCommand = createScoreGearScoreFuel(false);
                break;
            }

            // Gear with Camera
            case AutonomousCommandNames.sSCORE_GEAR_WITH_CAM:
            {
                newCommand = createScoreGearWithTrajectoryCommand(true, args);
                break;
            }
            case AutonomousCommandNames.sSCORE_GEAR_WITH_CAM_DUMP_HOPPER:
            {
                newCommand = createScoreGearDumpHopper(true, args);
                break;
            }
            case AutonomousCommandNames.sSCORE_GEAR_WITH_CAM_SCORE_FUEL:
            {
                newCommand = createScoreGearScoreFuel(true);
                break;
            }

            default:
                addError("Received unexpected command name '" + commandName + "'");
            }
        }
        catch (IndexOutOfBoundsException e)
        {
            addError("You have not specified enough aguments for the command: " + commandName + ". " + e.getMessage());
        }
        catch (Exception e)
        {
            addError("Failed to parse the command: " + commandName + ". " + e.getMessage());
            e.printStackTrace();
        }
        return newCommand;
    }

    private Command parseGoToPositionSmoothlyCommand(List<String> args)
    {
        double x = Double.parseDouble(args.get(1));
        double y = Double.parseDouble(args.get(2));

        return new GoToPositionSmoothly(x, y, mSnobot.getSnobotActor());
    }

    private Command createTurnPathCommand(List<String> args)
    {
        PathConfig dudePathConfig = new PathConfig(Double.parseDouble(args.get(1)), // Endpoint
                Double.parseDouble(args.get(2)), // Max Velocity
                Double.parseDouble(args.get(3)), // Max Acceleration
                sEXPECTED_DT);

        ISetpointIterator dudeSetpointIterator = new StaticSetpointIterator(dudePathConfig);

        return new DriveTurnPath(mSnobot.getDriveTrain(), mSnobot.getPositioner(), dudeSetpointIterator);
    }

    private Command createDrivePathCommand(List<String> args)
    {
        PathConfig dudePathConfig = new PathConfig(Double.parseDouble(args.get(1)), // Endpoint
                Double.parseDouble(args.get(2)), // Max Velocity
                Double.parseDouble(args.get(3)), // Max Acceleration
                sEXPECTED_DT);

        PathGenerator dudePathGenerator = new PathGenerator();
        List<PathSetpoint> dudeList = dudePathGenerator.generate(dudePathConfig);
        ISetpointIterator dudeSetpointIterator = new StaticSetpointIterator(dudeList);

        return new DriveStraightPath(mSnobot.getDriveTrain(), mSnobot.getPositioner(), dudeSetpointIterator);
    }

    private Command createTrajectoryCommand(String aFile)
    {
        String pathFile = Properties2017.sAUTON_PATH_DIRECTORY.getValue() + "/" + aFile.trim();
        TextFileDeserializer deserializer = new TextFileDeserializer();
        Path p = deserializer.deserializeFromFile(pathFile);

        Command output = null;
        if (p == null)
        {
            addError("Could not read path file " + pathFile);
        }
        else
        {
            output = new TrajectoryPathCommand(mSnobot.getDriveTrain(), mSnobot.getPositioner(), p);
        }

        return output;
    }

    private Command createScoreGearScoreFuel(boolean aUseCamera)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();
        if (startPosition == null)
        {
            return null;
        }

        String scoreFilename = null;
        String boilFilename = null;

        switch (startPosition)
        {
        case RedRight:
            scoreFilename = "StartToGear/RedRightScoreGear.csv";
            boilFilename = "GearToBoiler/RedRightGearToBoiler.csv";
            break;
        case RedMiddle:
            scoreFilename = "StartToGear/RedMiddleScoreGear.csv";
            boilFilename = "GearToBoiler/RedMiddleGearToBoiler.csv";
            break;
        case RedLeft:
            scoreFilename = "StartToGear/RedLeftScoreGear.csv";
            break;
        case BlueRight:
            scoreFilename = "StartToGear/BlueRightScoreGear.csv";
            break;
        case BlueMiddle:
            scoreFilename = "StartToGear/BlueMiddleScoreGear.csv";
            boilFilename = "GearToBoiler/BlueMiddleGearToBoiler.csv";
            break;
        case BlueLeft:
            scoreFilename = "StartToGear/BlueLeftScoreGear.csv";
            boilFilename = "GearToBoiler/BlueLeftGearToBoiler.csv";
            break;
        default:
            break;
        }
        CommandGroup output = new CommandGroup();

        if (scoreFilename != null)
        {
            if (aUseCamera)
            {
                output.addSequential(new TrajectoryWithVisionOverride(mSnobot, scoreFilename));
            }
            else
            {
                output.addSequential(createTrajectoryCommand(scoreFilename));
            }
            output.addSequential(parsePlaceGearCommand(3));

            if (boilFilename != null)
            {
                output.addSequential(createTrajectoryCommand(boilFilename));
            }
            else
            {
                output.addSequential(parseStupidDriveStraightCommand(1.1, -.3));
            }

        }
        else
        {
            addError("Invalid scoring filename");
        }

        return output;
    }

    private Command createScoreGearWithTrajectoryCommand(boolean aUseCamera, List<String> args)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();
        if (startPosition == null)
        {
            return null;
        }

        String fileName = null;
         double smackbackTime = .25;
         double smackforwardTime = .5;
         double backwardsTime = 2;
         double backwardsSpeed = -.3;
        
         if (args.size() >= 2)
         {
         backwardsSpeed = Double.parseDouble(args.get(1));
         }
         if (args.size() >= 3)
         {
         backwardsTime = Double.parseDouble(args.get(2));
         }

        switch (startPosition)
        {
        case RedLeft:
            fileName = "StartToGear/RedLeftScoreGear.csv";
            break;
        case RedMiddle:
            fileName = "StartToGear/RedMiddleScoreGear.csv";
            break;
        case RedRight:
            fileName = "StartToGear/RedRightScoreGear.csv";
            break;
        case BlueRight:
            fileName = "StartToGear/BlueRightScoreGear.csv";
            break;
        case BlueMiddle:
            fileName = "StartToGear/BlueMiddleScoreGear.csv";
            break;
        case BlueLeft:
            fileName = "StartToGear/BlueLeftScoreGear.csv";
            break;
        default:
            break;
        }

        CommandGroup group = new CommandGroup();
        if (fileName != null)
        {
            if (aUseCamera)
            {
                group.addSequential(new WaitCommand(.25));
                group.addSequential(new TrajectoryWithVisionOverride(mSnobot, fileName));
            }
            else
            {
                group.addSequential(createTrajectoryCommand(fileName));
            }
             group.addSequential(this.parsePlaceGearCommand(.8));
             group.addSequential(this.parseStupidDriveStraightCommand(smackbackTime,
             .3));
             group.addSequential(this.parseStupidDriveStraightCommand(smackforwardTime,
             .6));
             group.addSequential(this.parseStupidDriveStraightCommand(backwardsTime,
             backwardsSpeed));
            return group;
        }
        else
        {
            addError("Invalid start selection for the current robot start position command : " + startPosition);
            return null;
        }
    }

    private Command createGetHoppersWithTrajectoryCommand(List<String> args)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();
        if (startPosition == null)
        {
            return null;
        }

        boolean doClose = true;
        if (args.size() > 1)
        {
            if (args.get(1).equals("Far"))
            {
                doClose = false;
            }
        }

        String fileName = null;

        switch (startPosition)
        {
        case RedLeft:
            if (doClose)
            {
                fileName = "StartToHopper/RedLeftToHopperFive.csv";
            }
            else
            {
                fileName = "StartToHopper/RedLeftToHopperFour.csv";
            }
            break;
        case RedMiddle:
            if (doClose)
            {
                fileName = "StartToHopper/RedMiddleToHopperOne.csv";
            }
            else
            {
                fileName = "StartToHopper/RedMiddleToHopperFive.csv";
            }
            break;
        case RedRight:
            if (doClose)
            {
                fileName = "StartToHopper/RedRightToHopperOne.csv";
            }
            else
            {
                fileName = "StartToHopper/RedRightToHopperTwo.csv";
            }
            break;
        case BlueRight:
            if (doClose)
            {
                fileName = "StartToHopper/BlueRightToHopperFour.csv";
            }
            else
            {
                fileName = "StartToHopper/BlueRightToHopperFive.csv";
            }
            break;
        case BlueMiddle:
            if (doClose)
            {
                fileName = "StartToHopper/BlueMiddleToHopperThree.csv";
            }
            else
            {
                fileName = "StartToHopper/BlueMiddleToHopperFour.csv";
            }
            break;
        case BlueLeft:
            if (doClose)
            {
                fileName = "StartToHopper/BlueLeftToHopperThree.csv";
            }
            else
            {
                fileName = "StartToHopper/BlueLeftToHopperTwo.csv";
            }
            break;
        default:
            break;
        }

        if (fileName != null)
        {
            CommandGroup output = new CommandGroup();
            output.addSequential(createTrajectoryCommand(fileName));
            output.addSequential(parsePlaceGearCommand(3));
            return output;
        }
        else
        {
            addError("Invalid start selection for the current robot start position command : " + startPosition);
            return null;
        }
    }

    private Command createScoreGearDumpHopper(boolean aUseCamera, List<String> args)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();
        if (startPosition == null)
        {
            return null;
        }

        boolean doClose = true;
        if (args.size() > 1)
        {
            if (args.get(1).equals("Far"))
            {
                doClose = false;
            }
        }

        String scoreFilename = null;
        String hopperFilename = null;

        switch (startPosition)
        {
        case RedLeft:
            scoreFilename = "StartToGear/RedLeftScoreGear.csv";

            if (doClose)
            {
                hopperFilename = "GearToHopper/RedLeftScoreGearGetHopperFive.csv";
            }
            else
            {
                hopperFilename = "GearToHopper/RedLeftScoreGearGetHopperFour.csv";
            }

            break;
        case RedRight:
            scoreFilename = "StartToGear/RedRightScoreGear.csv";

            if (doClose)
            {
                hopperFilename = "GearToHopper/RedRightScoreGearGetHopperOne.csv";
            }
            else
            {
                hopperFilename = "GearToHopper/RedRightScoreGearGetHopperTwo.csv";
            }
            break;
        case BlueRight:
            scoreFilename = "StartToGear/BlueRightScoreGear.csv";

            if (doClose)
            {
                hopperFilename = "GearToHopper/BlueRightScoreGearGetHopperFour.csv";
            }
            else
            {
                hopperFilename = "GearToHopper/BlueRightScoreGearGetHopperFive.csv";
            }
            break;
        case BlueLeft:
            scoreFilename = "StartToGear/BlueLeftScoreGear.csv";

            if (doClose)
            {
                hopperFilename = "GearToHopper/BlueLeftScoreGearGetHopperThree.csv";
            }
            else
            {
                hopperFilename = "GearToHopper/BlueLeftScoreGearGetHoppertwo.csv";
            }
            break;

        case RedMiddle:
            scoreFilename = "StartToGear/RedMiddleScoreGear.csv";
            break;
        case BlueMiddle:
            scoreFilename = "StartToGear/BlueMiddleScoreGear.csv";
            break;

        // Intentional fall through, nothing to do
        case Origin:
        default:
            break;
        }

        CommandGroup output = new CommandGroup();

        if (scoreFilename != null)
        {
            if (aUseCamera)
            {
                output.addSequential(new TrajectoryWithVisionOverride(mSnobot, scoreFilename));
            }
            else
            {
                output.addSequential(createTrajectoryCommand(scoreFilename));
            }
            output.addSequential(parsePlaceGearCommand(3));

            if (hopperFilename != null)
            {
                output.addSequential(createTrajectoryCommand(hopperFilename));
            }
            else
            {
                output.addSequential(parseStupidDriveStraightCommand(1.1, -.3));
            }

        }
        else
        {
            addError("Invalid scoring filename");
        }

        return output;
    }

    /**
     * Parses the Score Gear with Camera and Gyroscope with Path command.
     * 
     * @param args
     * @return A command group with all the commands for this action
     */
    private Command parseScoreGearWithCameraAndGyroNoTraj(List<String> args)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();

        // Overall command group
        CommandGroup group = new CommandGroup();

        // Command group moves gear box up and drives path at the same time.
        CommandGroup groupUpAndMove = new CommandGroup();
        groupUpAndMove.addParallel(new RaiseGear(mSnobot.getGearBoss(), 1.5));
        double lMaxVelocity = Double.parseDouble(args.get(2));
        double lMaxAcceleration = Double.parseDouble(args.get(3));
        double lDriveRedLeft = Double.parseDouble(args.get(4));
        double lDriveRedRight = Double.parseDouble(args.get(5));
        double lDriveRedMiddle = Double.parseDouble(args.get(6));
        double lDriveBlueLeft = Double.parseDouble(args.get(7));
        double lDriveBlueRight = Double.parseDouble(args.get(8));
        double lDriveBlueMiddle = Double.parseDouble(args.get(9));
        groupUpAndMove.addParallel(new DriveStraightPathWithGyroFromStartingPosition(mSnobot.getDriveTrain(), mSnobot.getPositioner(), startPosition,
                lMaxVelocity, lMaxAcceleration, sEXPECTED_DT, lDriveRedLeft, lDriveRedRight, lDriveRedMiddle, lDriveBlueLeft, lDriveBlueRight,
                lDriveBlueMiddle));
        group.addSequential(groupUpAndMove);

        // Turn to the peg. This command requires that
        // DriveStraightPathWithGyroFromStartingPosition is used first.
        double lTurnRedLeft = Double.parseDouble(args.get(10));
        double lTurnRedRight = Double.parseDouble(args.get(11));
        double lTurnRedMiddle = Double.parseDouble(args.get(12));
        double lTurnBlueLeft = Double.parseDouble(args.get(13));
        double lTurnBlueRight = Double.parseDouble(args.get(14));
        double lTurnBlueMiddle = Double.parseDouble(args.get(15));
        group.addSequential(new TurnToPegAfterPathFromStartingPosition(Properties2017.sSIDE_AUTO_TURN_SPEED.getValue(), startPosition,
                mSnobot.getPositioner(), mSnobot.getSnobotActor(), lTurnRedLeft, lTurnRedRight, lTurnRedMiddle, lTurnBlueLeft, lTurnBlueRight, lTurnBlueMiddle));

        // A wait just for testing.
        // group.addSequential(new WaitCommand(0.5));

        // Now drive to the peg using the camera only.
        group.addSequential(new DriveToPegUsingVision(mSnobot.getVisionManager(), mSnobot.getSnobotActor(), 6, 16));

        // We want to give a little extra umph so drive straight for just a bit
        // more.
        group.addSequential(new StupidDriveStraight(mSnobot.getDriveTrain(), .25, 0.5));

        // Put the gear box down. Wait 2 seconds total.
        group.addSequential(new LowerGear(mSnobot.getGearBoss(), 2.0));

        // Now back up to let the gear go.
        group.addSequential(new StupidDriveStraight(mSnobot.getDriveTrain(), 1, -0.5));

        return group;
    }

    private Command parseDriveStraightPathWithGyro(List<String> args)
    {
        PathConfig dudePathConfig = new PathConfig(Double.parseDouble(args.get(1)), // Endpoint
                Double.parseDouble(args.get(2)), // Max Velocity
                Double.parseDouble(args.get(3)), // Max Acceleration
                sEXPECTED_DT);
        ISetpointIterator dudeSetpointIterator = new StaticSetpointIterator(dudePathConfig);
        return new DriveStraightPathWithGyro(mSnobot.getDriveTrain(), mSnobot.getPositioner(), dudeSetpointIterator);
    }

    private Command parseDriveStraightPathWithGyroFromStartingPosition(List<String> args)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();
        double lMaxVelocity = Double.parseDouble(args.get(1));
        double lMaxAcceleration = Double.parseDouble(args.get(2));
        double lRedLeft = Double.parseDouble(args.get(3));
        double lRedRight = Double.parseDouble(args.get(4));
        double lRedMiddle = Double.parseDouble(args.get(5));
        double lBlueLeft = Double.parseDouble(args.get(6));
        double lBlueRight = Double.parseDouble(args.get(7));
        double lBlueMiddle = Double.parseDouble(args.get(8));
        return new DriveStraightPathWithGyroFromStartingPosition(mSnobot.getDriveTrain(), mSnobot.getPositioner(), startPosition, lMaxVelocity,
                lMaxAcceleration, sEXPECTED_DT, lRedLeft, lRedRight, lRedMiddle, lBlueLeft, lBlueRight, lBlueMiddle);
    }

    private Command parseDriveToPegUsingVisionCommand(List<String> args)
    {
        double timeout = 4;
        double deadband = 6;
        
        if(args.size() >= 2)
        {
            timeout = Double.parseDouble(args.get(1));
        }
        
        if(args.size() >= 3)
        {
            deadband = Double.parseDouble(args.get(2));
        }
        
        return new DriveToPegUsingVision(mSnobot.getVisionManager(), mSnobot.getSnobotActor(), deadband, timeout);
    }

    private Command parseTurnWithDegrees(List<String> args)
    {
        double speed = Double.parseDouble(args.get(1));
        double angle = Double.parseDouble(args.get(2));
        return new TurnWithDegrees(speed, angle, mSnobot.getSnobotActor());
    }

    private Command parseTurnToPegAfterPathFromStartingPosition(List<String> args)
    {
        StartingPositions startPosition = mPositionChooser.getSelected();
        double lTurnRedLeft = Double.parseDouble(args.get(1));
        double lTurnRedRight = Double.parseDouble(args.get(2));
        double lTurnRedMiddle = Double.parseDouble(args.get(3));
        double lTurnBlueLeft = Double.parseDouble(args.get(4));
        double lTurnBlueRight = Double.parseDouble(args.get(5));
        double lTurnBlueMiddle = Double.parseDouble(args.get(6));

        return new TurnToPegAfterPathFromStartingPosition(Properties2017.sSIDE_AUTO_TURN_SPEED.getValue(), startPosition, 
                mSnobot.getPositioner(),
                mSnobot.getSnobotActor(),
                lTurnRedLeft, lTurnRedRight, lTurnRedMiddle, lTurnBlueLeft, lTurnBlueRight, lTurnBlueMiddle);
    }

    private Command parseDriveStraightADistance(List<String> args)
    {
        double distance = Double.parseDouble(args.get(1));
        double speed = Double.parseDouble(args.get(2));
        return new DriveStraightADistance(distance, speed, mSnobot.getSnobotActor());
    }

    private Command parsePlaceGearCommand(List<String> args)
    {
        double time = Double.parseDouble(args.get(1));
        return parsePlaceGearCommand(time);
    }

    private Command parsePlaceGearCommand(double aTime)
    {
        return new LowerGear(mSnobot.getGearBoss(), aTime);
    }

    private Command parseRaiseGearCommand(List<String> args)
    {
        double time = Double.parseDouble(args.get(1));
        return new RaiseGear(mSnobot.getGearBoss(), time);
    }

    private Command parseStupidDriveStraightCommand(List<String> args)
    {
        double time = Double.parseDouble(args.get(1));
        double speed = Double.parseDouble(args.get(2));
        return parseStupidDriveStraightCommand(time, speed);
    }

    private Command parseStupidDriveStraightCommand(double aTime, double aSpeed)
    {
        return new StupidDriveStraight(mSnobot.getDriveTrain(), aTime, aSpeed);
    }

    private Command parseReplayCommand(List<String> args) throws IOException
    {
        String autoPath = Properties2017.sREPLAY_PATH.getValue() + args.get(1);
        return new Replay(mSnobot.getDriveTrain(), autoPath);
    }

    private Command parseGoToPositionInStepsCommand(List<String> args) throws IOException
    {
        double x = Double.parseDouble(args.get(1));
        double y = Double.parseDouble(args.get(2));
        double speed = .5;
        if (args.size() > 3)
        {
            speed = Double.parseDouble(args.get(3));
        }

        return new GoToPositionInSteps(x, y, speed, mSnobot.getSnobotActor());
    }

    protected Command parseWaitCommand(List<String> args)
    {
        double time = Double.parseDouble(args.get(1));
        return new WaitCommand(time);
    }

    @Override
    public CommandGroup readFile(String aFilePath)
    {
        if (aFilePath == null)
        {
            aFilePath = "NOT FOUND!";
        }

        mAutonTable.putString(SmartDashBoardNames.sAUTON_FILENAME, aFilePath);
        return super.readFile(aFilePath);
    }

    public void saveAutonMode()
    {
        String new_text = mAutonTable.getString(SmartDashBoardNames.sROBOT_COMMAND_TEXT, "");
        String filename = mAutonTable.getString(SmartDashBoardNames.sAUTON_FILENAME, "auton_file.txt");

        System.out.println("*****************************************");
        System.out.println("Saving auton mode to " + filename);
        System.out.println("*****************************************");

        try
        {
            BufferedWriter bw = new BufferedWriter(new FileWriter(new File(filename)));
            bw.write(new_text);
            bw.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        mAutonTable.putBoolean(SmartDashBoardNames.sSAVE_AUTON, false);
    }
}
