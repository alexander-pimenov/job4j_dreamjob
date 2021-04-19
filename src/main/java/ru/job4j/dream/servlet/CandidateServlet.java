package ru.job4j.dream.servlet;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class CandidateServlet extends HttpServlet {

    /*Перенаправим в теле метода запрос в candidates.jsp
     * и загрузим в request список кандидатов*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        Store store = PsqlStore.instOf();
//        MemStore store = MemStore.instOf();
        Collection<Candidate> allCandidates = store.findAllCandidates();
        req.setAttribute("candidates", allCandidates);
        req.getRequestDispatcher("candidate/candidates.jsp").forward(req, resp);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        int id = Integer.parseInt(req.getParameter("id"));
        int candidateId = 0;
        String name = "";
        Integer photoId;

//        MemStore store = MemStore.instOf();

        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletContext servletContext = this.getServletConfig().getServletContext();
        //Устанавливаем временную директорию
        File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
        factory.setRepository(repository);
        //Создаем загрузчик
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            //Получаем список всех данных в запросе,
            // парсит request чтобы взять FileItem
            List<FileItem> formItems = upload.parseRequest(req);
            File folder = new File("c:\\images\\");
            if (!folder.exists()) {
                folder.mkdir();
            }
            for (FileItem item : formItems) {
                //Элемент является полем?
                //Если нет, то это файл и его загружаем
                if (!item.isFormField()) {
                    //удалим фото кандидата при перезаписи или удалении кандидата
                    Arrays.stream(Objects.requireNonNull(folder.listFiles()))
                            .filter(file -> FilenameUtils.getBaseName(file.getName()).equals(req.getParameter("id")))
                            .findAny().ifPresent(File::delete); // if file with this name is exist - delete it, because it's update

                    /*Если элемент не поле, то это файл и из него можно прочитать
                     * весь входной поток и записать его в файл или напрямую в
                     * базу данных.*/
                    //сохраняет файл на сервере в папке c:/images

                    File file = new File(folder + File.separator
                            + candidateId
                            + "."
                            + FilenameUtils.getExtension(item.getName())
                    );
                    try (FileOutputStream out = new FileOutputStream(file)) {
                        out.write(item.getInputStream().readAllBytes());
                    }
                } else {
//                    если это поле, то
                    if ("name".equals(item.getFieldName())) {
                        name = item.getString();
                    }
                    Candidate candidate = new Candidate(id, name, id);
                    Store store = PsqlStore.instOf();
                    Candidate can = store.saveCandidate(candidate);
                    candidateId = can.getId();
                    candidate.setPhotoId(candidateId);
                    store.saveCandidate(candidate);
                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        }
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
