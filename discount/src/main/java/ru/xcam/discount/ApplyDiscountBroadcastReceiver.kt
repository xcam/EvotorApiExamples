package ru.xcam.discount

import android.content.Context
import ru.evotor.framework.component.PaymentPerformerApi
import ru.evotor.framework.payment.PaymentType
import ru.evotor.framework.receipt.event.ApplyDiscountToReceiptEvent
import ru.evotor.framework.receipt.event.handler.receiver.SellReceiptBroadcastReceiver
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException
import ru.evotor.framework.receipt.formation.api.SellApi


class ApplyDiscountBroadcastReceiver : SellReceiptBroadcastReceiver() {
    override fun handleApplyDiscountToReceiptEvent(
        context: Context,
        eventApplyDiscountTo: ApplyDiscountToReceiptEvent
    ) {
        super.handleApplyDiscountToReceiptEvent(context, eventApplyDiscountTo)
        val paymentPerformers = PaymentPerformerApi.getAllPaymentPerformers(context.packageManager)

        for (paymentPerformer in paymentPerformers) {
            if (paymentPerformer.paymentSystem?.paymentType == PaymentType.ELECTRON) {
                SellApi.moveCurrentReceiptDraftToPaymentStage(
                    context,
                    paymentPerformer,
                    object : ReceiptFormationCallback {
                        override fun onError(error: ReceiptFormationException) {
                        }

                        override fun onSuccess() {
                        }

                    }
                )

                return
            }
        }

    }
}