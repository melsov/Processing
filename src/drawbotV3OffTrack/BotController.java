package drawbotV3OffTrack;

import drawbotV3OffTrack.MotorInstructions;

public interface BotController {
	MotorInstructions nextMotorInstructions();
	MotorInstructions goHomeInstructions();
	//TODO: setMachineSpecs(MachineSpecs mspecs);

}
