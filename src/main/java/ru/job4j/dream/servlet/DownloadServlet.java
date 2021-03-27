package ru.job4j.dream.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/*
 * Класс производящий скачивание файла.
 * */
public class DownloadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        //Мы выставляем заголовок ответа в протоколе. Таким образом, мы сообщаем браузеру, что будем отправлять файл.
        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");

        //Открываем поток и записываем файл в выходной поток servlet.
        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }
    }


        /*-----------------------------------------------------------------*/
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        File usersFile = null;
//        //Переберем все файлы находящиеся в папке "c:\\images\\"
//        for (File file : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
//            System.out.println(file.getAbsolutePath()); //печатаем абсолютный путь к файлу
//            /*Если мы нашли нужный нам файл "users.txt", то запомним его в usersFile
//             * и выходим из цикла.*/
//            if ("users.txt".equals(file.getName())) {
//                usersFile = file;
//                break;
//            }
//        }
//
//        //Чтобы указать, что сервер ОТПРАВЛЯЕТ файл, т.е. файл будет скачан браузером нужно установить тип данных.
//        //При открытии ссылки http://localhost:8080/dreamjob/download браузер скачает файл.
//        req.setCharacterEncoding("UTF-8");
//        resp.setContentType("application/octet-stream");
//        //Мы выставляем заголовок ответа в протоколе. Таким образом, мы сообщаем браузеру, что будем отправлять файл.
//        resp.setHeader("Content-Disposition", "attachment; filename=\"" + usersFile.getName() + "\"");
//
//
//        //Открываем поток и записываем содержимое файла в выходной поток servlet.
//        try (FileInputStream stream = new FileInputStream(Objects.requireNonNull(usersFile));
//             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
//
//             PrintWriter out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream()))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                out.println(line);
//                System.out.println(line);
//            }
//        }
//    }
        /*-------------------------------------------------------------------------*/

    }
//        String id = req.getParameter("id");
//        req.setCharacterEncoding("UTF-8");
//        resp.setContentType("id=" + id);
//        resp.setContentType("image/png");
//        resp.setHeader("Content-Disposition", "attachment; filename=\"" + id + "\"");
//        File file = new File("images" + File.separator + id);
//        try (BufferedReader reader = new BufferedReader(new FileReader(file));
//             PrintWriter out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream()))
//        ) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                out.print(line);
//            }
//        }

