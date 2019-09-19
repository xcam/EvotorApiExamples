package ru.xcam.evotor.example.interactor;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

import ru.evotor.framework.calculator.MoneyCalculator;
import ru.evotor.framework.component.PaymentPerformer;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;

public class InternetReceiptInteractor {

    private static final String TAG = "InternetReceiptInter";

    private static final String PRODUCT_NAME = "Лучший товар на этой кассе. Поставка ";
    private static final String MEASURE_NAME = "шт";

    private static final List<String> markCodes = new ArrayList<String>() {
        {
            add("00000046203946twkjoIm");
        }
    };


    private static final int RECEIPT_COUNT = 12;
    private static final int MAX_POSITION_IN_RECEIPT = 3;
    private static final int START_MARK_OFFSET = 312;
    private static final int STOP_MARK_OFFSET = 500;

    public void execute(final Activity activity) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                final StringBuffer buffer = new StringBuffer();
                Random random = new Random();

                int currentMarkOffser = START_MARK_OFFSET;

                for (int receiptIndex = 0; receiptIndex < RECEIPT_COUNT; receiptIndex++) { //12
                    int positionCount = random.nextInt(MAX_POSITION_IN_RECEIPT) + 1;

                    List<Position> positions = new ArrayList<>();
                    for (int i = 0; i < positionCount; i++) {
                        String mark = null;

                        BigDecimal quantity = new BigDecimal(random.nextInt(6) + 1);
                        if (random.nextInt(100) < 80 && currentMarkOffser < STOP_MARK_OFFSET) {
                            mark = getMark(currentMarkOffser);
                            currentMarkOffser++;
                            quantity = BigDecimal.ONE;
                        }

                        positions.add(createPosition(new BigDecimal(random.nextInt(200)), quantity, mark));
                    }


                    List<Payment> payments = new ArrayList<>();
                    payments.add(createPayment(positions));

                    String email = "random" + Math.abs(random.nextInt()) + "@someemail.ru";

                    final CountDownLatch latch = new CountDownLatch(1);
                    logReceipt(buffer, positions, payments, email);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    new PrintSellReceiptCommand(positions, payments, null, email).process(
                            activity,
                            new IntegrationManagerCallback() {
                                @Override
                                public void run(IntegrationManagerFuture integrationManagerFuture) {
                                    try {
                                        IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                                        if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                                            buffer.append("Receipt OK");
                                            buffer.append("\n");
                                        } else {
                                            throw new RuntimeException("" + result.getType().name() + " " + result.getError().getMessage());
                                        }

                                        latch.countDown();

                                    } catch (IntegrationException e) {
                                        final IntegrationException exception = e;
                                        e.printStackTrace();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(activity, "Есть проблема: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            });

                    try {
                        latch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                buffer.append("currentMarkOffser " + currentMarkOffser);
                buffer.append("\n");
                Log.e(TAG, "currentMarkOffser " + currentMarkOffser);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, buffer.toString());
                        Toast.makeText(activity, "Закончили! Раз! Два!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }.start();
    }

    private void logReceipt(StringBuffer buffer, List<Position> positions, List<Payment> payments, String email) {
        for (Position position : positions) {
            buffer.append(position.toString());
            buffer.append("\n");
            buffer.append("Position total " + position.getTotalWithoutDocumentDiscount().toPlainString());
            buffer.append("\n");
        }

        for (Payment payment : payments) {
            buffer.append(payment.toString());
            buffer.append("\n");
        }

        buffer.append("Email: " + email);
        buffer.append("\n");

    }

    private String getMark(int currentMarkOffser) {
        String markCode = markCodes.get(currentMarkOffser);
        return markCode + "AAFF" + "CAFE";
    }

    private Payment createPayment(List<Position> positions) {
        BigDecimal sum = BigDecimal.ZERO;
        for (Position position : positions) {
            sum = MoneyCalculator.add(sum, position.getTotalWithoutDocumentDiscount());
        }
        PaymentSystem paymentSystem = new PaymentSystem(PaymentType.CASH, "Cash", "ru.example.evotor.cash");
        return new Payment(
                UUID.randomUUID().toString(),
                sum,
                paymentSystem,
                new PaymentPerformer(
                        paymentSystem,
                        "ru.example.evotor.cash",
                        "ru.example.evotor.cash",
                        UUID.randomUUID().toString(),
                        "Cash"
                ),
                "purposeID",
                "accountID",
                "accountUserDescr"
        );
    }

    private Position createPosition(BigDecimal price, BigDecimal quantity, String mark) {
        Random random = new Random();
        String name = PRODUCT_NAME + random.nextInt(500);
        if (mark != null) {
            name = "Табак. " + name;
        }
        Position.Builder builder = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                name,
                MEASURE_NAME,
                0,
                price,
                quantity
        );

        if (mark != null) {
            builder.toTobaccoMarked(mark);
        }

        return builder.build();
    }
}
