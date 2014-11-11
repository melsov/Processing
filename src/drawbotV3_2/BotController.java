package drawbotV3_2;

import drawbotV3_2.DrawPointProvider.NudgeMode;
import drawbotV3_2.MotorInstructions;

public interface BotController {
	MotorInstructions nextMotorInstructions();
	MotorInstructions goHomeInstructions();
	//TODO: setMachineSpecs(MachineSpecs mspecs);
	void setNudgeMode(NudgeMode nudgeMode);

}
