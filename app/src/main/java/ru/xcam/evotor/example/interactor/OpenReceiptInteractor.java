package ru.xcam.evotor.example.interactor;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ru.evotor.framework.component.PaymentDelegatorApi;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationCallback;
import ru.evotor.framework.receipt.formation.api.ReceiptFormationException;
import ru.evotor.framework.receipt.formation.api.SellApi;

public class OpenReceiptInteractor {
    private static final String PRODUCT_UUID = UUID.randomUUID().toString();
    private static final String PRODUCT_NAME = "Лучший товар на этой кассе";
    private static final String MEASURE_NAME = "шт";

    public void execute(final Activity activity) throws JSONException {
        List<PositionAdd> list = new ArrayList<>();
        list.add(
                new PositionAdd(
                        Position.Builder.newInstance(
                                //UUID позиции
                                UUID.randomUUID().toString(),
                                //UUID товара
                                UUID.randomUUID().toString(),
                                //Наименование
                                "Тестовый товар",
                                //Наименование единицы измерения
                                "шт",
                                //Точность единицы измерения
                                0,
                                //Цена без скидок
                                new BigDecimal(100),
                                //Количество
                                new BigDecimal(1)
                        ).build()
                )
        );

        new OpenSellReceiptCommand(list, null).process(
                activity,
                new IntegrationManagerCallback() {
                    @Override
                    public void run(IntegrationManagerFuture integrationManagerFuture) {
                        try {
                            IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                            if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
//                                activity.startActivity(NavigationApi.createIntentForSellReceiptPayment());
                                SellApi.moveCurrentReceiptDraftToPaymentStage(
                                        activity,
                                        PaymentDelegatorApi.INSTANCE.getAllPaymentDelegators(activity.getPackageManager()).get(0),
                                        new ReceiptFormationCallback() {
                                            public void onError(ReceiptFormationException error) {
                                            }

                                            public void onSuccess() {
                                            }

                                        }
                                );
                            }
                        } catch (IntegrationException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Есть проблема: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
