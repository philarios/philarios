package io.philarios.structurizr

import io.philarios.core.Translator
import com.structurizr.Workspace as SWorkspace

class OutputTranslator : Translator<Workspace, SWorkspace> {
    override fun translate(context: Workspace) =
            SWorkspace(context.name, context.description)
}