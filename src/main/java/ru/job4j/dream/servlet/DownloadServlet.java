package ru.job4j.dream.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

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

        //Открываем поток и записываем его в выходной поток servlet.
        try (FileInputStream stream = new FileInputStream(downloadFile)) {
            resp.getOutputStream().write(stream.readAllBytes());
        }


        /*-----------------------------------------------------------------*/
//    @Override
//    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//
//        File users = null;
//        for (File file : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
//            System.out.println(file.getAbsolutePath());
//            if ("users.txt".equals(file.getName())) {
//                users = file;
//                break;
//            }
//        }
//
//        req.setCharacterEncoding("UTF-8");
//        resp.setContentType("application/octet-stream");
//        //Мы выставляем заголовок ответа в протоколе. Таким образом, мы сообщаем браузеру, что будем отправлять файл.
//        resp.setHeader("Content-Disposition", "attachment; filename=\"" + users.getName() + "\"");
//
//
//        //Открываем поток и записываем его в выходной поток servlet.
//        try (FileInputStream stream = new FileInputStream(users);
//             BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
//
//             PrintWriter out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream()))) {
//
//            String line;
//            while ((line = reader.readLine()) != null) {
//                out.println(line);
////                System.out.println(line);
//            }
//        }
        /*-------------------------------------------------------------------------*/

    }
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

