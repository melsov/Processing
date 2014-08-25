package drawbotV3;

import drawbotV3.MotorInstructions;

public interface BotController {
	MotorInstructions nextMotorInstructions();
	MotorInstructions goHomeInstructions();
	//TODO: setMachineSpecs(MachineSpecs mspecs);

}
