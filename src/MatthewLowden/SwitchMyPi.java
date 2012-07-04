//   Copyright 2012 Matthew Lowden
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package MatthewLowden;

import be.doubleyouit.raspberry.gpio.Boardpin;
import be.doubleyouit.raspberry.gpio.Direction;
import be.doubleyouit.raspberry.gpio.GpioGateway;
import be.doubleyouit.raspberry.gpio.impl.GpioGatewayImpl;

public class SwitchMyPi {
	
	// This GPIO Sample code uses PIN3 and PIN6 on the Raspberry Pi GPIO header.
	// PIN3 is GPIO 0, which is pulled up.
	// PIN6 is GND (Ground).
	// Connecting PIN3 and PIN6 results in PIN3 changing from high (true) to low (false). 
	
	// The schematic below identifies the pins used.
	//
	//         Raspberry Pi Top             __ Composite Video (yellow)
	//       ______________________________|  |__
	//      |                              |  |
	//      |  - - 6 - - - - - - - - - - 
	//      |  - 3 - - - - - - - - - - -
	//      |
	//  ____|
	// |    |
	// | SD |
	// |    |
	
	// More information on Raspberry Pi GPIO can be found here:
	// http://elinux.org/RPi_Low-level_peripherals
	
	private final static Boardpin usedGPIOpin = Boardpin.PIN3_GPIO0;

	private GpioGateway gpio;

	public class Shutdown implements Runnable {
		
		// This Runnable is called on shutdown to ensure the GPIO pin is released.
		
		@Override
		public void run() {
			shutDownGPIO();
			
			// Output a CR so that the command prompt is in the regular place on exit.
			System.out.println();
		}
	}

	public SwitchMyPi() {
		
		gpio = null;
		
		// Register Shutdown hook.
		Runtime.getRuntime().addShutdownHook(new Thread(new Shutdown()));
	}

	private void setUpGPIO() {
		
		// Set up GPIO
		gpio = new GpioGatewayImpl();
		gpio.export(usedGPIOpin);
		gpio.setDirection(usedGPIOpin, Direction.IN);
	}

	private void monitorGPIO() throws InterruptedException {
		
		// Simply loop for ever, checking the state every 50ms.

		while (true) {
			Boolean state = gpio.getValue(usedGPIOpin);
			if (true == state) {
				System.out.print("\r----- Switch opened -----");
			} else {
				System.out.print("\r+++++ Switch closed +++++");
			}
			Thread.sleep(50);
		}

	}

	private void shutDownGPIO() {
		if (null != gpio) {
			gpio.unexport(Boardpin.PIN3_GPIO0);
		}
	}

	public static void main(String[] args) {
		SwitchMyPi switchMyPi = null;
		try {
			switchMyPi = new SwitchMyPi();
			switchMyPi.setUpGPIO();
			switchMyPi.monitorGPIO();
		} catch (InterruptedException e) {
		} finally {
			if (null != switchMyPi) {
				switchMyPi.shutDownGPIO();
			}
		}
	}
}
