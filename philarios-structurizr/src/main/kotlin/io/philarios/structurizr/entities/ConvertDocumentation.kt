package io.philarios.structurizr.entities

import com.structurizr.model.SoftwareSystem
import io.philarios.structurizr.Decision
import io.philarios.structurizr.DecisionStatus
import io.philarios.structurizr.Documentation
import io.philarios.structurizr.Format
import java.text.SimpleDateFormat
import com.structurizr.documentation.DecisionStatus as SDecisionStatus
import com.structurizr.documentation.Documentation as SDocumentation
import com.structurizr.documentation.Format as SFormat

fun Documentation.convert(documentation: SDocumentation, elementMap: ElementMap) = documentation.also {
    decisions.forEach { decision -> decision.convert(documentation, elementMap) }
}

fun Decision.convert(documentation: SDocumentation, elementMap: ElementMap) = documentation.addDecision(
        elementMap[elementId] as? SoftwareSystem,
        id,
        date.convertToDate(),
        title,
        status.convert(),
        format.convert(),
        content
)

private fun String.convertToDate() = SimpleDateFormat("yyyy-MM-dd").parse(this)

private fun DecisionStatus.convert() = when (this) {
    DecisionStatus.Proposed -> SDecisionStatus.Proposed
    DecisionStatus.Accepted -> SDecisionStatus.Accepted
    DecisionStatus.Superseded -> SDecisionStatus.Superseded
    DecisionStatus.Deprecated -> SDecisionStatus.Deprecated
    DecisionStatus.Rejected -> SDecisionStatus.Rejected
}

private fun Format.convert() = when (this) {
    Format.Markdown -> SFormat.Markdown
    Format.AsciiDoc -> SFormat.AsciiDoc
}