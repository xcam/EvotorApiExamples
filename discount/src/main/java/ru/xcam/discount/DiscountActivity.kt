package ru.xcam.discount

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_discount.*
import ru.evotor.framework.core.IntegrationActivity
import ru.evotor.framework.core.action.event.receipt.discount.ReceiptDiscountEventResult
import java.math.BigDecimal


class DiscountActivity : IntegrationActivity() {
    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_discount)
        btnApply.setOnClickListener {
            val amount = BigDecimal(etAmount.text.toString())
            setIntegrationResult(
                ReceiptDiscountEventResult(
                    amount,
                    null,
                    emptyList()
                )
            )

            finish()
        }
    }
}