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
import ru.evotor.framework.core.action.command.PrintSellReceiptCommand2;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandErrorData;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandErrorDataFactory;
import ru.evotor.framework.counterparties.collaboration.agent_scheme.Agent;
import ru.evotor.framework.counterparties.collaboration.agent_scheme.Principal;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.position.AgentRequisites;
import ru.evotor.framework.users.UserApi;
import ru.xcam.evotor.example.telegram.TelegramApi;

public class InternetReceiptInteractor {

    private static final String TAG = "InternetReceiptInter";

    private static final String PRODUCT_NAME = "Лучший товар на этой кассе. Поставка ";
    private static final String MEASURE_NAME = "шт";

    private static final List<String> markCodes = new ArrayList<String>() {
        {
            add("00000046203946twkjoIm");
        }
    };


    private static final int RECEIPT_COUNT = 110;
    private static final int MAX_POSITION_IN_RECEIPT = 7;

    public void execute(final Activity activity) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                String userUuid = UserApi.getAllUsers(activity).get(0).getUuid();

                Random random = new Random();
                TelegramApi.INSTANCE.sendMessage("Начали");
                for (int receiptIndex = 1; receiptIndex < RECEIPT_COUNT; receiptIndex++) { //12
                    int positionCount = 1;//random.nextInt(MAX_POSITION_IN_RECEIPT) + 1;

                    List<Position> positions = new ArrayList<>();
                    for (int i = 0; i < positionCount; i++) {
                        String mark = null;

                        BigDecimal quantity = new BigDecimal(random.nextInt(6) + 1);

                        positions.add(createPosition(new BigDecimal(random.nextInt(200)), quantity, mark));
                    }


                    List<Payment> payments = new ArrayList<>();
                    payments.add(createPayment(positions));

                    String email = "random" + Math.abs(random.nextInt()) + "@someemail.ru";

                    final CountDownLatch latch = new CountDownLatch(1);
                    final int index = receiptIndex;

                    new PrintSellReceiptCommand2().create(positions, payments, null, email, null, null, userUuid).process(
                            activity,
                            new IntegrationManagerCallback() {
                                @Override
                                public void run(IntegrationManagerFuture integrationManagerFuture) {
                                    try {
                                        IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                                        if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                                            if (index % 300 == 0) {
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        super.run();
                                                        TelegramApi.INSTANCE.sendMessage("Закончили чек:" + index);
                                                    }
                                                }.start();
                                            }
                                        } else {
                                            if (result.getType() != IntegrationManagerFuture.Result.Type.OK) {
                                                PrintReceiptCommandErrorData data = PrintReceiptCommandErrorDataFactory.create(result.getData());
                                                String dataStr = "";
                                                if (data != null) {
                                                    dataStr = "ext: ";
                                                    dataStr += "errorcode = " + ((PrintReceiptCommandErrorData.KktError) data).getKktErrorCode();
                                                    dataStr += " errordescription = " + ((PrintReceiptCommandErrorData.KktError) data).getKktErrorDescription();
                                                }
                                                Log.e("TAG", dataStr);

                                                throw new RuntimeException("" + result.getType().name() + " " + result.getError().getMessage() + " " + dataStr);
                                            }
                                        }

                                        latch.countDown();

                                    } catch (IntegrationException e) {
                                        final IntegrationException exception = e;
                                        e.printStackTrace();
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new Thread() {
                                                    @Override
                                                    public void run() {
                                                        super.run();
                                                        TelegramApi.INSTANCE.sendMessage("Есть проблема: " + exception.getMessage());
                                                    }
                                                }.start();

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

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                TelegramApi.INSTANCE.sendMessage("Закончили! Раз! Два!");

                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "Закончили! Раз! Два!", Toast.LENGTH_LONG).show();
                    }
                });

            }
        }.start();
    }

    private void logReceipt(StringBuffer buffer, List<Position> positions, List<Payment> payments, String email) {
//        for (Position position : positions) {
//            buffer.append(position.toString());
//            buffer.append("\n");
//            buffer.append("Position total " + position.getTotalWithoutDocumentDiscount().toPlainString());
//            buffer.append("\n");
//        }
//
//        for (Payment payment : payments) {
//            buffer.append(payment.toString());
//            buffer.append("\n");
//        }
//
//        buffer.append("Email: " + email);
//        buffer.append("\n");

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
//        List<String> phones = new ArrayList<>();
//        phones.add("79264618881");
//        AgentRequisites agentRequisites2 = new AgentRequisites(
//                new Agent(
//                        null,
//                        Agent.Type.AGENT,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null,
//                        null
//                ),
//                null,
//                new Principal(
//                        null,
//                        null,
//                        null,
//                        "PrincipalShortName",
//                        "1122334563",
//                        null,
//                        phones,
//                        null
//                ),
//                null,
//                null
//        );
//
//        AgentRequisites agentRequisites = AgentReqFactory.INSTANCE.create("1122334563", "SomeName", phones);

        Position.Builder builder = Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                name,
                MEASURE_NAME,
                0,
                price,
                quantity
        );


//        if (mark != null) {
//            builder.toTobaccoMarked(mark);
//        }

        return builder.build();
    }
}
