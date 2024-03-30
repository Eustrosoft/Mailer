package org.eustrosoft;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.eustrosoft.Constants.EMPTY;

public class MailMessage {
    private List<File> filesToSend;
    private final List<String> recipients;
    private final String subject;
    private final String text;
    private final String from;

    public MailMessage(String from, String[] recipients, String subject, String text) {
        this.from = from;
        this.recipients = Arrays.asList(recipients);
        this.subject = getOrDefault(subject, EMPTY);
        this.text = getOrDefault(text, EMPTY);
    }

    public MailMessage(String from, List<String> recipients, String subject, String text) {
        this.from = from;
        this.recipients = recipients;
        this.subject = getOrDefault(subject, EMPTY);
        this.text = getOrDefault(text, EMPTY);
    }

    public MailMessage(String from, List<String> recipients, String subject, String text, List<File> files) {
        this.from = from;
        this.recipients = recipients;
        this.subject = getOrDefault(subject, EMPTY);
        this.text = getOrDefault(text, EMPTY);
        this.filesToSend = files;
    }

    public MimeMessage buildMessage(Session session) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(from);
        message.setSubject(subject);
        message.setRecipients(Message.RecipientType.TO, getRecipientsAddresses());

        Multipart multipart = new MimeMultipart();
        // Set text part
        multipart.addBodyPart(getMessagePart());

        // Set files part
        List<BodyPart> filesParts = getFilesParts();
        if (filesParts.size() > 0) {
            filesParts.forEach(filePart -> {
                try {
                    multipart.addBodyPart(filePart);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
        }

        message.setContent(multipart);
        return message;
    }

    public void send(MimeMessage message) throws MessagingException {
        Transport.send(message);
    }

    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }

    public void addRecipients(List<String> recipients) {
        this.recipients.addAll(recipients);
    }

    public void setFilesToSend(List<File> filesToSend) {
        this.filesToSend = filesToSend;
    }

    private String getOrDefault(String value, String def) {
        if (value == null || value.isEmpty()) {
            return def;
        }
        return value;
    }

    private BodyPart getMessagePart() throws MessagingException {
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText(text);
        return messageBodyPart;
    }

    private List<BodyPart> getFilesParts() {
        if (filesToSend == null || filesToSend.isEmpty()) {
            return new ArrayList<>();
        }
        List<BodyPart> fileParts = new ArrayList<>();
        for (File file : filesToSend) {
            try {
                MimeBodyPart filePart = new MimeBodyPart();
                DataSource source = new FileDataSource(file);
                filePart.setDataHandler(new DataHandler(source));
                filePart.setFileName(file.getName());
                fileParts.add(filePart);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return fileParts;
    }

    private Address[] getRecipientsAddresses() throws AddressException {
        Address[] addresses = new Address[recipients.size()];
        for (int i = 0; i < recipients.size(); i++) {
            addresses[i] = new InternetAddress(recipients.get(i));
        }
        return addresses;
    }
}
