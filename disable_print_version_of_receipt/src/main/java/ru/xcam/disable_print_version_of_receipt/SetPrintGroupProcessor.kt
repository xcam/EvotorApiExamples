package ru.xcam.disable_print_version_of_receipt

import android.content.Context
import ru.evotor.framework.core.action.event.receipt.changes.position.SetPrintGroup
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEvent
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventProcessor
import ru.evotor.framework.core.action.event.receipt.print_group.PrintGroupRequiredEventResult
import ru.evotor.framework.receipt.PrintGroup
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi
import java.nio.charset.Charset
import java.util.*

class SetPrintGroupProcessor(
    private val context: Context
) : PrintGroupRequiredEventProcessor() {

    override fun call(action: String, event: PrintGroupRequiredEvent, callback: Callback) {
        moveAllPositionsToNonFiscal(action, callback)
    }

    private fun moveAllPositionsToNonFiscal(
        action: String,
        callback: Callback
    ) {
        val result = moveAllPositionsToNonFiscal(action)

        if (result == null) {
            callback.skip()
        } else {
            callback.onResult(result)
        }
    }

    private fun moveAllPositionsToNonFiscal(action: String): PrintGroupRequiredEventResult? {
        try {
            val receipt = ReceiptApi.getReceipt(
                context,
                if (action == PrintGroupRequiredEvent.NAME_PAYBACK_RECEIPT) {
                    Receipt.Type.PAYBACK
                } else {
                    Receipt.Type.SELL
                }
            ) ?: return null

            return PrintGroupRequiredEventResult(
                null,
                listOf(
                    SetPrintGroup(
                        printGroup,
                        listOf(),
                        receipt.getPositions().map { it.uuid }
                    )
                )
            )
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

    private val printGroup = PrintGroup(
        UUID.nameUUIDFromBytes("Some random text 2".toByteArray(Charset.forName("UTF-8")))
            .toString(),
        PrintGroup.Type.CASH_RECEIPT,
        null,
        null,
        null,
        null,
        false, // не печатать чек
        null,
        null
    )
}