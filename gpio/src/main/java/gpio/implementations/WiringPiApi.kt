package gpio.implementations

import com.pi4j.internal.ProviderAliases
import com.pi4j.io.gpio.digital.DigitalOutput
import com.pi4j.io.gpio.digital.DigitalOutputProvider
import gpio.MinimalGPIOApi

class WiringPiApi : MinimalGPIOApi {
    private lateinit var pi4j: ProviderAliases

    fun init() {
       // val port: DigitalOutput = pi4j.digitalOutput().create(11)
        val led = pi4j.digitalOutput<DigitalOutputProvider?>().create<DigitalOutput?>(22)
    }

    override fun initializeOutputPortMode(port: Int) {
//        Gpio.pinMode(port, Gpio.OUTPUT)

    }

    override fun initializeInputPortMode(port: Int) {
//        Gpio.pinMode(port, Gpio.INPUT)
    }

    override fun readOutputPort(port: Int): Boolean {
//        val digitalRead = Gpio.digitalRead(port)
//        println("digitalRead[$port] = $digitalRead")
//        return digitalRead == Gpio.LOW
        return false;
    }

    override fun writeOutputPort(port: Int, state: Boolean) {
        //Gpio.digitalWrite(port, if (state) Gpio.LOW else Gpio.HIGH)
//        Gpio.digitalWrite(port, state)
    }

    override fun readInputPort(port: Int): Int {
//        return Gpio.digitalRead(port)
        return 1;
    }

    override fun terminate() {
        TODO("Not yet implemented")
    }
}