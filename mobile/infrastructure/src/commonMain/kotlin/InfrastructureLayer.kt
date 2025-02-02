import com.iwbi.utils.koin.BaseKoinComponent
import di.infrastructureModule
import org.koin.core.component.inject
import org.koin.dsl.koinApplication

interface InfraLayer {
    val outputPorts: OutputPorts
}

fun createInfrastructureLayer() : InfraLayer = object : InfraLayer, BaseKoinComponent() {
    override val application = koinApplication {
        modules(infrastructureModule)
    }
    override val outputPorts by inject<OutputPorts>()
}

