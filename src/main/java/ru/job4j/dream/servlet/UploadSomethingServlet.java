package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

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

/**
 * Класс, обрабатывающий загрузку файла на сервер.
 * Вспомогательный класс, для информации.
 * пока не используется.
 */
public class UploadSomethingServlet extends HttpServlet {

    /**
     * Метод doGet отображает список доступных файлов.
     * Все файлы папки c:/images собираются в список, и передаются через
     * атрибут images клиенту в браузер. Где выводятся списком.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Сервлет UploadServlet метод doGet()");
        List<String> images = new ArrayList<>();
        for (File name : Objects.requireNonNull(new File("c:\\images\\").listFiles())) {
            System.out.println(name.getAbsolutePath());
            if (!name.isDirectory()) {
                images.add(name.getName());
            }
        }
        req.setAttribute("images", images);
        RequestDispatcher dispatcher = req.getRequestDispatcher("/upload.jsp");
        dispatcher.forward(req, resp);
    }

    /**
     * Метод doPost загружает выбранный файл на сервер в папку c:\\images\\
     * Создает фабрику DiskFileItemFactory, по которой можем понять, какие
     * данные есть в запросе.
     * Данные могу быть полями или файлами.
     * Получает список всех данных в запросе, парсит request, чтобы взять
     * FileItem. Если не является полем, то это файл и из него можно прочитать
     * весь входной поток и записать его в файл или напрямую в базу данных.
     * Если FileItem поле, то у него можем спросить имя getFileName() и получить
     * его значение getString().
     * В конце метода переходит в метод doGet().
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("Сервлет UploadServlet метод doPost()");
        String name = "";

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            //Получаем список всех данных в запросе
            List<FileItem> items = upload.parseRequest(req);
            File folder = new File("c:\\images\\");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : items) {
                if (!item.isFormField()) {
                    //если это файл, то
                    File file = new File(folder + File.separator + item.getName());
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                    }
                } else {
//                    если это поле, то
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString();
                    }
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        doGet(req, resp);
    }

    /**
     * Метод определения расширения файла.
     * Если в имени файла есть точка и она не является первым символом
     * в названии файла, то вырезаем все знаки после последней точки в
     * названии файла, то есть ХХХХХ.txt -> txt
     * В противном случае возвращаем заглушку "", то есть расширение не найдено.
     *
     * @param file файл
     * @return строковое представление расширения.
     * Если расширения нет, то вернется ничего "".
     */
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    /**
     * Метод извлекающий имя файла
     *
     * @param part парта, пришедшая из формы.
     * @return строковое представление имени файла.
     * Если не получится получить, то вернет null.
     */
    private String extractFileName(Part part) {
        // form-data; name="file"; filename="C:\file1.zip"
        // form-data; name="file"; filename="C:\Note\file2.zip"
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                // C:\file1.zip
                // C:\Note\file2.zip
                String clientFileName = s.substring(s.indexOf("=") + 2, s.length() - 1);
                clientFileName = clientFileName.replace("\\", "/");
                int i = clientFileName.lastIndexOf('/');
                // file1.zip
                // file2.zip
                return clientFileName.substring(i + 1);
            }
        }
        return null;
    }
}
