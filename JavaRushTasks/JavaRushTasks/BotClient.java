package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BotClient extends Client {
    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            System.out.println(message);
            if (message != null && message.contains(":")) {
                String[] data = message.split(": ");
                if (data.length>2) {
                    return;
                }
                Date calendar = Calendar.getInstance().getTime();
                HashMap<String, String> formats = new HashMap<String, String>() {
                    {
                        put("дата", "d.MM.YYYY");
                        put("день", "d");
                        put("месяц", "MMMM");
                        put("год", "YYYY");
                        put("время", "H:mm:ss");
                        put("час", "H");
                        put("минуты", "m");
                        put("секунды", "s");
                    }
                };
                if (formats.containsKey(data[1])) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(formats.get(data[1]));
                    message = String.format("Информация для %s: %s", data[0], dateFormat.format(calendar));
                    sendTextMessage(message);
                }
            }
        }

    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        return "date_bot_" + (int)(Math.random()*100);
    }



    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }
}
