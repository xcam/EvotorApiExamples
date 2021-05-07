package ru.xcam.receipt_logger

import android.content.Context
import android.util.Log
import ru.evotor.framework.receipt.event.ReceiptCompletedEvent
import ru.evotor.framework.receipt.event.handler.receiver.SellReceiptBroadcastReceiver


class CustomSellReceiptBroadcastReceiver : SellReceiptBroadcastReceiver() {
    override fun handleReceiptCompletedEvent(context: Context, event: ReceiptCompletedEvent) {
        super.handleReceiptCompletedEvent(context, event)

        Log.e("SOME_TAG", "Received event: " + event.receiptUuid)
        SellReceiptProcessIntentService.start(context)
    }
}