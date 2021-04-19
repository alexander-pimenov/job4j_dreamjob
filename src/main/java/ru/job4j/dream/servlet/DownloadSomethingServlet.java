package ru.job4j.dream.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/*
 * Класс производящий скачивание файла.
 * Вспомогательный класс, для информации.
 * пока не используется.
 * */
public class DownloadSomethingServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Сервлет DownloadServlet метод doGet()");

        String name = req.getParameter("name");
        File downloadFile = null;
//        for (File file : new File("c:\\images\\").listFiles()) {
        for (File file : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
            if (name.equals(file.getName())) {
                downloadFile = file;
                break;
            }
        }
        req.setCharacterEncoding("UTF-8");

        //Мы выставляем заголовок ответа в протоколе. Таким образом, мы
        // сообщаем браузеру, что будем отправлять файл.
        //Установка  заголовка Content-Disposition  в объекте ответа сообщает браузеру,
        //как обрабатывать файл, к которому он обращается.
        //Браузеры понимают использование Content-Disposition как соглашение, но на
        //самом деле это не часть стандарта HTTP.
//        System.out.println("downloadFile.getName() = " + downloadFile.getName());

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");

        //Открываем поток и записываем файл в выходной поток servlet.
        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }
}

