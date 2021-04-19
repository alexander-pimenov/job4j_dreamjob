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

/*
 * Класс производящий скачивание файла.
 * */
public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String name = req.getParameter("photoId");
        resp.setContentType("image/png; image/jpeg; image/svg+xml; image/webp");
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

        //Открываем поток и записываем файл в выходной поток servlet.
        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }
}
