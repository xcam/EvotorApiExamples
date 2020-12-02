package ru.xcam.evotor.example.interactor;

import android.app.Activity;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand;
import ru.xcam.evotor.example.utils.Logger;

public class ZReportInteractor {
    public void execute(final Activity activity) {
        log("ZReportInteractor#execute start");
        new Thread("BackgroundThread") {
            @Override
            public void run() {
                final CountDownLatch latch = new CountDownLatch(1);
                executeZReportCommand(activity, latch);

                try {
                    boolean result = latch.await(30, TimeUnit.SECONDS);
                    log("ZReportInteractor#await result: " + result);
                } catch (
                        InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        log("ZReportInteractor#execute finish");
    }

    private void executeZReportCommand(final Activity activity, final CountDownLatch latch) {
        PrintZReportCommand command = new PrintZReportCommand();
        command.process(activity, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                log("ZReportInteractor#run");
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    log("ZReportInteractor#got result: " + result.getType());
                    switch (result.getType()) {
                        case OK:
                            log("ZReportInteractor# RESULT OK!");
                            break;
                        case ERROR:
                            log("ZReportInteractor# RESULT ERROR!");
                            break;
                    }
                } catch (IntegrationException e) {
                    log("ZReportInteractor#got exception");
                    e.printStackTrace();
                }
                latch.countDown();
            }
        });
    }

    private void log(String message) {
        Logger.log(Thread.currentThread().getName() + ": " + message);
    }
}
