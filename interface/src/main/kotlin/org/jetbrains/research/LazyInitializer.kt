package org.jetbrains.research

import kotlin.reflect.KProperty


class LazyInitializer(private val init: () -> Unit) {
    var hasInitialized = false
        private set

    inner class Property<C, T> {
        var hasInitialized = false
            private set

        private lateinit var value: KtWrapper<T>

        operator fun getValue(instance: C, property: KProperty<*>): T {
            if (!this@LazyInitializer.hasInitialized) init()
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