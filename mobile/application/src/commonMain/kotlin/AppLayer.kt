import com.iwbi.utils.koin.BaseKoinComponent
import com.mobile.iwbi.application.authentication.di.authenticationModule
import com.mobile.iwbi.application.di.inputPortsModule
import com.mobile.iwbi.application.di.outputPortsModule
import org.koin.core.component.inject
import org.koin.dsl.koinApplication
import org.koin.dsl.module

interface AppLayer {
    val inputPorts: InputPorts
}

fun createApplicationLayer(
    outputPorts: OutputPorts
) : AppLayer = object : AppLayer, BaseKoinComponent() {
    override val application = koinApplication {
        modules(module {
            single { outputPorts }
        })

        modules(inputPortsModule)
        modules(outputPortsModule)

        modules(authenticationModule)
    }
    override val inputPorts by inject<InputPorts>()
}