package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Post;
import ru.job4j.dream.store.PsqlStore;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class PostServlet extends HttpServlet {

    /*Перенаправим в теле метода запрос в posts.jsp
     * И загружаем в request список вакансий*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
//        MemStore store = MemStore.instOf();
        Store store = PsqlStore.instOf();
        Collection<Post> allPosts = store.findAllPosts();
        req.setAttribute("posts", allPosts);
        req.getRequestDispatcher("post/posts.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        String description = req.getParameter("description");
//        MemStore store = MemStore.instOf();
        Store store = PsqlStore.instOf();
        store.savePost(
                new Post(
                        Integer.parseInt(id),
                        name,
                        description));
        resp.sendRedirect(req.getContextPath() + "/posts.do");
    }
}
