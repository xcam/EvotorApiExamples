package ru.xcam.evotor.example.interactor;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import ru.evotor.framework.component.PaymentPerformerApi;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.navigation.NavigationApi;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Position;
import ru.xcam.evotor.example.BuildConfig;

public class OpenReceiptInteractor {
    private static final String PRODUCT_UUID = UUID.randomUUID().toString();
    private static final String PRODUCT_NAME = "Лучший товар на этой кассе";
    private static final String MEASURE_NAME = "шт";

    public void execute(final Activity activity) throws JSONException {
//        List<PositionAdd> positionAdds = new ArrayList<>();

//        positionAdds.add(new PositionAdd(createPosition(new BigDecimal(100), new BigDecimal(1))));
//        positionAdds.add(new PositionAdd(createPosition(new BigDecimal(200), new BigDecimal(2))));

        PaymentPerformerApi.INSTANCE.getAllPaymentPerformers(activity.getPackageManager());
        List<PositionAdd> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 1; i <= 250; i++) {
            list.add(new PositionAdd(
                            Position.Builder.newInstance(
                                    //UUID позиции
                                    UUID.randomUUID().toString(),
                                    //UUID товара
                                    UUID.randomUUID().toString(),
                                    //Наименование
                                    "Тестовый " + i,
                                    //Наименование единицы измерения
                                    "шт",
                                    //Точность единицы измерения
                                    0,
                                    //Цена без скидок
                                    new BigDecimal(i),
                                    //Количество
                                    new BigDecimal(1)
                            ).build()
                    )
            );
        }

        JSONObject object = new JSONObject("{\"extra\":\"12345\"}");
        new OpenSellReceiptCommand(list, new SetExtra(object)).process(
                activity,
                new IntegrationManagerCallback() {
                    @Override
                    public void run(IntegrationManagerFuture integrationManagerFuture) {
                        try {
                            IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                            if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                                activity.startActivity(NavigationApi.createIntentForSellReceiptEdit(false));
                            }
                        } catch (IntegrationException e) {
                            e.printStackTrace();
                            Toast.makeText(activity, "Есть проблема: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private Position createPosition(BigDecimal price, BigDecimal quantity) {
        Set<ExtraKey> extraKeys = new HashSet<>();
        String uuid = UUID.randomUUID().toString();
        extraKeys.add(new ExtraKey(uuid, BuildConfig.APP_UUID, "ExtraKey " + uuid));

        return Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                PRODUCT_UUID,
                PRODUCT_NAME,
                MEASURE_NAME,
                0,
                price,
                quantity
        )
                .setExtraKeys(extraKeys)
                .build();
    }
}
