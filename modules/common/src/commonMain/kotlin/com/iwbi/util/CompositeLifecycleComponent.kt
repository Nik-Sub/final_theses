package com.iwbi.util

class CompositeLifecycleComponent(
    vararg components: LifecycleComponent,
) : LifecycleComponent {
    @Suppress("SpreadOperator")
    private val components = mutableSetOf(*components)

    fun add(component: LifecycleComponent) {
        components.add(component)
    }

    fun addAll(vararg components: LifecycleComponent) {
        components.forEach { add(it) }
    }

    override fun initialize() {
        components.forEach {
            it.initialize()
        }
    }

    override fun release() {
        components.reversed().forEach {
            it.release()
        }
    }
}
