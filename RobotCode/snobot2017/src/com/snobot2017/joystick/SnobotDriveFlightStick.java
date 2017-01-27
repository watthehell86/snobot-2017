package com.snobot2017.joystick;

import edu.wpi.first.wpilibj.Joystick;

public class SnobotDriveFlightStick implements IDriverJoystick
{

    private Joystick mLeft;
    private Joystick mRight;
    private double mRightSpeed;
    private double mLeftSpeed;

    public SnobotDriveFlightStick(Joystick aLeft, Joystick aRight)
    {
        mLeft = aLeft;
        mRight = aRight;
    }

    @Override
    public void init()
    {

    }

    @Override
    public void update()
    {
        mLeftSpeed = mLeft.getY();
        mRightSpeed = mRight.getY();
    }

    @Override
    public void control()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void rereadPreferences()
    {

    }

    @Override
    public void updateSmartDashboard()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateLog()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public void stop()
    {
        // TODO Auto-generated method stub

    }

    @Override
    public double getRightSpeed()
    {

        return mRightSpeed;
    }

    @Override
    public double getLeftSpeed()
    {

        return mLeftSpeed;
    }

    @Override
    public double getArcadePower()
    {
        return 0;
    }

    @Override
    public double getArcadeTurn()
    {
        return 0;
    }

    @Override
    public boolean isArcadeMode()
    {
        return false;
    }

}
