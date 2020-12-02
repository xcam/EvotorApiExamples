package ru.xcam.discount

import android.content.Intent
import ru.evotor.framework.receipt.formation.event.DiscountScreenAdditionalItemsEvent
import ru.evotor.framework.receipt.formation.event.handler.service.SellIntegrationService


class DiscountService : SellIntegrationService() {
    override fun handleEvent(event: DiscountScreenAdditionalItemsEvent): Nothing? {
        startIntegrationActivity(Intent(this, DiscountActivity::class.java))
        return null
    }
}