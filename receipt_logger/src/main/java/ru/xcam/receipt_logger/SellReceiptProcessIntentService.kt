package ru.xcam.receipt_logger

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import ru.evotor.framework.receipt.Receipt
import ru.evotor.framework.receipt.ReceiptApi

private const val STORAGE_NAME = "SellReceiptProcessIntentService"
private const val KEY_LAST_PROCESSED_RECEIPT_UUID = "LAST_PROCESSED_RECEIPT_UUID"
private const val TAG = "ru.xcam.receipt_logger"

class SellReceiptProcessIntentService : IntentService("SellReceiptProcessIntentService") {

    private lateinit var pref: SharedPreferences
    override fun onCreate() {
        super.onCreate()
        pref = applicationContext.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE)
    }

    override fun onHandleIntent(intent: Intent?) {
        try {
            Log.e(TAG, "Start process #1")
            handleIntent()
        } catch (e: Exception) {
            Log.e(TAG, "GotException ${e.message}", e)
        }

        // Дадим время софту Эвотора поработать
        Thread.sleep(1000)

        // Попробуем еще раз - может появились новые чеки
        try {
            Log.e(TAG, "Start process #2")
            handleIntent()
        } catch (e: Exception) {
            Log.e(TAG, "GotException ${e.message}", e)
        }
    }

    private fun handleIntent() {

        ReceiptApi.getReceiptHeaders(
            context = this,
            type = Receipt.Type.SELL
        )?.use { receiptCursor ->
            val lastProcessedReceiptUuid = pref.getString(KEY_LAST_PROCESSED_RECEIPT_UUID, null)

            // Перемещаемся на последний чек
            if (!receiptCursor.moveToLast()) {
                // Чеков нет -> выходим
                Log.e(TAG, "End process: Чеков нет -> выходим")
                return
            }

            if (lastProcessedReceiptUuid != null) {
                // В предыдущие запуски мы уже обрабатывали чеки
                while (
                    receiptCursor.getValue()?.uuid != lastProcessedReceiptUuid &&
                    receiptCursor.moveToPrevious()
                ) {
                    // Мотаем до последнего обработанного чека
                }

                // Мы либо на первом чеке в курсоре, либо на чеке который уже обработали в предыдущий заход
                // Если на чеке который уже обработали в предыдущий заход -> двигаемся на 1 чек вперед чтобы быть на чеке,
                // который еще не обрабатывали

                if (receiptCursor.getValue()?.uuid == lastProcessedReceiptUuid) {
                    if (!receiptCursor.moveToNext()) {
                        // а следующего чека нет -> обрабатывать нечего - выходим
                        Log.e(TAG, "End process: следующего чека нет -> обрабатывать нечего")
                        return
                    }
                } else{
                    // Мы на первом чеке в курсоре - это значит что мы не нашли предыдущий обработанный чек
                    // Такая ситуация возможна, если пользователь восстановился из старого бекапа
                    // Что в этом случае делать - вопрос бизнес логики. Возможно стоит обработать только последний чек
                    // В текущей реализации обработаются ВСЕ чеки из базы (речь может идти о тысячах чеков)
                }
            }

            // Обрабатываем чеки
            do {
                val header = receiptCursor.getValue()
                header ?: continue
                val receipt = ReceiptApi.getReceipt(
                    context = this,
                    uuid = header.uuid
                )

                if (processReceipt(receipt)) {
                    pref.edit().putString(KEY_LAST_PROCESSED_RECEIPT_UUID, header.uuid).apply()
                } else {
                    // Обработка чека не удалась - выходим
                    break
                }
            } while (receiptCursor.moveToNext())
        }

        Log.e(TAG, "End process: все хорошо")
    }

    private fun processReceipt(receipt: Receipt?): Boolean {
        receipt ?: return {
            Log.e(TAG, "Process receipt with UUID ${receipt?.header?.uuid}: FAIL (Empty receipt)")
            false
        }.invoke()

        if (receipt.getPayments().isEmpty()) {
            Log.e(TAG, "Process receipt with UUID ${receipt.header.uuid}: FAIL (Empty Payments)")
            return false
        }

        if (receipt.getPositions().isEmpty()) {
            Log.e(TAG, "Process receipt with UUID ${receipt.header.uuid}: FAIL (Empty Positions)")
            return false
        }

        if (receipt.printDocuments.isEmpty()) {
            Log.e(
                TAG,
                "Process receipt with UUID ${receipt.header.uuid}: FAIL (Empty printDocuments)"
            )
            return false
        }

        Log.e(TAG, "Process receipt with UUID ${receipt.header.uuid}: SUCCESS")

        return true
    }

    companion object {
        @JvmStatic
        fun start(context: Context) {
            val intent = Intent(context, SellReceiptProcessIntentService::class.java)
            context.startService(intent)
        }
    }
}