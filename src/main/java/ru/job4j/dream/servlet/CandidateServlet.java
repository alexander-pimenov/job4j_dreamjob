package ru.job4j.dream.servlet;

import ru.job4j.dream.model.Candidate;
import ru.job4j.dream.store.Store;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

public class CandidateServlet extends HttpServlet {

    /*Перенаправим в теле метода запрос в candidates.jsp
     * и загрузим в request список кандидатов*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        Store store = Store.instOf();
        Collection<Candidate> allCandidates = store.findAllCandidates();
        req.setAttribute("candidates", allCandidates);
        req.getRequestDispatcher("candidate/candidates.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");
        String id = req.getParameter("id");
        String name = req.getParameter("name");
        Store.instOf().saveCandidate(
                new Candidate(
                        Integer.parseInt(id),
                        name));
        resp.sendRedirect(req.getContextPath() + "/candidates.do");
    }
}
