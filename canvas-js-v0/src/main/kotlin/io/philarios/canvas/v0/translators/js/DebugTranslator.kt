package io.philarios.canvas.v0.translators.js

import io.philarios.core.v0.Translator

class DebugTranslator<C> : Translator<C, C> {
    override fun translate(context: C): C {
        console.log(context)
        return context
    }
}