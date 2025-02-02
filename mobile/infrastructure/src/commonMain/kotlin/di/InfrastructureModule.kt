package di

import OutputPorts
import com.mobile.iwbi.application.authentication.output.AuthenticationProviderPort
import com.mobile.iwbi.infrastructure.authentication.AuthenticationProvider
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val infrastructureModule = module {
    singleOf(::OutputPortsImpl) bind OutputPorts::class

    singleOf(::AuthenticationProvider) bind AuthenticationProviderPort::class
}

internal class OutputPortsImpl(
    override val authenticationProviderPort: AuthenticationProviderPort
) : OutputPorts