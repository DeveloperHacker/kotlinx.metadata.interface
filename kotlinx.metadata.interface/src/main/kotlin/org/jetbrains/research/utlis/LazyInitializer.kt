package org.jetbrains.research.utlis

import kotlin.reflect.KProperty


class LazyInitializer(private val init: () -> Unit) {
    var hasInitialized = false
        private set

    var hasInitialization = false
        private set

    fun forceInit() {
        if (this@LazyInitializer.hasInitialization)
            error("Initialization recursive problem")
        this@LazyInitializer.hasInitialization = true
        this@LazyInitializer.init()
        this@LazyInitializer.hasInitialization = false
    }

    inner class Property<C, T> {
        var hasInitialized = false
            private set

        private lateinit var value: KtWrapper<T>

        operator fun getValue(instance: C, property: KProperty<*>): T {
            if (this@Property.hasInitialized) return this@Property.value.value
            if (!this@LazyInitializer.hasInitialized) forceInit()
            if (!this@Property.hasInitialized)
                error("Property ${property.name} hasn't initialized during $instance initialization")
            this@LazyInitializer.hasInitialized = true
            return this@Property.value.value
        }

        operator fun setValue(instance: C, property: KProperty<*>, value: T) {
            this@Property.hasInitialized = true
            this@Property.value = KtWrapper(value)
        }
    }
}