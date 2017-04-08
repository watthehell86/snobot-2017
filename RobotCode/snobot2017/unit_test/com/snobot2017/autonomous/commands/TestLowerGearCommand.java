package com.snobot2017.autonomous.commands;

import org.junit.Assert;
import org.junit.Test;

import com.snobot.lib.logging.ILogger;
import com.snobot.simulator.wrapper_accessors.DataAccessorFactory;
import com.snobot2017.ModuleFactory;
import com.snobot2017.gearboss.IGearBoss;
import com.snobot2017.joystick.IOperatorJoystick;
import com.snobot2017.test.mocks.MockLogger;
import com.snobot2017.test.mocks.MockOperatorJoystick;
import com.snobot2017.test.utilities.BaseAutonModeTest;

public class TestLowerGearCommand extends BaseAutonModeTest
{
    @Test
    public void simpleTest()
    {
        DataAccessorFactory.getInstance().getSimulatorDataAccessor().setDisabled(false);

        IOperatorJoystick opJoy = new MockOperatorJoystick();
        ILogger logger = new MockLogger();
        IGearBoss gearBoss = new ModuleFactory().createGearBoss(opJoy, logger);
        gearBoss.moveGearHigh();
        gearBoss.update();
        Assert.assertTrue(gearBoss.isGearUp());

        double time = .5;
        LowerGear autonCommand = new LowerGear(gearBoss, time);
        autonCommand.start();

        runCommand(gearBoss, time * 1.1);
        Assert.assertFalse(autonCommand.isRunning());
        Assert.assertFalse(gearBoss.isGearUp());
    }
}
