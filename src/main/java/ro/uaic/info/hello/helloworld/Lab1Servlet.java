package ro.uaic.info.hello.helloworld;

import ro.uaic.info.hello.helloworld.models.RepositoryEntry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

@WebServlet(name = "Lab1Servlet",
        urlPatterns = {"/lab1"},
        initParams = {
                @WebInitParam(name = "repositoryFilename", value = "repository.txt")
        })
public class Lab1Servlet extends HttpServlet {
    private static final String REPOSITORY_FILENAME_PARAMETER = "repositoryFilename";
    private static final String SEPARATOR = "#";
    private static final String MOCK_PARAMETER = "mock";
    private static final String KEY_PARAMETER = "key";
    private static final String VALUE_PARAMETER = "value";
    private static final String SYNC_PARAMETER = "sync";
    private static final String CONFIRMATION_MESSAGE = "Mock is true";
    private static final String KEY_IS_EMPTY_MESSAGE = "Key is empty";
    private static final String INVALID_VALUE_MESSAGE = "Invalid value. The value should be greater or equal to 0";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String MOZILLA_BROWSER = "Mozilla";

    private OutputStream outputStream;
    private List<RepositoryEntry> repositoryEntries;

    @Override
    public void init() throws ServletException {
        super.init();

        repositoryEntries = new ArrayList<>();

        String repositoryFilename = getServletConfig().getInitParameter(REPOSITORY_FILENAME_PARAMETER);
        try {
            Path path = Paths.get(getServletContext().getRealPath(repositoryFilename));
            outputStream = new BufferedOutputStream(Files.newOutputStream(path));
        } catch (IOException e) {
            getServletContext().log("Could not open the repository");
            throw new ServletException("IOException in init method");
        }
    }

    @Override
    public void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws IOException {
        writeRequestInformationInServerLog(request);

        try (PrintWriter out = response.getWriter()) {
            boolean mock = Boolean.parseBoolean(request.getParameter(MOCK_PARAMETER));
            if (mock) {
                out.println(CONFIRMATION_MESSAGE);
            } else {
                String key = request.getParameter(KEY_PARAMETER);
                if (key == null || key.equals("")) {
                    out.println(KEY_IS_EMPTY_MESSAGE);
                } else {
                    int value = Integer.parseInt(request.getParameter(VALUE_PARAMETER));
                    if (isValueValid(value)) {
                        boolean sync = Boolean.parseBoolean(request.getParameter(SYNC_PARAMETER));
                        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                        RepositoryEntry repositoryEntry = new RepositoryEntry(key, value, timestamp);
                        storeEntryInRepository(repositoryEntry, sync);
                        Collections.sort(repositoryEntries);
                        if (isBrowserRequest(request)) {
                            showRepositoryContentInHTMLFormat(response, sync);
                        } else {
                            showRepositoryContentInSimpleText(response, sync);
                        }
                    } else {
                        out.println(INVALID_VALUE_MESSAGE);
                    }
                }
            }
        }
    }

    private void writeRequestInformationInServerLog(HttpServletRequest request) {
        getServletContext().log(
                String.format("Method: %s | ", request.getMethod()) +
                String.format("IP-address of the client: %s | ", request.getRemoteAddr()) +
                String.format("User-agent: %s | ", request.getHeader(USER_AGENT_HEADER)) +
                String.format("Client language(s): %s |  ", getClientLanguagesFromRequest(request)) +
                String.format("Parameters: %s", getParametersOfRequest(request))
        );
    }

    private String getClientLanguagesFromRequest(HttpServletRequest request) {
        StringBuilder clientLanguages = new StringBuilder();
        Enumeration<Locale> locales = request.getLocales();
        while (locales.hasMoreElements()) {
            clientLanguages.append(String.format("%s ", locales.nextElement()));
        }
        return clientLanguages.toString();
    }

    private String getParametersOfRequest(HttpServletRequest request) {
        StringBuilder parametersOfTheRequest = new StringBuilder();
        Enumeration<String> parameters = request.getParameterNames();
        while (parameters.hasMoreElements()) {
            String currentParameter = parameters.nextElement();
            parametersOfTheRequest.append(String.format("%s=%s ", currentParameter, request.getParameterValues(currentParameter)[0]));
        }
        return parametersOfTheRequest.toString();
    }

    private boolean isValueValid(int value) {
        return value >= 0;
    }

    private void storeEntryInRepository(RepositoryEntry repositoryEntry, boolean sync) throws IOException {
        synchronized (sync ? repositoryEntries : new Object()) {
            repositoryEntries.add(repositoryEntry);
        }

        synchronized (sync ? outputStream : new Object()) {
            String keyRepeatedNTimes = getStringRepeatedNTimes(repositoryEntry.getKey(), repositoryEntry.getValue());
            outputStream.write(keyRepeatedNTimes.getBytes());
            outputStream.write(String.format(" %s ", SEPARATOR).getBytes());
            outputStream.write(repositoryEntry.getTimestamp().toString().getBytes());
            outputStream.write("\n".getBytes());
            outputStream.flush();
        }
    }

    private boolean isBrowserRequest(HttpServletRequest request) {
        return request.getHeader(USER_AGENT_HEADER).contains(MOZILLA_BROWSER);
    }

    private String getStringRepeatedNTimes(String input, int n) {
        return String.join("", Collections.nCopies(n, input));
    }

    private void showRepositoryContentInHTMLFormat(HttpServletResponse response, boolean sync) throws IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            synchronized (sync ? repositoryEntries : new Object()) {
                out.println("<html><head><title>Repository content</title></head><body>");
                out.println("<ul>");
                for (RepositoryEntry repositoryEntry : repositoryEntries) {
                    String keyRepeatedNTimes = getStringRepeatedNTimes(repositoryEntry.getKey(), repositoryEntry.getValue());
                    out.printf("<li>%s %s %s</li>", keyRepeatedNTimes, SEPARATOR, repositoryEntry.getTimestamp().toString());
                }
                out.println("</ul></body></html>");
            }
        }
    }

    private void showRepositoryContentInSimpleText(HttpServletResponse response, boolean sync) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            synchronized (sync ? repositoryEntries : new Object()) {
                for (RepositoryEntry repositoryEntry : repositoryEntries) {
                    String keyRepeatedNTimes = getStringRepeatedNTimes(repositoryEntry.getKey(), repositoryEntry.getValue());
                    out.printf("%s %s %s\n", keyRepeatedNTimes, SEPARATOR, repositoryEntry.getTimestamp().toString());
                }
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        try {
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            getServletContext().log("Could not close the repository");
        }
    }
}
