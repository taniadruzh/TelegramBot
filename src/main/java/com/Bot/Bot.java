package com.Bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class Bot extends TelegramLongPollingBot {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        sendMsg(update.getMessage().getChatId().toString(), message);
    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param s Строка, которую необходимот отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        setButtons(sendMessage);
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(getExchangeRateNow(s));
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "RateBot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "675282815:AAGAeenVxaW3hmusGCc8gzK6khwJGwTq7FA";
    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboard1 = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboard1.add(new KeyboardButton("USD rate UAH"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboard2 = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboard2.add(new KeyboardButton("EUR rate UAH"));

        //Третья
        KeyboardRow keyboard3 = new KeyboardRow();
        // Добавляем кнопки в третью строчку клавиатуры
        keyboard3.add(new KeyboardButton("RUB rate UAH"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboard1);
        keyboard.add(keyboard2);
        keyboard.add(keyboard3);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    private String getExchangeRateNow(String currency){
        if (currency.equals("/start")){
            return "Привет";
        }
        //
        currency = currency.substring(0,currency.indexOf(" ")).trim();

        URLConnection connection;
        InputStream inputStream = null;
        String answer = new String();

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();

        try {
            connection = new URL("https://api.privatbank.ua/p24api/exchange_rates?json&date="+dateFormat.format(date)).openConnection();
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(inputStream);
        String rateStr = new String();

        while (sc.hasNext()) {
            rateStr += sc.next();
        }

        System.out.println(rateStr);
        Rate rate = GSON.fromJson(rateStr, Rate.class);

        for (ExchangeRate e :
                rate.getExchangeRate()) {

            if (currency.equals(e.getCurrency())) {
                answer = String.format("Покупка: %s \nПродажа: %s", e.getPurchaseRateNB(), e.getSaleRateNB());
            }
        }

        return answer;
    }
}