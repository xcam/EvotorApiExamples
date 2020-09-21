package ru.xcam.evotor.example.interactor;

import android.app.Activity;

import ru.evotor.devices.commons.Constants;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.exception.DeviceServiceException;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;

public class PrintDocumentInteractor {
    public void execute(final Activity activity) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                DeviceServiceConnector.startInitConnections(activity.getApplicationContext());
                try {
                    DeviceServiceConnector.getPrinterService().printDocument(
                            Constants.DEFAULT_DEVICE_INDEX, new PrinterDocument(
                                    new PrintableBarcode("4600682557500",
                                            PrintableBarcode.BarcodeType.QR_CODE)));
                } catch (DeviceServiceException e) {
                    e.printStackTrace();
                }


            }
        }.start();
    }
}
