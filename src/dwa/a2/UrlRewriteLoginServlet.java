package dwa.a2;

import dwa.Configuration;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "UrlRewriteLoginServlet", urlPatterns = {"/UrlRewriteLoginServlet"})
public class UrlRewriteLoginServlet extends HttpServlet {
    private static final String QUERY = "SELECT * FROM user WHERE login='%s' AND password='%s'";


    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // get params
        String login = request.getParameter("login");
        String password = request.getParameter("password");

        try {
            Class.forName(Configuration.DRIVER_CLASS);
            Connection con = DriverManager.getConnection(Configuration.URL);
            Statement stmt = con.createStatement();
            String query = String.format(QUERY, login, password);

            ResultSet rs = stmt.executeQuery(query);

            // if logged in successfully
            if (rs.next()) {
                HttpSession session = request.getSession();
                session.setAttribute("login", login);
                session.setAttribute("message", "This is simply awesome. Check it out <a href=\"http://mirek.s" +
                        ".cnl.sk/hijack.php\">here</a>!");
                session.setMaxInactiveInterval(30 * 60);

                //Get the encoded URL string
                String encodedURL = response.encodeRedirectURL("a2.url.rewrite.main.jsp");
                response.sendRedirect(encodedURL);
            } else {
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/a2.url.rewrite.login.jsp");
                request.setAttribute("alert", "Login or password is wrong.");
                rd.include(request, response);
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
}
