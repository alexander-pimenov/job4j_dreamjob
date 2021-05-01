package ru.job4j.dream.servlet;

import org.apache.commons.io.FilenameUtils;

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
 */
public class DownloadServlet extends HttpServlet {
    /**
     * В методе прочитываем файлы, находящиеся в папке c:/images.
     * Находим соответствующий значению атрибута name, полученного
     * от клиента, ищем по имени файла без расширения.
     * Чтобы указать, что сервер отправляет файл, т.е. файл будет скачан
     * браузером, устанавливаем типы данных в resp.setContentType(...) и
     * в resp.setHeader(...)
     * Далее открываем поток resp.getOutputStream() и найденный файл
     * записываем в выходной поток servlet, т.е. т.обр. отправляем файл на клиента.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String name = req.getParameter("photoId");
        resp.setContentType("image/png; image/jpeg; image/svg+xml; image/webp");
//        resp.setContentType("application/octet-stream");
        File downloadFile = null;
        for (File file : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
            if (name.equals(FilenameUtils.getBaseName(file.getName()))) {
                downloadFile = file;
                break;
            }
            if (name.equals(file.getName())) {
                downloadFile = file;
                break;
            }
        }

        resp.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");

        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }
}
