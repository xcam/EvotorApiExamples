package ru.xcam.disable_print_version_of_receipt

import ru.evotor.framework.core.IntegrationService
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent
import ru.evotor.framework.core.action.processor.ActionProcessor

class DisablePrintingService : IntegrationService() {
    override fun createProcessors(): MutableMap<String, ActionProcessor> {
        val resultMap = mutableMapOf<String, ActionProcessor>()
        val setPrintGroupProcessor = SetPrintGroupProcessor(this)
        resultMap[PrintGroupRequiredEvent.NAME_SELL_RECEIPT] = setPrintGroupProcessor
        resultMap[PrintGroupRequiredEvent.NAME_PAYBACK_RECEIPT] = setPrintGroupProcessor
        return resultMap
    }
}