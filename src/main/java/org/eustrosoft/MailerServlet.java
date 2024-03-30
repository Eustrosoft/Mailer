package org.eustrosoft;

import com.sun.deploy.net.HttpRequest;
import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.annotation.HttpMethodConstraint;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import static org.eustrosoft.Constants.*;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 10
)
public class MailerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.println(String.format(MSG_METHOD_NOT_SUPPORTED, "GET"));
        resp.setStatus(400);
        writer.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String[] recipients = getRecipients(request.getParameter(PARAM_TO));
        String subject = request.getParameter(PARAM_SUBJECT);
        String message = request.getParameter(PARAM_MESSAGE);
        Properties properties = new Properties();
        properties.setProperty(PROP_MAIL_SMTP_HOST, getContextParam(PROP_MAIL_SMTP_HOST));
        properties.setProperty(PROP_MAIL_SMTP_POST, getContextParam(PROP_MAIL_SMTP_POST));
        properties.setProperty(PROP_MAIL_SMTP_SSL, getContextParam(PROP_MAIL_SMTP_SSL));
        properties.setProperty(PROP_MAIL_SMTP_AUTH, getContextParam(PROP_MAIL_SMTP_AUTH));

        try {
            MailMessage mailMessage = new MailMessage(
                    getContextParam(PROP_MAIL_FROM),
                    recipients,
                    subject,
                    message
            );
            mailMessage.setFilesToSend(getParts(request));
            String user = getContextParam(PROP_MAIL_USER);

            Session session;
            if (user == null || user.isEmpty()) {
                session = Session.getInstance(properties);
            } else {
                session = Session.getInstance(
                        properties,
                        new MailAuthenticator(user, getContextParam(PROP_MAIL_PASSWORD))
                );
            }
            mailMessage.send(mailMessage.buildMessage(session));
            response.getWriter().write(MSG_MAIL_SENT);
        } catch (Exception mex) {
            response.getWriter().write(String.format(MSG_MAIL_SEND_ERROR, mex.getLocalizedMessage()));
        }
        response.getWriter().flush();
    }

    private List<File> getParts(HttpServletRequest request) throws IOException, ServletException {
        Collection<Part> parts = request.getParts();
        File tempDir = Files.createTempDirectory(String.valueOf(System.currentTimeMillis())).toFile();
        List<File> filesToSend = new ArrayList<>();
        for (Part part : parts) {
            if (part.getName().startsWith(FILES_PART_REGEX)) {
                File file = new File(tempDir, part.getSubmittedFileName());
                writeToFile(file, part.getInputStream());
                filesToSend.add(file);
            }
        }
        tempDir.deleteOnExit();
        return filesToSend;
    }

    private void writeToFile(File file, InputStream stream) throws IOException {
        byte[] buffer = new byte[FILE_BYTE_BUFFER_SIZE];
        if (!file.exists()) {
            boolean newFile = file.createNewFile();
            if (!newFile) {
                throw new IOException(String.format(MSG_FILE_CREATION_ERROR, file.getName()));
            }
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            while (stream.read(buffer) > 0) {
                outputStream.write(buffer);
            }
            outputStream.flush();
        }
        try {
            stream.close();
        } catch (Exception ignored) {
        }
    }

    private String[] getRecipients(String to) {
        return to.split(RECIPIENTS_DELIMITER);
    }

    private String getContextParam(String name) {
        return getServletContext().getInitParameter(name);
    }
}
