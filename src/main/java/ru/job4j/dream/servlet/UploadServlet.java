package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/*
 * Класс, обрабатывающий загрузку файла на сервер.
 * */
public class UploadServlet extends HttpServlet {

    private final Random random = new Random();

    /*Метод doGet отображает список доступных файлов*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<String> images = new ArrayList<>();
        for (File name : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
//            System.out.println(name.getAbsolutePath());
            if (!name.isDirectory()) {
                images.add(name.getName());
            }
        }

        req.setAttribute("images", images);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/upload.jsp");
        dispatcher.forward(req, resp);
    }

    /*Метод doPost загружает выбранный файл на сервер в папку c:\\images\\*/
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        //Создаем фабрику, по которой можем понять, какие данные есть в запросе.
        //Данные могу быть: поля или файлы.
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            //Получаем список всех данных в запросе, парсит request чтобы взять
            // FileItem
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("c:\\images\\");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                //Элемент является полем?:
                if (!item.isFormField()) {
                    /*Если элемент не поле, то это файл и из него можно прочитать
                     * весь входной поток и записать его в файл или напрямую в
                     * базу данных.*/
                    //сохраняет файл на сервере в папке c:/images

                    File file = new File(folder + File.separator + item.getName());

                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        //переходим в метод doGet()
        doGet(req, resp);
    }
}
