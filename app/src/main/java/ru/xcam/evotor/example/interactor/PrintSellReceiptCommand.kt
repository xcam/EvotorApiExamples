package ru.evotor.framework.core.action.command

import ru.evotor.framework.calculator.MoneyCalculator
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand
import ru.evotor.framework.min
import ru.evotor.framework.payment.PaymentType
import ru.evotor.framework.receipt.Payment
import ru.evotor.framework.receipt.Position
import ru.evotor.framework.receipt.PrintGroup
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.sumByBigDecimal
import java.math.BigDecimal
import java.util.*


/**
 * Команда печати чека продажи.
 * @param printReceipts Список чеков для печати.
 * @param extra Дополнительные данные к чеку.
 * @param clientPhone Телефон клиента.
 * @param clientEmail Электронная почта клиента.
 * @param receiptDiscount Скидка на чек.
 * @param paymentAddress Адрес места расчёта
 * @param paymentPlace Место расчёта
 * @param userUuid Идентификатор сотрудника в формате `uuid4`, от лица которого будет произведена операция. Если передано null, то будет выбран текущий авторизованный сотрудник. @see ru.evotor.framework.users.UserAPI
 */
class PrintSellReceiptCommand2() {

    /**
     * @param positions Список позиций
     * @param payments Список оплат
     * @param clientPhone Телефон клиента
     * @param clientEmail Эл.почта клиента
     * @param paymentAddress Адрес места расчёта
     * @param paymentPlace Место расчёта
     */
    fun create(
        positions: List<Position>,
        payments: List<Payment>,
        clientPhone: String?,
        clientEmail: String?,
        paymentAddress: String? = null,
        paymentPlace: String? = null,
        userUuid: String? = null
    ) = PrintSellReceiptCommand(
        ArrayList<Receipt.PrintReceipt>().apply {
            add(
                Receipt.PrintReceipt(
                    PrintGroup(
                        UUID.randomUUID().toString(),
                        PrintGroup.Type.CASH_RECEIPT,
                        null,
                        null,
                        null,
                        null,
                        true,
                        null,
                        null
                    ),
                    positions,
                    payments.associate { it to it.value },
                    calculateChanges(
                        positions.sumByBigDecimal { it.totalWithSubPositionsAndWithoutDocumentDiscount },
                        payments
                    ),
                    hashMapOf()
                )
            )
        },
        null,
        clientPhone,
        clientEmail,
        BigDecimal.ZERO,
        paymentAddress,
        paymentPlace,
        userUuid
    )

    fun calculateChanges(sum: BigDecimal, payments: List<Payment>): Map<Payment, BigDecimal> {
        var remaining = MoneyCalculator.subtract(payments.sumByBigDecimal { it.value }, sum)
        val result = HashMap<Payment, BigDecimal>()
        for (payment in payments) {
            if (payment.paymentPerformer.paymentSystem?.paymentType != PaymentType.CASH) {
                result.put(payment, BigDecimal.ZERO)
                continue
            }

            val change = min(payment.value, remaining)
            remaining = MoneyCalculator.subtract(remaining, change)
            result.put(payment, change)
        }

        return result
    }
}