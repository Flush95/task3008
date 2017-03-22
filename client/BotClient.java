package com.javarush.task.task30.task3008.client;


import com.javarush.task.task30.task3008.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Admin on 22.03.2017.
 */
public class BotClient extends Client {

    public static void main(String[] args) {
        BotClient botClient = new BotClient();
        botClient.run();
    }

    public class BotSocketThread extends Client.SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String name = "";
            String text;

            if (message.contains(": ")) {
                name = message.split(": ")[0];
                text = message.split(": ")[1];
            } else {
                text = message;
            }

            SimpleDateFormat time;
            if (text.equalsIgnoreCase("дата"))
                time = (new SimpleDateFormat("d.MM.YYYY"));
            else if (text.equalsIgnoreCase("день"))
                time = (new SimpleDateFormat("d"));
            else if (text.equalsIgnoreCase("месяц"))
                time = (new SimpleDateFormat("MMMM"));
            else if (text.equalsIgnoreCase("год"))
                time = (new SimpleDateFormat("YYYY"));
            else if (text.equalsIgnoreCase("время"))
                time = (new SimpleDateFormat("H:mm:ss"));
            else if (text.equalsIgnoreCase("час"))
                time = (new SimpleDateFormat("H"));
            else if (text.equalsIgnoreCase("минуты"))
                time = (new SimpleDateFormat("m"));
            else if (text.equalsIgnoreCase("секунды"))
                time = (new SimpleDateFormat("s"));
            else
                time = null;
            if (time != null)
                sendTextMessage("Информация для " + name + ": " + time.format(Calendar.getInstance().getTime()));
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
        return "date_bot_" + (int)(Math.random() * 100);
    }
}
