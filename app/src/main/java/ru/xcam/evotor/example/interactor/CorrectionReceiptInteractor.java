package ru.xcam.evotor.example.interactor;

import android.app.Activity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import ru.evotor.framework.kkt.api.DocumentRegistrationCallback;
import ru.evotor.framework.kkt.api.DocumentRegistrationException;
import ru.evotor.framework.kkt.api.KktApi;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.SettlementType;
import ru.evotor.framework.receipt.TaxationSystem;
import ru.evotor.framework.receipt.correction.CorrectionType;
import ru.evotor.framework.receipt.position.VatRate;

public class CorrectionReceiptInteractor {
    public void execute(final Activity activity) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                for (VatRate rate : VatRate.values()) {
                    final CountDownLatch latch = new CountDownLatch(1);
                    KktApi.registerCorrectionReceipt(
                            activity,
                            SettlementType.INCOME,
                            TaxationSystem.COMMON,
                            CorrectionType.BY_PRESCRIBED,
                            "basis " + rate.name(),
                            "prescr",
                            new Date(100000000),
                            BigDecimal.TEN,
                            PaymentType.CASH,
                            rate,
                            "desc",
                            new DocumentRegistrationCallback() {

                                @Override
                                public void onSuccess(@Nullable UUID uuid) {
                                    latch.countDown();
                                }

                                @Override
                                public void onError(@NotNull DocumentRegistrationException e) {
                                    latch.countDown();
                                    e.printStackTrace();
                                }
                            }
                    );
                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                }

            }
        }.start();
    }
}
