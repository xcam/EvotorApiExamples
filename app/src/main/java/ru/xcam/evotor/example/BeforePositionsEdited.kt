package ru.xcam.evotor.example

import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEvent
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventProcessor
import ru.evotor.framework.core.action.event.receipt.before_positions_edited.BeforePositionsEditedEventResult
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionEdit
import ru.evotor.framework.core.action.processor.ActionProcessor
import ru.evotor.framework.receipt.Position
import ru.evotor.framework.receipt.position.AgentRequisites

class BeforePositionsEdited : IntegrationService() {
    private var requisitesForFirstIsSet = false

    override fun createProcessors(): MutableMap<String, ActionProcessor> {
        val eventProcessor = createProcessor()
        val map = mutableMapOf<String, ActionProcessor>();
        map[BeforePositionsEditedEvent.NAME_SELL_RECEIPT] = eventProcessor
        return map
    }

    private fun createProcessor(): BeforePositionsEditedEventProcessor {
        return object : BeforePositionsEditedEventProcessor() {
            override fun call(
                action: String,
                event: BeforePositionsEditedEvent,
                callback: ActionProcessor.Callback
            ) {
                val changes = event.changes

                event.changes
                    .filterIsInstance<PositionAdd>()
                    .forEach {
                        val agentRequisites: AgentRequisites

                        //Первой добавленной добавляем одни агентские реквизиты. Второй - другие.
                        if (!requisitesForFirstIsSet) {
//                            agentRequisites =
//                                AgentRequisites.createForAgent("1122334563", listOf("89233137352"))

                            android.util.Log.d(packageName, "!! agent req is added!")
                            requisitesForFirstIsSet = true
                        } else {
                            //Чек будет анулирован с ошибкой "Печать кассового чека. Неопознанная ошибка", если агентские реквизиты у позиций имеют совпадающие номера
//                            agentRequisites = AgentRequisites.createForAgent(
//                                "2648413587",
//                                listOf("89233137352", "89233137354")
//                            )

                            //Более того, ошибка происходит, даже если это разные типы реквизитов, с разными инн.
//                            agentRequisites = AgentRequisites.createForCommissioner("0909140140", listOf("89233137352", "89233137354"))

                            //Или один и того же тип реквизитов с разными инн, но совпадающими телефонами
//                            agentRequisites = AgentRequisites.createForAgent("0909140140", listOf("89233137352", "89233137354"))

                            //Если же совпадающих номеров нет (неважно, совпадают ли инн) - чек распечатается без ошибок
                            agentRequisites = AgentRequisites.createForCommissioner("2648413587", listOf("89233137353", "89233137354"))

                            android.util.Log.d(packageName, "!! second agent req is added!")
                        }

//                        changes.add(
//                            PositionEdit(
//                                Position.Builder.copyFrom(it.position)
//                                    .setAgentRequisites(agentRequisites)
//                                    .build()
//                            )
//                        )
                    }

                callback.onResult(BeforePositionsEditedEventResult(changes, null))
            }
        }
    }
}
