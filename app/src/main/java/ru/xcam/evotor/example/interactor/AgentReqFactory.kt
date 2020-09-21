package ru.xcam.evotor.example.interactor

import ru.evotor.framework.receipt.position.AgentRequisites


object AgentReqFactory {
    fun create(principalInn: String, principalName: String, phones: List<String>) =
        AgentRequisites.createForAgent(principalInn, phones).let {
            it.copy(
                principal = it.principal.copy(shortName = principalName)
            )
        }
}