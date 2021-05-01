package ru.job4j.dream.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Класс производящий скачивание файла.
 * Вспомогательный класс, для информации.
 * пока не используется.
 */
public class DownloadSomethingServlet extends HttpServlet {
    /**
     * В методе прочитываем файлы, находящиеся в папке c:/images.
     * Находим соответствующий значению атрибута name, полученного
     * от клиента, ищем по имени файла без расширения.
     * Чтобы указать, что сервер отправляет файл, т.е. файл будет скачан
     * браузером, устанавливаем типы данных в resp.setContentType(...) и
     * в resp.setHeader(...)
     * Мы выставляем заголовок ответа в протоколе. Таким образом, мы
     * сообщаем браузеру, что будем отправлять файл.
     * Установка  заголовка Content-Disposition  в объекте ответа сообщает браузеру,
     * как обрабатывать файл, к которому он обращается.
     * Браузеры понимают использование Content-Disposition как соглашение, но на
     * самом деле это не часть стандарта HTTP.
     * Далее открываем поток resp.getOutputStream() и найденный файл записываем
     * в выходной поток servlet, т.е. т.обр. отправляем файл на клиента.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Сервлет DownloadServlet метод doGet()");

        String name = req.getParameter("name");
        File downloadFile = null;
        for (File file : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
            if (name.equals(file.getName())) {
                downloadFile = file;
                break;
            }
        }
        req.setCharacterEncoding("UTF-8");

//        System.out.println("downloadFile.getName() = " + downloadFile.getName());

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");

        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }
}

